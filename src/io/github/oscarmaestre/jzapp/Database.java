package io.github.oscarmaestre.jzapp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
			System.out.println(temp);
			tableNames.add(temp);
		}
		return tableNames;
	}
	
	public String createClass ( String tableName, String endLine ){
		String txt="";
		txt+="public class %s {" + endLine;
		txt+="\t %s" + endLine + endLine;
		txt+="}";
		txt=String.format(txt, tableName, "attributes");
		return txt;
	}
	public String createClass (String tableName){
		String endLine="\n";
		String txt=this.createClass(tableName, endLine);
		return txt;
	}
}
