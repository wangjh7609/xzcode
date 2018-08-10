package com.yinhai.xzcode.database;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.text.StringUtil;
import com.yinhai.xzcode.entity.DbFieldInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther Administrator 王骄宏
 * @create 2018/2/20 0020 下午 3:41
 * @author wangjh
 */
public class DatabaseHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHandle.class);
    /** 以下常量为idea全局持久化保存的key名称 */
    public static final String XZCODE_SAVE_NAME_DB_HOST = "xzcode.db_host";
    public static final String XZCODE_SAVE_NAME_DB_TYPE = "xzcode.db_type";
    public static final String XZCODE_SAVE_NAME_DB_NAME = "xzcode.db_name";
    public static final String XZCODE_SAVE_NAME_DB_PORT = "xzcode.db_port";
    public static final String XZCODE_SAVE_NAME_DB_USERNAME = "xzcode.db_username";
    public static final String XZCODE_SAVE_NAME_DB_PASSWORD = "xzcode.db_password";
    /** SQLserver数据库 */
    public static final String SQLSERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /** mysql数据库 */
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    /** oracle数据库 */
    public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    /** jsp页面保存的项目路径 */
    public static final String JSP_ROOT_PATH = "/webapp/WEB-INF/page/";
    /** 控制器类名后辍 */
    public static final String SUFFIX_CONTROLLER = "Controller";
    /** 业务处理类名后辍 */
    public static final String SUFFIX_SERVICE = "Service";
    /** 实体类名后辍 */
    public static final String SUFFIX_ENTITY = "Bean";
    /** 点符号 */
    private static final char DOT = '.';

    private Connection conn;

    private DataBaseInfoSql sql ;

    /** 表描述缓存，key为表名，value为表描述 */
    private static ConcurrentHashMap<String,String> tables = new ConcurrentHashMap();

    /**
     * 初始化数据库连接
     * @return 是否成功
     */
    public Boolean init(){
        try {
            if (conn!=null&&!conn.isClosed()){
                return true;
            }
            PropertiesComponent propertiesComponent = ServiceManager.getService(PropertiesComponent.class);
            String dbType = propertiesComponent.getValue(XZCODE_SAVE_NAME_DB_TYPE);
            String url = propertiesComponent.getValue(XZCODE_SAVE_NAME_DB_HOST);
            String port = propertiesComponent.getValue(XZCODE_SAVE_NAME_DB_PORT);
            String dbName = propertiesComponent.getValue(XZCODE_SAVE_NAME_DB_NAME);
            String userName = propertiesComponent.getValue(XZCODE_SAVE_NAME_DB_USERNAME);
            String password = propertiesComponent.getValue(XZCODE_SAVE_NAME_DB_PASSWORD);
            if (conn==null||conn.isClosed()) {
                if (databaseName.sqlserver.name().equals(dbType)) {
                    try {
                        String myurl = "jdbc:sqlserver://"+url+":"+port+";SelectMethod=cursor;databaseName="+dbName+";integratedSecurity=false";
                        Class.forName(SQLSERVER_DRIVER);
                        conn = DriverManager.getConnection(myurl, userName, password);
                        if (sql == null || !(sql instanceof SqlServerInfo)) {
                            sql = new SqlServerInfo();
                        }
                        return true;
                    } catch (Exception e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                    }
                }else if(databaseName.mysql.name().equals(dbType)){
                    try {
                        String myurl = "jdbc:mysql://"+url+":"+port+"/"+dbName+"?useServerPrepStmts=false&useUnicode=true&characterEncoding=utf8";
                        Class.forName(MYSQL_DRIVER);
                        conn = DriverManager.getConnection(myurl, userName, password);
                        if (sql == null || !(sql instanceof MysqlInfo)) {
                            sql = new MysqlInfo();
                        }
                        return true;
                    } catch (Exception e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                    }
                }else if(databaseName.oracle.name().equals(dbType)){
                    try {
                        String myurl = "jdbc:oracle:thin:@//"+url+":"+port+"/"+dbName;
                        Class.forName(ORACLE_DRIVER);
                        conn = DriverManager.getConnection(myurl, userName, password);
                        if (sql == null || !(sql instanceof OracleInfo)) {
                            sql = new OracleInfo();
                        }
                        return true;
                    } catch (Exception e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
        return false;
    }

    /**
     * 查询表字段信息
     * @param tableName 表名
     * @return 字段信息
     * @throws SQLException
     */
    public List<DbFieldInfo> query(String tableName) throws SQLException {
        PreparedStatement pst = conn.prepareStatement(sql.getColumnInfo(tableName));
        ResultSet rs = pst.executeQuery();
        List<DbFieldInfo> list = new ArrayList<>();
        DbFieldInfo dbFieldInfo;
        while (rs.next()){
            dbFieldInfo = new DbFieldInfo();
            dbFieldInfo.setFieldName(rs.getString("field_name"));
            dbFieldInfo.setFieldDesc(rs.getString("field_desc"));
            dbFieldInfo.setFieldType(rs.getString("field_type"));
            dbFieldInfo.setLength(rs.getInt("length"));
            dbFieldInfo.setPrecision(rs.getInt("precision"));
            list.add(dbFieldInfo);
        }
        return list;
    }

    /**
     * 查询所有表信息
     * @param tableName 表名查询条件
     * @return 符合条件的表信息
     * @throws SQLException
     */
    public List<Map<String,String>> queryTable(String tableName) throws SQLException{
        PreparedStatement pst = conn.prepareStatement(sql.getTableInfo(tableName));
        ResultSet rs = pst.executeQuery();
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> tableInfo;
        while (rs.next()){
            tableInfo = new HashMap<>(2);
            tableInfo.put("tableName",rs.getString("table_name"));
            tableInfo.put("tableDesc",rs.getString("table_desc"));
            tables.put(tableInfo.get("tableName"),""+tableInfo.get("tableDesc"));
            list.add(tableInfo);
        }
        return list;
    }

    /**
     * 将表名转化为大驼峰类名
     * @param tableName 表名
     * @return 类名
     */
    public String makeClassName(String tableName){
        String[] b = tableName.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<b.length;i++){
            sb.append(StringUtil.capitalizeWithJavaBeanConvention(b[i]));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的空白字符，用于代码补齐
     * @param length 长度
     * @return 空白字符
     */
    public String makeBlankStr(int length){
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<length;i++){
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * 返回数据库连接，用于配置页面检查是否连接成功
     * @return 数据库连接
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * 重置数据库连接
     * @param conn 数据库连接
     */
    public void setConn(Connection conn){
        if (conn==null&&this.conn!=null){
            try {
                this.conn.close();
            } catch (SQLException e) {
                LOGGER.error(e.getLocalizedMessage(),e);
            }
        }
        this.conn = conn;
    }

    /**
     * 将数据库表放描述信息放入缓存中
     * @param tableName 表名
     * @param tableDesc 表描述
     */
    public void putTableDesc(String tableName,String tableDesc){
        tables.put(tableName,tableDesc);
    }

    /**
     * 通过表名获取表描述
     * @param tableName 表名
     * @return 表描述
     */
    public String castTableDescFromName(String tableName){
        return tables.get(tableName);
    }

    /**
     * 从完整路径中获取包路径
     * @param fullPackage 完整路径，即全限定名
     * @return 包名
     */
    public String makePackagePath(String fullPackage){
        if (fullPackage.indexOf(DOT)>-1){
            return fullPackage.substring(0,fullPackage.lastIndexOf(DOT));
        }else{
            return "";
        }
    }
    public String makeFileName(String fullPackage){
        if (fullPackage.indexOf(DOT)>-1){
            return fullPackage.substring(fullPackage.lastIndexOf(DOT)+1);
        }else{
            return fullPackage;
        }
    }

    /**
     * 数据库名称枚举
     */
    public enum databaseName{
        /** mysql数据库 */
        mysql,
        /** sqlserver数据库 */
        sqlserver,
        /** oracle数据库 */
        oracle
    }

}
