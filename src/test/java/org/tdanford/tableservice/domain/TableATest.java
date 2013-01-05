package org.tdanford.tableservice.domain;

import java.io.IOException;
import java.io.InputStream;

import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * 
 * @author Timothy Danford
 *
 */
public class TableATest {
	
	private InputStream tableInput; 
	private Table table;

	@BeforeClass
	@Parameters({ "tableA" })
	public void setup(@Optional("data/tableA.txt") String tableName) throws IOException { 
		tableInput = getInput(tableName);
	}
	
	@Test(groups={ "functional" })
	public void testCreateTable() throws IOException { 
		table = new TextFileTable(tableInput, "\t");
	}
	
	@Test(groups={ "functional" }, dependsOnMethods={ "testCreateTable" })
	public void testTableSize() { 
		assertEquals(table.getNumRows(), 4, "table.getNumRows()");
		assertEquals(table.getWidth(), 3, "table.getWidth()");
	}
	
	@Test(groups={ "functional" }, dependsOnMethods={ "testTableSize" })
	public void testTableContents() { 
		assertEquals(table.getRow(0)[0], "1", "0,0");
		assertEquals(table.getRow(0)[1], "1", "0,1");
		assertEquals(table.getRow(0)[2], "2", "0,2");

		assertEquals(table.getRow(1)[0], "1", "1,0");
		assertEquals(table.getRow(1)[1], "1", "1,1");
		assertEquals(table.getRow(1)[2], "3", "1,2");

		assertEquals(table.getRow(2)[0], "2", "2,0");
		assertEquals(table.getRow(2)[1], "3", "2,1");
		assertEquals(table.getRow(2)[2], "1", "2,2");

		assertEquals(table.getRow(3)[0], "2", "3,0");
		assertEquals(table.getRow(3)[1], "3", "3,1");
		assertEquals(table.getRow(3)[2], "2", "3,2");
	}
	
	private static InputStream getInput(String name) { 
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}
	
}
