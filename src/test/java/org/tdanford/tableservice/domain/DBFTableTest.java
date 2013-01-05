package org.tdanford.tableservice.domain;

import java.io.*;

import org.testng.annotations.*;
import static org.testng.Assert.*;

public class DBFTableTest {
	
	private Table table;

	@Test(groups={ "functional", "dbf" })
	public void testCreateTable() throws IOException { 
		table = new DBFTable(getInput("data/nmcrash.dbf"));
	}
	
	@Test(groups={ "functional", "dbf" }, dependsOnMethods={ "testCreateTable" })
	public void testTableSize() { 
		assertEquals(table.getNumRows(), 7163, "table.getNumRows()");
		assertEquals(table.getWidth(), 5, "table.getWidth()");
	}
	
	private static InputStream getInput(String name) { 
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}
	
}
