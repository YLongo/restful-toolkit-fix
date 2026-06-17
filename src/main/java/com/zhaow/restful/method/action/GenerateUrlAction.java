package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.*;
import com.zhaow.restful.action.AbstractBaseAction;
import com.zhaow.restful.annotations.JaxrsHttpMethodAnnotation;
import com.zhaow.restful.common.PsiMethodHelper;

import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

/**
 * 生成并复制restful url
 * todo: 没考虑RequestMapping 多个值的情况
 */
public class GenerateUrlAction /*extends RestfulMethodSpringSupportedAction*/ extends AbstractBaseAction {
    Editor myEditor;

    @Override
    public void actionPerformed(AnActionEvent e) {

        myEditor = e.getData(CommonDataKeys.EDITOR);
        PsiMethod psiMethod = restMethodAtCaret(e);
        if (psiMethod == null) {
            return;
        }

        //TODO: 需完善 jaxrs 支持
        String servicePath;
        if (isJaxrsRestMethod(psiMethod)) {
            servicePath = PsiMethodHelper.create(psiMethod).buildServiceUriPath();
        } else {
            servicePath = PsiMethodHelper.create(psiMethod).buildServiceUriPathWithParams();
        }

        CopyPasteManager.getInstance().setContents(new StringSelection(servicePath));
        showPopupBalloon("复制成功", myEditor);
    }

    private boolean isJaxrsRestMethod(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(JaxrsHttpMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if (match) {
                return match;
            }
        }

        return false;
    }

    /**
     * spring rest 方法被选中才触发
     *
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        setActionPresentationVisible(e, inEditorPopup(e));
    }

}
