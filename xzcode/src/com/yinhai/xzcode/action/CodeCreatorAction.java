package com.yinhai.xzcode.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.yinhai.xzcode.component.CodeCreatorComponent;

/**
 * 兴政代码自动生成插件主控制action
 * @version 1.0 2018-5-14
 * @author wangjh
 */
public class CodeCreatorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        CodeCreatorComponent component = ApplicationManager.getApplication().getComponent(CodeCreatorComponent.class);
        component.showPanel(e);
    }
}
