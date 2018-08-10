package com.yinhai.xzcode.ui;

import com.intellij.openapi.ui.Messages;
import com.yinhai.xzcode.database.DatabaseHandle;

import javax.swing.*;

/**
 * 配置设置
 * @version 1.0 2018/2/11 0011 下午 2:50
 * @author wangjh
 */
public class CodeConfiguretionForm {
    private JComboBox db_type;
    private JTextField db_name;
    private JTextField db_host;
    private JTextField db_port;
    private JTextField db_username;
    private JTextField db_password;
    private JButton connecttestbtn;
    private JPanel mainPanel;

    public CodeConfiguretionForm(){
        db_type.addActionListener(e -> {
            if(db_type.getSelectedItem()==null){
                return;
            }
            String dbType = db_type.getSelectedItem().toString();
            if(DatabaseHandle.databaseName.oracle.name().equals(dbType)){
                db_port.setText("1521");
            }else if(DatabaseHandle.databaseName.sqlserver.name().equals(dbType)){
                db_port.setText("1433");
            }else if(DatabaseHandle.databaseName.mysql.name().equals(dbType)){
                db_port.setText("3306");
            }
        });
        connecttestbtn.addActionListener(e -> {
            DatabaseHandle handle = new DatabaseHandle();
            handle.init();
            if (handle.getConn()!=null){
                Messages.showMessageDialog(
                        "连接成功",
                        "兴政代码生成器",
                        Messages.getInformationIcon()
                );
            }else{
                Messages.showMessageDialog(
                        "连接失败",
                        "兴政代码生成器",
                        Messages.getInformationIcon()
                );
            }
        });
    }
    public JPanel getMainPanel(){
        return mainPanel;
    }

    public JComboBox getDb_type() {
        return db_type;
    }

    public JTextField getDb_name() {
        return db_name;
    }

    public JTextField getDb_host() {
        return db_host;
    }

    public JTextField getDb_port() {
        return db_port;
    }

    public JTextField getDb_username() {
        return db_username;
    }

    public JTextField getDb_password() {
        return db_password;
    }

}
