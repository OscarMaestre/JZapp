package io.github.oscarmaestre.jzapp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
