package org.tdanford.tableservice.domain;

import java.util.*;
import java.io.*;

public class Table {
	
	private int width;
	private ArrayList<String> header;
	private ArrayList<String[]> rows;

	public Table(InputStream is, String delims) throws IOException { 
		parse(is, delims, "UTF-8");
	}
	
	public int getWidth() { return width; }
	
	public int getNumRows() { return rows.size(); }
	
	public String getField(int i) { return header.get(i); }
	
	public String[] getRow(int j) { return rows.get(j); }
	
	public Map<String,Integer> getFieldCounts(int i) { 
		Map<String,Integer> counts = new TreeMap<String,Integer>();
		
		for(int j = 0; j < rows.size(); j++) { 
			String value = rows.get(j)[i];
			if(!counts.containsKey(value)) { 
				counts.put(value, 1);
			} else { 
				counts.put(value, counts.get(value) + 1);
			}
		}
		
		return counts;
	}

	private void parse(InputStream is, String delims, String charSet) throws IOException { 
		width = -1;
		int lineIndex = 0;
		rows = new ArrayList<String[]>();
		header = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(is, charSet));
		
		try { 
			String line;
			while((line = br.readLine()) != null) {
				lineIndex += 1;
				if(!line.startsWith("#")) {  // skip #-started lines, as comments.
					
					if(width == -1) { // read the first non-comment line as header. 
						String[] array = line.split(delims);
						header = new ArrayList<String>(Arrays.asList(array));
						width = header.size();
						
					} else {  // read all subsequent non-comment lines as rows.
						String[] array = line.split(delims);
						if(array.length == width) {
							rows.add(array);
							
						} else { 
							throw new IOException(String.format("Line %d has %d fields, while the file should have width %d", 
									lineIndex, array.length, width));
						}
					}
				}
			}
			
			
		} finally { 
			br.close();
		}
		
	}

}
