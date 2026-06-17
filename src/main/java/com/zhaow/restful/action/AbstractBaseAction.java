package com.zhaow.restful.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import com.zhaow.restful.annotations.JaxrsHttpMethodAnnotation;
import com.zhaow.restful.annotations.SpringControllerAnnotation;
import com.zhaow.restful.annotations.SpringRequestMethodAnnotation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;


public abstract class AbstractBaseAction extends AnAction {

    protected Module myModule(AnActionEvent e) {
        return e.getData(DataKeys.MODULE);
    }

    protected Project myProject(AnActionEvent e) {
        return getEventProject(e);
    }

    @Nullable
    protected PsiElement psiElementAtCaret(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }

    @Nullable
    protected PsiMethod psiMethodAtCaret(AnActionEvent e) {
        PsiElement element = psiElementAtCaret(e);
        return element == null ? null : PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    }

    @Nullable
    protected PsiClass psiClassAtCaret(AnActionEvent e) {
        PsiElement element = psiElementAtCaret(e);
        return element == null ? null : PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

    /**
     * 取光标所在方法，并校验它是 REST 端点（标注了 Spring/JAX-RS 请求映射注解，
     * 或所在类是 @RestController/@Controller）。
     * update() 无法做精确过滤（data context 不可靠），所以语义校验放在这里，
     * 由 actionPerformed 调用；非 REST 方法返回 null，调用方 early return。
     */
    @Nullable
    protected PsiMethod restMethodAtCaret(AnActionEvent e) {
        PsiMethod psiMethod = psiMethodAtCaret(e);
        if (psiMethod == null) {
            return null;
        }
        PsiModifierList modifiers = psiMethod.getModifierList();
        if (modifiers != null) {
            for (PsiAnnotation annotation : modifiers.getAnnotations()) {
                String qn = annotation.getQualifiedName();
                if (qn == null) {
                    continue;
                }
                boolean restMapping = Arrays.stream(SpringRequestMethodAnnotation.values())
                        .anyMatch(a -> a.getQualifiedName().equals(qn))
                        || Arrays.stream(JaxrsHttpMethodAnnotation.values())
                        .anyMatch(a -> a.getQualifiedName().equals(qn));
                if (restMapping) {
                    return psiMethod;
                }
            }
        }
        return isRestController(psiMethod.getContainingClass()) ? psiMethod : null;
    }

    private boolean isRestController(PsiClass containingClass) {
        if (containingClass == null) {
            return false;
        }
        PsiModifierList modifierList = containingClass.getModifierList();
        if (modifierList == null) {
            return false;
        }
        return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null
                || modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null;
    }

    /**
     * 判断 update() 是否在编辑器右键菜单上下文中被调用。
     * 用 e.getPlace() 判断，不依赖 data context（EDITOR/PSI_FILE 在 update() 里不可靠）；
     * 真正的位置校验由 actionPerformed 内部完成。
     */
    protected boolean inEditorPopup(AnActionEvent e) {
        return ActionPlaces.EDITOR_POPUP.equals(e.getPlace());
    }

    /**
     * 设置触发有效条件
     *
     * @param e
     * @param visible
     */
    protected void setActionPresentationVisible(AnActionEvent e, boolean visible) {
        e.getPresentation().setVisible(visible);
    }

    protected void showPopupBalloon(final String result, final Editor myEditor) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                        .setFadeoutTime(1000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(myEditor), Balloon.Position.atRight);
            }
        });
    }

}
