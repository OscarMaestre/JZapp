package io.github.oscarmaestre.jzapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.sqlite.JDBC;

public class Database {
	Connection conn;
	DatabaseMetaData dbMetadata;
	ResultSet catalogs;
	public Database (String filename) throws SQLException{
		JDBC driver=new JDBC();
		DriverManager.registerDriver(driver);
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
	private String toTitleCase(String givenString, String separator) {
		givenString=givenString.replace("_", "");
	    String[] arr = givenString.split(separator);
	    StringBuffer sb = new StringBuffer();

	    for (int i = 0; i < arr.length; i++) {
	        sb.append(Character.toUpperCase(arr[i].charAt(0)))
	            .append(arr[i].substring(1)).append(" ");
	    }          
	    return sb.toString().trim();
	}  
	public String getClassTemplateAsString(String eol) throws IOException{
		InputStream is=this.getClass().getResourceAsStream("ClassTemplate.txt");
		if (is==null){
			return "NULL %s";
		}
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader bfr=new BufferedReader(isr);
		String template="";
		String line="";
		line=bfr.readLine();
		while (line!=null){
			template += line + eol;
			line=bfr.readLine();
		}
		return template;		
	}
	public String getClassTemplateAsString() throws IOException{
		return this.getClassTemplateAsString("\r\n");
	}
	
	private String getFieldsAsConstants(String tableName) throws SQLException{
		String constants="";
		ArrayList<Field<?>> fields=this.getFields(tableName);
		for (Field f:fields){
			constants += f.getConstantDeclaration();
		}
		return constants;
	}
	public String createClass (String tableName) throws SQLException, IOException{
		String template=this.getClassTemplateAsString();
		String classCode;
		
		String constants=this.getFieldsAsConstants(tableName);
		
		classCode=String.format(template, 
				this.toTitleCase(tableName, "_"),
				tableName, constants);
		return classCode;
	}
	public void dump() throws SQLException, IOException{
		ArrayList<String> tableNames=this.getTableNames();
		for (String tableName : tableNames){
			System.out.println("Dumping "+tableName);
			String classCode=this.createClass(tableName);
			FileWriter fw=new FileWriter(this.toTitleCase(tableName, "_")+".java");
			PrintWriter pw=new PrintWriter(fw);
			pw.println(classCode);
			pw.close();
			fw.close();
		}
	}
	public static void main(String[] args) throws SQLException, IOException{
		System.out.println(args[0]);
		Database db=new Database(args[0]);
		db.dump();
	}
}
