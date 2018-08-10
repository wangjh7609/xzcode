package com.yinhai.xzcode.entity;

/**
 * @auther Administrator 王骄宏
 * @create 2018/2/20 0020 下午 1:51
 * @author wangjh
 */
public class DbFieldInfo {
    /** 字段名 */
    private String fieldName;
    /** 字段描述 */
    private String fieldDesc;
    /** 字段类型varchar/number/date/datetime */
    private String fieldType;
    /** 字段长度 */
    private Integer length;
    /** 字段精度 */
    private Integer precision;
    /** 码表编码名 */
    private String codeName;
    /** 是否主键字段 */
    private Boolean pk;
    /** 是否内部字段 */
    private Boolean inner;
    /** 内部数据类型 当inner值为true时，其值才有意义*/
    private DataType dataType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public Boolean getPk() {
        return pk;
    }

    public void setPk(Boolean pk) {
        this.pk = pk;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Boolean getInner() {
        return inner;
    }

    public void setInner(Boolean inner) {
        this.inner = inner;
    }
}
