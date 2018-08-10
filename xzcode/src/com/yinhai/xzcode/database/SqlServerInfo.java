package com.yinhai.xzcode.database;

/**
 * @auther Administrator 王骄宏
 * @create 2018/2/20 0020 下午 2:18
 * @author wangjh
 */
public class SqlServerInfo implements DataBaseInfoSql{
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
        String sql = "select lower(a.name) as table_name,\n" +
                    "       convert(varchar,b.value) as table_desc \n" +
                    "  from sys.tables a \n" +
                    "  left join  sys.extended_properties b \n" +
                    "    on (a.object_id=b.major_id \n" +
                    "   and b.minor_id=0) \n" +
                    " where lower(a.name) like '%"+myName.toLowerCase()+"%' ESCAPE '\\'" +
                    "   and a.schema_id = SCHEMA_ID('dbo')" +
                    " order by a.name";
        return sql;
    }

    @Override
    public String getColumnInfo(String tableName) {
        if (tableName==null||tableName.length()==0){
            return null;
        }
        String sql = " select a.name as field_name,\n" +
                    "        convert(varchar,b.value) as field_desc,\n" +
                    "        case when TYPE_NAME(a.system_type_id) in ('int','decimal','numeric') then 'number' \n" +
                    "             when TYPE_NAME(a.system_type_id) in ('char','varchar','text') then 'varchar'\n" +
                    "             else TYPE_NAME(a.system_type_id) end as field_type,\n" +
                    "        case when precision>0 then precision else max_length end as length,\n" +
                    "        scale as precision\n" +
                    "  from sys.columns a left join sys.extended_properties b \n" +
                    "    on (a.object_id=b.major_id \n" +
                    "   and a.column_id=b.minor_id) \n" +
                    " where lower(object_name(a.object_id))='"+tableName.toLowerCase()+"'";
        return sql;
    }
}
