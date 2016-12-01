package io.github.oscarmaestre.jzapp;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class DatabaseTest {
	@Test
	public void testDatabase() throws SQLException {
		Database db=new Database("ciclos.db");
		ResultSet tables=db.getTables();
		while(tables.next()){
			System.out.println(tables.toString());
		}
	}

}
