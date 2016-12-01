package io.github.oscarmaestre.jzapp;

public class Field {
	private String fieldName;
	private String dbType;
	private String javaType;
	public Field(String name, String dbType, String javaType) {
		super();
		this.fieldName = name;
		this.dbType = dbType;
		this.javaType = javaType;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getDbType() {
		return dbType;
	}
	public String getJavaType() {
		return javaType;
	}
	
}
