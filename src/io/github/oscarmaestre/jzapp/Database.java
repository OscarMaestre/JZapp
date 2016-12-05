package io.github.oscarmaestre.jzapp;

import java.io.BufferedReader;
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

import org.sqlite.JDBC;


public class Database {
	Connection conn;
	DatabaseMetaData dbMetadata;
	ResultSet catalogs;
	protected final String SINGLE_CLASS_TEMPLATE = "ClassTemplate.txt";
	protected final String CONSTANTS_CLASS_TEMPLATE = "ConstantsTemplate.txt";
	protected final String EOL = System.getProperty("line.separator");
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
	public String getClassTemplateAsString(String file) throws IOException{
		InputStream is=this.getClass().getResourceAsStream(file);
		if (is==null){
			return "NULL %s";
		}
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader bfr=new BufferedReader(isr);
		String template="";
		String line="";
		line=bfr.readLine();
		while (line!=null){
			template += line + EOL;
			line=bfr.readLine();
		}
		return template;		
	}
	public String getClassTemplateAsString() throws IOException{
		return this.getClassTemplateAsString(this.CONSTANTS_CLASS_TEMPLATE);
	}
	
	private String getFieldsAsConstants(String tableName) throws SQLException{
		String constants="";
		ArrayList<Field<?>> fields=this.getFields(tableName);
		for (Field f:fields){
			constants += f.getConstantDeclaration();
		}
		return constants;
	}
	private String getFieldsAsConstantsPrefixedWithTableName(String tableName) throws SQLException{
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
	public void dumpClassesFiles() throws SQLException, IOException{
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
	public void dumpSingleClass(String packageName,
			String className) throws SQLException, IOException{
		
		ArrayList<String> tableNames=this.getTableNames();
		String constants="";
		for (String tableName : tableNames){
			ArrayList<Field<?>> fields=this.getFields(tableName);
			constants+="\t/* Constants for table "+tableName+"*/"+EOL;
			constants+="\tpublic static final String TABLE_"+tableName.toUpperCase() +
					"=\""+tableName+"\""+EOL;
			for (Field<?> f: fields){
				f.setNamePrefix(tableName.toUpperCase()+"_");
				constants+=f.getConstantDeclaration();
			}
			constants+="\t/* End of constants for table "+tableName+"/*"+EOL+EOL;
		}
		String template=this.getClassTemplateAsString(this.CONSTANTS_CLASS_TEMPLATE);
		String classCode;
		
		classCode=String.format(template, packageName, className, constants);
		
		System.out.println("Dumping "+className+".java");
		FileWriter fw=new FileWriter(className+".java");
		PrintWriter pw=new PrintWriter(fw);
		pw.println(classCode);
		pw.close();
		fw.close();
	}
	
	public ResultSet getForeignKeys() throws SQLException{
		DatabaseMetaData dbMetadata=this.conn.getMetaData();
		ArrayList<String> tableNames=this.getTableNames();
		ResultSet foreignKeys=null;
		for (String tableName : tableNames){
			System.out.println("Checking FK in :"+tableName);
			foreignKeys=dbMetadata.getExportedKeys("", "", tableName);
			while (foreignKeys.next()){
				String tableReferenced=foreignKeys.getString("PKTABLE_NAME");
				String fieldReferenced=foreignKeys.getString("PKCOLUMN_NAME");
				
				String tableWhichReferences=foreignKeys.getString("FKTABLE_NAME");
				String fieldWhichReferences=foreignKeys.getString("PKCOLUMN_NAME");
				String cad="%s(%s)==>%s(%s)";
				String msg=String.format(cad, 
						tableWhichReferences,fieldWhichReferences, 
						tableReferenced,fieldReferenced);
				System.out.println(msg);
			}
			
		}
		return foreignKeys;
		
	}
	public static void main(String[] args) throws SQLException, IOException{
		System.out.println(args[0]);
		Database db=new Database(args[0]);
		db.dumpClassesFiles();
		db.dumpSingleClass(args[1], "Constantes");
	}
}
