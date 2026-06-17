package com.zhaow.restful.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import com.zhaow.restful.common.PsiClassHelper;

import java.awt.datatransfer.StringSelection;

public class ConvertClassToJSONCompressedAction extends ConvertClassToJSONAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        PsiClass psiClass = psiClassAtCaret(e);

        if(psiClass == null) return;

        String json = PsiClassHelper.create(psiClass).convertClassToJSON(myProject(e), false);
        CopyPasteManager.getInstance().setContents(new StringSelection(json));
    }
}
