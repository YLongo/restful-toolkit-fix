package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.zhaow.restful.action.AbstractBaseAction;

/**
 * Restful method （restful 方法添加方法 ）
 */
public abstract class SpringAnnotatedMethodAction extends AbstractBaseAction {

    @Override
    public void update(AnActionEvent e) {
        setActionPresentationVisible(e, inEditorPopup(e));
    }

}
