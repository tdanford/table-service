package org.tdanford.tableservice.domain;

import java.util.LinkedHashMap;
import java.util.SortedSet;

public interface Table {

	public abstract int getWidth();

	public abstract int getNumRows();

	public abstract String getField(int i);

	public abstract String[] getRow(int j);

	public abstract Integer findHeader(String value);

	public abstract String[] getHeaders();

	public abstract SortedSet<Integer> findRows(Query q);

	public abstract LinkedHashMap<String, Integer> getFieldCounts(int i);

}