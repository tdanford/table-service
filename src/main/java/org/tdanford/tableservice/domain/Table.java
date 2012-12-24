package org.tdanford.tableservice.domain;

import java.util.*;
import java.io.*;

public class Table {

	private int width;
	private ArrayList<String> header;
	private Map<String,Integer> headerIndices;
	private ArrayList<String[]> rows;

	public Table(File f, String delims) throws IOException { 
		InputStream is = new FileInputStream(f);
		parse(is, delims, "UTF-8");
		is.close();
	}

	public Table(InputStream is, String delims) throws IOException { 
		parse(is, delims, "UTF-8");
	}

	public int getWidth() { return width; }

	public int getNumRows() { return rows.size(); }

	public String getField(int i) { return header.get(i); }

	public String[] getRow(int j) { return rows.get(j); }

	public Integer findHeader(String value) { 
		if(headerIndices.containsKey(value)) {
			return headerIndices.get(value);
		} else { 
			return null;
		}
	}

	public String[] getHeaders() { 
		return header != null ? header.toArray(new String[0]) : new String[0];
	}

	public SortedSet<Integer> findRows(Query q) { 
		TreeSet<Integer> ris = new TreeSet<Integer>();
		for(int j = 0; j < rows.size(); j++) { 
			if(q.returnsRow(rows.get(j))) { 
				ris.add(j);
			}
		}
		return ris;
	}

	public LinkedHashMap<String,Integer> getFieldCounts(int i) { 
		LinkedHashMap<String,Integer> counts = new LinkedHashMap<String,Integer>();

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

	private void parseHeaders(String line, String delims) { 
		if(line.startsWith("#")) { line = line.substring(1, line.length()); }
		String[] array = line.split(delims);
		header = new ArrayList<String>(Arrays.asList(array));
		headerIndices = new TreeMap<String,Integer>();
		for(int i = 0; i < header.size(); i++) {
			headerIndices.put(header.get(i), i);
		}
		width = header.size();
	}

	private void parse(InputStream is, String delims, String charSet) throws IOException { 
		width = -1;
		int lineIndex = 0;
		rows = new ArrayList<String[]>();
		header = null;
		headerIndices = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(is, charSet));

		try { 
			String line;
			while((line = br.readLine()) != null) {
				lineIndex += 1;

				// skip #-started lines, as comments.
				// *except* for the last commented line, which is the header.
				if(line.startsWith("#")) {  
					parseHeaders(line, delims);

				} else { 

					String[] array = line.split(delims);
					if(array.length == width) {
						rows.add(array);

					} else { 
						throw new IOException(String.format("Line %d has %d fields, while the file should have width %d", 
								lineIndex, array.length, width));
					}
				}
			}


		} finally { 
			br.close();
		}

	}

}
