package org.tdanford.tableservice.resources;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;
import org.tdanford.tableservice.domain.AndQuery;
import org.tdanford.tableservice.domain.EqualsQuery;
import org.tdanford.tableservice.domain.OrQuery;
import org.tdanford.tableservice.domain.TextFileTable;

public class TableResource {

	private TextFileTable table;

	public TableResource(TextFileTable t) { 
		this.table = t;
	}

	@GET
	@Path("dimensions")
	@Produces({"application/json"})
	public LinkedHashMap<String,Integer> getDimensions() { 
		LinkedHashMap<String,Integer> map = new LinkedHashMap<String,Integer>();		
		map.put("numRows", table.getNumRows());
		map.put("width", table.getWidth());

		return map;
	}

	@GET
	@Path("fields")
	@Produces({"application/json"})
	public LinkedHashMap getFields() { 
		LinkedHashMap map = new LinkedHashMap();

		map.put("fields", table.getHeaders());

		return map;
	}
	
	@GET
	@Path("field/{fieldName}")
	@Produces({"application/json"})
	public LinkedHashMap getField(@PathParam("fieldName") String fieldName) {
		Integer idx = table.findHeader(fieldName);
		if(idx == null) { 
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity(String.format("Unknown field %s", fieldName))
					.build());
		}
		return table.getFieldCounts(idx);
	}

	@GET
	@Path("rows")
	@Produces({"application/json"})
	public LinkedHashMap findRows(@Context UriInfo info) { 
		MultivaluedMap<String, String> queryParams = info.getQueryParameters();

		AndQuery aq = new AndQuery();
		for(String header : queryParams.keySet()) {
			Integer index = table.findHeader(header);

			if(index != null) { 
				OrQuery or = new OrQuery();
				for(String value : queryParams.get(header)) { 
					EqualsQuery eq = new EqualsQuery(index, value);
					or.addQuery(eq);
				}
				aq.addQuery(or);
				
			} else { 
				throw new WebApplicationException(Response
						.status(Status.BAD_REQUEST)
						.entity(String.format("Unknown field %s", header))
						.build());
			}
		}

		SortedSet<Integer> rowIndices = table.findRows(aq);
		ArrayList<LinkedHashMap> rows = new ArrayList<LinkedHashMap>();
		for(Integer idx : rowIndices) { rows.add(convertRow(table.getRow(idx))); }

		LinkedHashMap map = new LinkedHashMap();
		map.put("rows", rows.toArray());

		return map;
	}
	
	private Pattern intRegex = Pattern.compile("-?\\d+");
	private Pattern	decimalRegex = Pattern.compile("-?(?:\\.\\d+)|(?:\\d+\\.\\d*)");
	
	/*
	 * Turns each row in the Table (a String[]) into an object (LinkedHashMap), whose keys
	 * are the field names of the table and whose values are the values from the row, 
	 * suitable for returning from the service itself.
	 * 
	 * Using regexes, this automatically parses numbers into corresponding numeric types.
	 */
	private LinkedHashMap convertRow(String[] row) { 
		LinkedHashMap obj = new LinkedHashMap();
		
		for(int i = 0; i < table.getWidth(); i++) { 
			String value = row[i];
			if(intRegex.matcher(value).matches()) { 
				obj.put(table.getField(i), Integer.parseInt(value));
				
			} else if(decimalRegex.matcher(value).matches()) { 
				obj.put(table.getField(i), Double.parseDouble(value));
				
			} else { 
				obj.put(table.getField(i), row[i]);
			}
		}
		
		return obj;
	}
}
