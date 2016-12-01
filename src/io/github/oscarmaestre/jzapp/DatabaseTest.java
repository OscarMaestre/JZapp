package io.github.oscarmaestre.jzapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseTest {
	Database db;
	
	@Before
	public void setUp() throws SQLException{
		db=new Database("ciclos.db");
	}
	@Test
	public void testDatabase() throws SQLException {
		
		ResultSet tables=db.getTables();
		int contador=0;
		while(tables.next()){
			//System.out.println(tables.toString());
			contador++;
		}
		System.out.println("Tablas:"+contador);
		Assert.assertNotEquals(0, contador);
	}
	@Test
	public void testTableNames() throws SQLException{
		ArrayList<String> names=db.getTableNames();
		//System.out.println(names.toString());
	}
	@Test
	public void testClassBuilder(){
		String txt=db.createClass("ciclos");
		System.out.println(txt);
	}
	@Test
	public void testFields() throws SQLException{
		ArrayList<Field> fields;
		fields=db.getFields("ciclos");
	}

}
