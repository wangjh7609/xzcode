package com.yinhai.xzcode.database;

/**
 * mysql数据库查询语句
 * @version 1.0 2018/2/20 0020 下午 2:18
 * @author wangjh
 */
public class MysqlInfo implements DataBaseInfoSql{
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
        String sql = "select table_name,\n" +
                "       table_comment as table_desc\n" +
                "  from information_schema.tables\n" +
                " where TABLE_SCHEMA=database() \n" +
                "   and table_type='BASE TABLE'\n" +
                "   and lower(table_name) like '%"+myName.toLowerCase()+"%'\n" +
                " order by table_name";
        return sql;
    }

    @Override
    public String getColumnInfo(String tableName) {
        if (tableName==null||tableName.length()==0){
            return null;
        }
        String sql = "select column_name as field_name,\n" +
                    "       column_comment as field_desc,\n" +
                    "       data_type as field_type,\n" +
                    "       case when data_type in ('int','bigint','integer','smallint','tinyint','decimal','numeric','mediumint','double','float') \n" +
                    "            then numeric_precision \n" +
                    "       else character_maximum_length \n" +
                    "        end as length,\n" +
                    "       numeric_scale as `precision` \n" +
                    "  from information_schema.columns\n" +
                    " where table_schema=database()\n" +
                    "   and lower(table_name)='"+tableName.toLowerCase()+"'";
        return sql;
    }
}
