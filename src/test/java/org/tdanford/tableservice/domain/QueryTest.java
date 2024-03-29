package org.tdanford.tableservice.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * 
 * @author Timothy Danford
 *
 */
public class QueryTest {
	
	@Test(groups={"functional", "query"})
	public void testEqualsQueryTableA() throws IOException { 
		Table tableA = new TextFileTable(getInput("data/tableA.txt"), "\t");
		Set<Integer> found = tableA.findRows(new EqualsQuery(0, "1"));
	
		assertEquals(found.size(), 2, "Didn't find the correct number of rows");
		assertTrue(found.contains(0), "Didn't find row 0 in the query result");
		assertTrue(found.contains(1), "Didn't find row 1 in the query result");
	}

	@Test(groups={"functional", "query"})
	public void testOrQueryTableA() throws IOException { 
		Table tableA = new TextFileTable(getInput("data/tableA.txt"), "\t");
		Set<Integer> found = tableA.findRows(
				new OrQuery(
						new EqualsQuery(2, "2"),
						new EqualsQuery(2, "3")));
	
		assertEquals(found.size(), 3, "Didn't find the correct number of rows");
		assertTrue(found.contains(0), "Didn't find row 0 in the query result");
		assertTrue(found.contains(1), "Didn't find row 1 in the query result");
		assertTrue(found.contains(3), "Didn't find row 1 in the query result");
	}

	@Test(groups={"functional", "query"})
	public void testAndQueryTableA() throws IOException { 
		Table tableA = new TextFileTable(getInput("data/tableA.txt"), "\t");
		Set<Integer> found = tableA.findRows(
				new AndQuery(
						new EqualsQuery(2, "2"),
						new EqualsQuery(0, "1")));
	
		assertEquals(found.size(), 1, "Didn't find the correct number of rows");
		assertTrue(found.contains(0), "Didn't find row 0 in the query result");
	}
	
	
	private static InputStream getInput(String name) { 
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}
	
}
