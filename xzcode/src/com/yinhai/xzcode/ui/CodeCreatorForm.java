package com.yinhai.xzcode.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.yinhai.xzcode.component.CodeCreatorComponent;
import com.yinhai.xzcode.database.DatabaseHandle;
import com.yinhai.xzcode.entity.DbFieldInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 兴政代码生成工具主界面GUI
 * @version 1.0 2018/5/14 0014 下午 5:50
 * @author wangjh
 */
public class CodeCreatorForm {

    private JPanel createrPanel;
    private JComboBox tableName;
    private JTextField tableNameField;
    private JLabel tableNameLabel;
    private JButton tableQuery;
    private JButton codeOkButton;
    private JPanel fileConfigPanel;
    private JPanel tableSelectPanel;
    private JPanel configInfoPanel;
    private JPanel buttonPanel;
    private JTextField controllerPath;
    private JTextField xmlPath;
    private JTextField servicePath;
    private JTextField beanPath;
    private JTextField jspPath;
    private JTable fieldTable;
    private JScrollPane selectFieldPanel;
    private JTextField my_table_desc;
    private JTextField my_table_name;
    private JTextField validFlagField;
    private JTextField validFlagFieldValue;
    private JTextField validFlagFieldNoValue;
    private JTextField sequenceNameField;
    private DatabaseHandle handle ;
    private CodeCreatorComponent codeCreatorComponent;

    public CodeCreatorForm(){
        tableQuery.addActionListener(e -> {
            handle.setConn(null);
            if(!handle.init()){
                Messages.showMessageDialog("数据库连接失败！","兴政代码生成工具",Messages.getInformationIcon());
                return;
            }
            createComboBox();
        });
        tableName.addActionListener(e -> {
            handle.init();
            String tableNameValue = ""+tableName.getSelectedItem();
            initPath(tableNameValue);
            createUIComponents(tableNameValue);
        });
        codeOkButton.addActionListener(e -> {
            if(StringUtil.isEmpty(my_table_name.getText())){
                Messages.showMessageDialog("请先选择要生成代码的表","兴政代码生成工具",Messages.getInformationIcon());
                return;
            }
            handle.init();
            createCodeFile();
        });
    }

    /**
     * 创建界面上的表格控件
     * @param tableName 表名
     */
    private void createUIComponents(String tableName) {
        fieldTable.removeAll();
        String[] header = {"选择","字段名称","字段描述","字段类型","字段长度","精度","是否ID字段","是否条件","编码名称"};
        try {
            List<DbFieldInfo> tableList = handle.query(tableName);
            Object[][] dataRow = new Object[tableList.size()][9];
            DbFieldInfo info;
            for (int i = 0 ;i<tableList.size();i++){
                info = tableList.get(i);
                dataRow[i][0] = Boolean.TRUE;
                dataRow[i][1] = info.getFieldName();
                dataRow[i][2] = info.getFieldDesc();
                dataRow[i][3] = info.getFieldType();
                dataRow[i][4] = info.getLength();
                dataRow[i][5] = info.getPrecision();
                dataRow[i][6] = info.getPk();
                dataRow[i][7] = Boolean.TRUE;
                dataRow[i][8] = "";
            }
            DefaultTableModel model = new DefaultTableModel(dataRow,header){
                /** 对应列是否可编辑 */
                private boolean[] editors = {true,false,true,false,true,false,true,true,true};
                @Override
                public boolean isCellEditable(int row, int col)
                {
                    return editors[col];
                }
            };
            fieldTable.setModel(model);
            //首列为复选框
            TableColumn tc0 = fieldTable.getColumnModel().getColumn(0);
            tc0.setCellEditor(fieldTable.getDefaultEditor(Boolean.class));
            tc0.setCellRenderer(fieldTable.getDefaultRenderer(Boolean.class));
            //第7列，是否ID字段为复选框
            TableColumn tc6 = fieldTable.getColumnModel().getColumn(6);
            tc6.setCellEditor(fieldTable.getDefaultEditor(Boolean.class));
            tc6.setCellRenderer(fieldTable.getDefaultRenderer(Boolean.class));
            //第8列，是否查询条件为复选框
            TableColumn tc7 = fieldTable.getColumnModel().getColumn(7);
            tc7.setCellEditor(fieldTable.getDefaultEditor(Boolean.class));
            tc7.setCellRenderer(fieldTable.getDefaultRenderer(Boolean.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建下拉选项控件
     */
    private void createComboBox(){
        tableName.removeAllItems();
        String tableNameValue = tableNameField.getText();
        if (tableNameValue==null){
            tableNameValue = "";
        }
        try {
            DatabaseHandle handle = new DatabaseHandle();
            handle.init();
            List<Map<String,String>> tableList = handle.queryTable(tableNameValue);
            for (Map<String,String> info : tableList){
                tableName.addItem(info.get("tableName"));
            }
            if (tableList!=null&&tableList.size()==1){
                createUIComponents(tableList.get(0).get("tableName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取主界面
     * @return 主界面
     */
    public JPanel getCreaterPanel(){
        return createrPanel;
    }

    /**
     * 生成代码文件
     */
    private void createCodeFile(){
        Map<String,Object> data = makeData();
        Boolean myHasId = (Boolean)data.get("myHasId");
        //判断是否选择了ID字段
        if (!myHasId){
            Messages.showMessageDialog("请至少选择一个ID字段！","兴政代码生成插件",Messages.getInformationIcon());
            return;
        }
        makeEntityFile(data);
        makeXmlFile(data);
        makeServiceFile(data);
        makeJspFile(data);
        makeControllerFile(data);
    }

    /**
     * 生成xml文件
     * @param data 用于生成的数据
     */
    private void makeXmlFile(Map<String,Object> data){
        String xmlPackageValue = xmlPath.getText();
        if (xmlPackageValue==null||xmlPackageValue.length()==0){
            return;
        }
        String xmlFileName = handle.makeFileName(xmlPackageValue) ;
        String xmlPathValue = handle.makePackagePath(xmlPackageValue);
        xmlPathValue = xmlPathValue.replace('.','/');
        xmlPathValue = codeCreatorComponent.makeRootPath(xmlPathValue).getPath()+"/" + xmlPathValue +"/" + xmlFileName +".xml";
        data.put("xmlNameSpace",xmlPackageValue);
        codeCreatorComponent.createCode("xml.vm",data,xmlPathValue);
    }

    /**
     * 生成service接口类和实现类文件
     * @param data 用于生成的数据
     */
    private void makeServiceFile(Map<String,Object> data){
        String servicePackageValue = servicePath.getText();
        if (servicePackageValue==null||servicePackageValue.length()==0){
            return;
        }
        String serviceFileName = handle.makeFileName(servicePackageValue);
        String servicePathValue = handle.makePackagePath(servicePackageValue);
        servicePathValue = servicePathValue.replace('.','/');
        data.put("servicePackageName",handle.makePackagePath(servicePackageValue));
        if (serviceFileName.endsWith(DatabaseHandle.SUFFIX_SERVICE)){
            serviceFileName = serviceFileName.substring(0,serviceFileName.length()-DatabaseHandle.SUFFIX_SERVICE.length());
        }
        String serviceImplPathValue = codeCreatorComponent.makeRootPath(servicePathValue).getPath() + "/" + servicePathValue + "/impl/" + serviceFileName + "ServiceImpl.java";
        servicePathValue = codeCreatorComponent.makeRootPath(servicePathValue).getPath() + "/" + servicePathValue + "/" + serviceFileName + DatabaseHandle.SUFFIX_SERVICE + ".java";
        data.put("serviceClassName",serviceFileName);
        data.put("serviceClassName_lower",StringUtil.decapitalize(serviceFileName));
        codeCreatorComponent.createCode("service.vm",data,servicePathValue);
        codeCreatorComponent.createCode("service_impl.vm",data,serviceImplPathValue);
    }

    /**
     * 生成控制器controller类文件
     * @param data 用于生成的数据
     */
    private void makeControllerFile(Map<String,Object> data){
        String controllerPackageValue = controllerPath.getText();
        if (controllerPackageValue==null||controllerPackageValue.length()==0){
            return;
        }
        String controllerFileName = handle.makeFileName(controllerPackageValue);
        String controllerPathValue = handle.makePackagePath(controllerPackageValue);
        controllerPathValue = controllerPathValue.replace('.','/');
        data.put("controllerPackageName",handle.makePackagePath(controllerPackageValue));
        if (controllerFileName.endsWith(DatabaseHandle.SUFFIX_CONTROLLER)){
            controllerFileName = controllerFileName.substring(0,controllerFileName.length()-DatabaseHandle.SUFFIX_CONTROLLER.length());
        }
        controllerPathValue = codeCreatorComponent.makeRootPath(controllerPathValue).getPath() + "/" + controllerPathValue + "/" + controllerFileName + DatabaseHandle.SUFFIX_CONTROLLER + ".java";
        data.put("controllerClassName",controllerFileName);
        data.put("controllerClassName_lower",StringUtil.decapitalize(controllerFileName));
        codeCreatorComponent.createCode("controller.vm",data,controllerPathValue);
    }

    /**
     * 生成java实体类文件
     * @param data 用于生成的数据
     */
    private void makeEntityFile(Map<String,Object> data){
        String entityPackageValue = beanPath.getText();
        if (entityPackageValue==null||entityPackageValue.length()==0){
            return;
        }
        String entityFileName = handle.makeFileName(entityPackageValue);
        String entityPathValue = handle.makePackagePath(entityPackageValue);
        entityPathValue = entityPathValue.replace('.','/');
        data.put("entityPackageName",handle.makePackagePath(entityPackageValue));
        if (entityFileName.endsWith(DatabaseHandle.SUFFIX_ENTITY)){
            entityFileName = entityFileName.substring(0,entityFileName.length()-DatabaseHandle.SUFFIX_ENTITY.length());
        }
        entityPathValue = codeCreatorComponent.makeRootPath(entityPathValue).getPath() + "/" + entityPathValue + "/" + entityFileName + DatabaseHandle.SUFFIX_ENTITY + ".java";
        data.put("entityClassName",entityFileName);
        data.put("entityClassName_lower",StringUtil.decapitalize(entityFileName));
        codeCreatorComponent.createCode("entity.vm",data,entityPathValue);
    }

    /**
     * 生成jsp列表和编辑页面文件
     * @param data 用于生成的数据
     */
    private void makeJspFile(Map<String,Object> data){
        String jspPackageValue = jspPath.getText();
        if (jspPackageValue==null||jspPackageValue.length()==0){
            return;
        }
        String tbName = tableName.getSelectedItem().toString();
        String jspPathValue = jspPackageValue;
        jspPathValue = jspPathValue.replace('.','/');
        String jspListPathValue = codeCreatorComponent.getProjectBasePath() + DatabaseHandle.JSP_ROOT_PATH + jspPathValue + "/"+tbName+"_list.jsp";
        String jspEditPathValue = codeCreatorComponent.getProjectBasePath() + DatabaseHandle.JSP_ROOT_PATH + jspPathValue + "/"+tbName+"_edit.jsp";
        data.put("jspPathName",jspPathValue);
        codeCreatorComponent.createCode("jsp_list.vm",data,jspListPathValue);
        codeCreatorComponent.createCode("jsp_edit.vm",data,jspEditPathValue);
    }
    /**
     * 构造模板数据
     * @return 数据
     */
    private Map<String,Object> makeData(){
        Map<String,Object> data =new HashMap<>(32);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tbName = tableName.getSelectedItem().toString();
        handle.putTableDesc(tbName,my_table_desc.getText());
        data.put("sequenceNameField",sequenceNameField.getText());
        data.put("validFlagField",validFlagField.getText());
        data.put("validFlagFieldValue",validFlagFieldValue.getText());
        data.put("validFlagFieldNoValue",validFlagFieldNoValue.getText());
        data.put("tableName",tbName);
        data.put("className",handle.makeClassName(tbName));
        data.put("className_lower",StringUtil.decapitalize(handle.makeClassName(tbName)));
        data.put("tableDesc",handle.castTableDescFromName(tbName));
        data.put("currTime",sdf.format(new Date()));
        data.put("user",System.getProperty("user.name"));
        TableModel model = fieldTable.getModel();
        Integer rows = model.getRowCount();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object>  temp;
        Boolean choice ;
        Boolean myHasId = false;
        for(int i=0;i<rows;i++){
            choice = (Boolean) model.getValueAt(i,0);
            if (!choice){
                continue;
            }
            temp = new HashMap<>(16);
            if (model.getValueAt(i,6)!=null&&(Boolean) model.getValueAt(i,6)) {
                myHasId = (Boolean) model.getValueAt(i, 6);
            }
            temp.put("fieldName",model.getValueAt(i,1));
            temp.put("fieldDesc",model.getValueAt(i,2));
            temp.put("fieldType",model.getValueAt(i,3));
            temp.put("length",model.getValueAt(i,4));
            temp.put("precision",model.getValueAt(i,5));
            temp.put("isPK",model.getValueAt(i,6));
            temp.put("isCondition",model.getValueAt(i,7));
            temp.put("codeName",model.getValueAt(i,8));
            temp.put("blankSpace",handle.makeBlankStr(40-temp.get("fieldName").toString().length()));
            temp.put("blankSpace2",handle.makeBlankStr(60-2*temp.get("fieldName").toString().length()));
            temp.put("fieldName_lower",StringUtil.decapitalize(handle.makeClassName(""+temp.get("fieldName"))));
            temp.put("fieldName_upper",handle.makeClassName(""+temp.get("fieldName")));
            list.add(temp);
        }
        if (myHasId==null){
            myHasId = false;
        }
        data.put("fields",list);
        data.put("myHasId",myHasId);
        return data;
    }

    /**
     * 初始化界面上的表单输入框
     * @param tableName
     */
    public void initPath(String tableName){
        String className = handle.makeClassName(tableName);
        my_table_name.setText(tableName);
        my_table_desc.setText(handle.castTableDescFromName(tableName));
        controllerPath.setText(className+DatabaseHandle.SUFFIX_CONTROLLER);
        xmlPath.setText(className);
        servicePath.setText(className+DatabaseHandle.SUFFIX_SERVICE);
        beanPath.setText(className+DatabaseHandle.SUFFIX_ENTITY);
    }

    /**
     * 设置数据库控制器
     * @param handle 数据库控制器
     */
    public void setHandle(DatabaseHandle handle){
        codeCreatorComponent = ApplicationManager.getApplication().getComponent(CodeCreatorComponent.class);
        this.handle = handle;
    }

}
