package io.github.oscarmaestre.jzapp;

import java.io.IOException;
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
	public void testFields() throws SQLException{
		ArrayList<Field<?>> fields;
		fields=db.getFields("evaluaciones");
		for (Field<?> f: fields){
			System.out.print("F:"+f.toString());
		}
	}
	@Test
	public void testClassGeneration() throws SQLException, IOException{
		String classContents=this.db.createClass("modulos");
		System.out.println(classContents);
	}
	@Test
	public void testFK() throws SQLException, IOException{
		this.db.generateForeignKeysClass("Relaciones.java", "com.ies", "Relaciones");
	}

}
