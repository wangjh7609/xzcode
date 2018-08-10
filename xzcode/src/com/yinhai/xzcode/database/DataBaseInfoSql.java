package com.yinhai.xzcode.database;

/**
 * 数据查询SQL语句生成接口，不同的数据库只需要实现此接口
 * @version 2018-5-17
 * @author wangjh
 */
public interface DataBaseInfoSql {
    /**
     * 查询当前连接用户下的表信息，支持模糊查询
     * @param tableName 要查询的表名
     * @return 返回对应的SQL语句
     */
    String getTableInfo(String tableName);

    /**
     * 表字段信息查询方法
     * @param tableName 表名
     * @return 对应的SQL语句
     */
    String getColumnInfo(String tableName);
}
