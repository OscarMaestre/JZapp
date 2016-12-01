package io.github.oscarmaestre.jzapp;

import java.lang.reflect.ParameterizedType;
import java.sql.Types;
import java.util.Date;

public class Field<T> {
	private String fieldName;
	private T value;
	public Field(String name) {
		super();
		this.fieldName = name;
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
		}
		return null;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String toString(){
		String str="";
		ParameterizedType p;
		p =this.getClass().getp
		//return str;
	}
}
