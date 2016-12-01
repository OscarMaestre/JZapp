package io.github.oscarmaestre.jzapp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
	Connection conn;
	DatabaseMetaData dbMetadata;
	ResultSet catalogs;
	public Database (String filename) throws SQLException{
		conn=DriverManager.getConnection("jdbc:sqlite:"+filename);
		dbMetadata=conn.getMetaData();
		catalogs=dbMetadata.getCatalogs();
	}
	public ResultSet getTables() throws SQLException{
		return dbMetadata.getTables(null, null, null, null);
	}
	public ArrayList<String> getTableNames() throws SQLException{
		ArrayList<String> tableNames=new ArrayList<String>();
		ResultSet rs=this.getTables();
		while (rs.next()){
			String temp=rs.getString("TABLE_NAME");
			//System.out.println(temp);
			tableNames.add(temp);
		}
		return tableNames;
	}
	public ArrayList<Field<?>> getFields(String tableName) throws SQLException{
		ArrayList<Field<?>> fields=new ArrayList<Field<?>>();
		String sql="select * from %s";
		
		Statement st=conn.createStatement();
		ResultSet results=st.executeQuery(String.format(sql, tableName));
		results.next();
		ResultSetMetaData metadata=results.getMetaData();
		int columns=metadata.getColumnCount();
		
		for (int i=1; i<=columns;i++){
			
			String fieldName=metadata.getColumnName(i);
			int columnType=metadata.getColumnType(i);
			Field<?> f=Field.fieldFactory(fieldName, columnType);
			fields.add(f);
		}
		return fields;
	}
	public String createClass ( String tableName, String endLine ) throws SQLException{
		String txt="";
		txt+="public class %s {" + endLine;
		txt+="\t %s" + endLine + endLine;
		txt+="}";
		ArrayList<Field<?>> fields=this.getFields(tableName);
		for (Field f: fields){
			
		}
		txt=String.format(txt, tableName, "attributes");
		return txt;
	}
	public String createClass (String tableName) throws SQLException{
		String endLine="\n";
		String txt=this.createClass(tableName, endLine);
		return txt;
	}
}
