package com.yinhai.xzcode.component;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.Messages;
import com.yinhai.xzcode.database.DatabaseHandle;
import com.yinhai.xzcode.ui.CodeConfiguretionForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 数据库连接配置组件，用于配置信息的持久化
 * @version  2018/5/14 0014 下午 2:53
 * @author wangjh
 */
public class CodeSettingComponent implements SearchableConfigurable{

    private CodeConfiguretionForm codeConfiguretionForm;

    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    @NotNull
    @Override
    public String getId() {
        return "com.yinhai.xzcode";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "兴政代码生成器配置";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (codeConfiguretionForm ==null) {
            codeConfiguretionForm = new CodeConfiguretionForm();
        }
        return codeConfiguretionForm.getMainPanel();
    }

    @Override
    public boolean isModified() {
        if (!codeConfiguretionForm.getDb_host().getText().equals(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_HOST))){
            return true;
        }
        if (!codeConfiguretionForm.getDb_type().getSelectedItem().toString().equals(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_TYPE))){
            return true;
        }
        if (!codeConfiguretionForm.getDb_name().getText().equals(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_NAME))){
            return true;
        }
        if (!codeConfiguretionForm.getDb_port().getText().equals(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_PORT))){
            return true;
        }
        if (!codeConfiguretionForm.getDb_username().getText().equals(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_USERNAME))){
            return true;
        }
        if (!codeConfiguretionForm.getDb_password().getText().equals(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_PASSWORD))){
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        propertiesComponent.setValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_HOST, codeConfiguretionForm.getDb_host().getText());
        propertiesComponent.setValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_NAME, codeConfiguretionForm.getDb_name().getText());
        propertiesComponent.setValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_PASSWORD, codeConfiguretionForm.getDb_password().getText());
        propertiesComponent.setValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_PORT, codeConfiguretionForm.getDb_port().getText());
        propertiesComponent.setValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_TYPE, codeConfiguretionForm.getDb_type().getSelectedItem().toString());
        propertiesComponent.setValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_USERNAME, codeConfiguretionForm.getDb_username().getText());
        Messages.showMessageDialog(
                "保存成功",
                "兴政代码生成器",
                Messages.getInformationIcon()
        );
    }

    @Override
    public void reset() {
        codeConfiguretionForm.getDb_host().setText(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_HOST));
        codeConfiguretionForm.getDb_name().setText(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_NAME));
        codeConfiguretionForm.getDb_type().setSelectedItem(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_TYPE));
        codeConfiguretionForm.getDb_port().setText(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_PORT));
        codeConfiguretionForm.getDb_username().setText(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_USERNAME));
        codeConfiguretionForm.getDb_password().setText(propertiesComponent.getValue(DatabaseHandle.XZCODE_SAVE_NAME_DB_PASSWORD));
    }

    @Override
    public void disposeUIResources() {
        codeConfiguretionForm = null;
    }
}
