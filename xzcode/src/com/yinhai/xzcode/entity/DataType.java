package com.yinhai.xzcode.entity;

/**
 * 内部数据类型字段，如果没有指定将使用其id作为默认字段名
 * @version 1.0 2018-2-20
 * @author wangjh
 */
public enum DataType {

    CREATE_BY("create_by","创建人"),
    UPDATE_BY("update_by","更新人"),
    CREATE_TIME("create_time","创建时间"),
    UPDATE_TIME("update_time","更新时间"),
    VALID_FLAG("valid_flag","有效标识");
    private String id;
    private String name;

    DataType(String id,String name){
        this.id = id;
        this.name = name;
    }
}
