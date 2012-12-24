package org.tdanford.tableservice.domain;

public class EqualsQuery implements Query {
	
	private int index;
	private String value;
	
	public EqualsQuery(int i, String v) { 
		this.index = i;
		this.value = v;
	}
	
	public boolean returnsRow(String[] row) {
		return index >= 0 && index < row.length && row[index] == value || row[index].equals(value);
	}
}
