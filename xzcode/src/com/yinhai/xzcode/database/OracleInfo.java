package com.yinhai.xzcode.database;

/**
 * oracle数据库查询语句
 * @version 1.0 2018/2/20 0020 下午 2:18
 * @author wangjh
 */
public class OracleInfo implements DataBaseInfoSql{
    /**
     * 获取数据库中的表信息
     * @param tableName
     * @return
     */
    @Override
    public String getTableInfo(String tableName) {
        String myName = "";
        if (tableName!=null){
            myName = tableName;
        }
        String sql = "SELECT lower(table_name) AS \"table_name\",\n" +
                    "       comments AS \"table_desc\" \n" +
                    "  FROM User_Tab_Comments \n" +
                    " WHERE table_type='TABLE'\n" +
                    "   AND table_name LIKE '%"+myName.toUpperCase()+"%'\n" +
                    " ORDER BY table_name";
        return sql;
    }

    @Override
    public String getColumnInfo(String tableName) {
        if (tableName==null||tableName.length()==0){
            return null;
        }
        String sql = " SELECT LOWER(a.column_name) AS \"field_name\",\n" +
                "        b.comments AS \"field_desc\",\n" +
                "        DECODE(a.DATA_TYPE,'VARCHAR2','varchar','CHAR','varchar','CLOB','varchar',LOWER(a.DATA_TYPE)) AS \"field_type\",\n" +
                "        DECODE(a.DATA_TYPE,'NUMBER',a.data_precision,a.DATA_LENGTH) AS \"length\",\n" +
                "        a.DATA_SCALE AS \"precision\"\n" +
                "   FROM cols a,\n" +
                "        User_Col_Comments b\n" +
                "  WHERE a.TABLE_NAME=b.table_name\n" +
                "    AND a.COLUMN_NAME=b.column_name\n" +
                "    AND a.TABLE_NAME='"+tableName.toUpperCase()+"'";
        return sql;
    }
}
