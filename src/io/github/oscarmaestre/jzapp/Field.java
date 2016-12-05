package io.github.oscarmaestre.jzapp;


import java.sql.Types;
import java.util.Date;

public class Field<T> {
	private String fieldName;
	private T value;
	private String prefix="";
	public Field(String name) {
		super();
		this.fieldName = name;
	}
	public void setNamePrefix(String prefix){
		this.prefix=prefix;
	}
	
	public static Field<?> fieldFactory(String name, int type){
		switch(type){
			case Types.INTEGER:{
				return new Field<Integer>(name);
			}
			case Types.VARCHAR:{
				return new Field<String>(name);
			}
			case Types.DATE: {
				return new Field<Date>(name);
			}
			case Types.BOOLEAN:{
				return new Field<Boolean>(name);
			}
		}
		System.err.println("Unknown type for "+name+" with type "+type + " in name "+name+". Returning String.");
		return new Field<String>(name);
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public String getConstantDeclaration(){
		String fieldTemplate="\tpublic static final String %s=\"%s\";\r\n";
		String nameOfConstant=prefix+fieldName;
		String capitalizedName=nameOfConstant.toUpperCase();
		String fieldDeclaration=String.format(fieldTemplate, capitalizedName,fieldName);
		return fieldDeclaration;
	}
	
	public String getFieldName() {
		return fieldName;
	}

}
