/**
 * 
 */
package org.tdanford.tableservice.domain;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.linuxense.javadbf.DBFReader;

/**
 * @author danfoti1
 *
 */
public class DBFTable implements Table {
	
	private static Map<String,DBFTypeConverter> typeConverters;
	static { 
		typeConverters = new TreeMap<String,DBFTypeConverter>();
		typeConverters.put("C", new DBFStringConverter());
		typeConverters.put("N", new DBFIntegerConverter());
		typeConverters.put("F", new DBFDoubleConverter());
		typeConverters.put("L", new DBFBooleanConverter());
		typeConverters.put("D", new DBFDateConverter());
	}
	
	private ArrayList<String> fields;
	private Map<String,Integer> fieldIndices;
	private ArrayList<String[]> rows;
	
	private DBFTable() { 
		fields = new ArrayList<String>();
		fieldIndices = new TreeMap<String,Integer>();
		rows = new ArrayList<String[]>();
	}
	
	public DBFTable(File dbfFile) throws IOException { 
		this();
		FileInputStream fis = new FileInputStream(dbfFile);
		try { 
			DBFReader reader = new DBFReader(fis);
			ArrayList<DBFTypeConverter> fieldConverters = new ArrayList<DBFTypeConverter>();
			
			for(int i = 0; i < reader.getFieldCount(); i++) { 
				String fieldName = reader.getField(i).getName();
				fields.add(fieldName);
				fieldIndices.put(fieldName, i);
				String type = new String(new byte[] { reader.getField(i).getDataType() }, "UTF-8");
				if(!typeConverters.containsKey(type)) { 
					throw new IllegalArgumentException(String.format("Unknown type %s in field %d (%s)",
							type, i, fieldName));
				}
				fieldConverters.add(typeConverters.get(type));
			}
			
			for(int i = 0; i < reader.getRecordCount(); i++) { 
				Object[] record = reader.nextRecord();
				String[] row = new String[record.length];
				for(int j = 0; j < record.length; j++) { 
					if(record[j] == null) { 
						row[j] = null;
					} else { 
						row[j] = fieldConverters.get(j).convert(record[j]);
					}
				}
				
				rows.add(row);
			}
			
		} finally { 
			fis.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#getWidth()
	 */
	@Override
	public int getWidth() {
		return fields.size();
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#getNumRows()
	 */
	@Override
	public int getNumRows() {
		return rows.size();
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#getField(int)
	 */
	@Override
	public String getField(int i) {
		return fields.get(i);
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#getRow(int)
	 */
	@Override
	public String[] getRow(int j) {
		return rows.get(j);
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#findHeader(java.lang.String)
	 */
	@Override
	public Integer findHeader(String value) {
		return fieldIndices.get(value);
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#getHeaders()
	 */
	@Override
	public String[] getHeaders() {
		return fields.toArray(new String[0]);
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#findRows(org.tdanford.tableservice.domain.Query)
	 */
	@Override
	public SortedSet<Integer> findRows(Query q) {
		TreeSet<Integer> results = new TreeSet<Integer>();
		for(int i = 0; i < rows.size(); i++) { 
			if(q.returnsRow(rows.get(i))) { 
				results.add(i);
			}
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see org.tdanford.tableservice.domain.Table#getFieldCounts(int)
	 */
	@Override
	public LinkedHashMap<String, Integer> getFieldCounts(int i) {
		LinkedHashMap<String,Integer> map = new LinkedHashMap<String,Integer>();
		for(int j = 0; j < rows.size(); j++) { 
			String value = rows.get(j)[i];
			if(!map.containsKey(value)) { 
				map.put(value, 1);
			} else { 
				map.put(value, map.get(value) + 1);
			}
		}
		return map;
	}

}

interface DBFTypeConverter { 
	public String convert(Object value);
}

class DBFStringConverter implements DBFTypeConverter { 
	public String convert(Object value) { 
		return value.toString();
	}
}
class DBFIntegerConverter implements DBFTypeConverter { 
	public String convert(Object value) { 
		return value.toString();
	}
}
class DBFDoubleConverter implements DBFTypeConverter { 
	public String convert(Object value) { 
		return String.format("%f", ((Double)value));
	}
}
class DBFBooleanConverter implements DBFTypeConverter { 
	public String convert(Object value) { 
		return ((Boolean)value) ? "true" : "false";
	}
}
class DBFDateConverter implements DBFTypeConverter { 
	
	private DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	public String convert(Object value) { 
		return format.format((Date)value);
	}
}