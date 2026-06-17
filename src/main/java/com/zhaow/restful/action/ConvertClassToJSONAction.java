package com.zhaow.restful.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import com.zhaow.restful.common.PsiClassHelper;

import java.awt.datatransfer.StringSelection;

public class ConvertClassToJSONAction extends AbstractBaseAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = psiClassAtCaret(e);

        if(psiClass == null) return;

        String json = PsiClassHelper.create(psiClass).convertClassToJSON(myProject(e), true);
        CopyPasteManager.getInstance().setContents(new StringSelection(json));
    }

    @Override
    public void update(AnActionEvent e) {
        setActionPresentationVisible(e, inEditorPopup(e));
    }
}
