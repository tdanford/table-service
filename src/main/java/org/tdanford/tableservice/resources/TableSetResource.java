package org.tdanford.tableservice.resources;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.tdanford.tableservice.domain.DBFTable;
import org.tdanford.tableservice.domain.Table;
import org.tdanford.tableservice.domain.TextFileTable;

import com.sun.jersey.api.NotFoundException;

@Component
@Path("tables")
public class TableSetResource {

	private Logger LOG = Logger.getLogger(TableSetResource.class);

	private File dir;
	private Map<String,Table> tables;

	public TableSetResource(String path) throws IOException { 
		this(new File(path));
	}

	public TableSetResource(File dir) throws IOException { 
		this.dir = dir;
		if(!this.dir.isDirectory() || !this.dir.canRead()) { 
			throw new IOException(String.format("%s is either not a directory, or is unreadable", 
					dir.getAbsolutePath()));
		}
		tables = new TreeMap<String,Table>();
	}

	private Table findTable(String tableId) throws IOException {

		if(!tables.containsKey(tableId)) {

			File[] filenames = new File[] { 
					new File(dir, String.format("%s.txt", tableId)),
					new File(dir, String.format("%s.dbf", tableId))
			};

			for(File filename : filenames) { 
				LOG.info(String.format("Looking for file %s", filename.getAbsolutePath()));

				Table t = null;

				if(filename.exists()) { 
					if(!filename.isDirectory() && filename.canRead()) {

						LOG.info(String.format("Loading file %s", filename.getAbsolutePath()));

						if(filename.getName().endsWith("txt")) { 
							t = new TextFileTable(filename, "\t"); 
						
						} else if(filename.getName().endsWith("dbf")) { 
							t = new DBFTable(filename);
						}
						
						return t;

					} else { 
						LOG.warn(String.format("Can't read file %s", filename.getAbsolutePath()));
					}
				}
			}

			// if none of the files are readable, we return null.
			return null;

		} else { 
			return tables.get(tableId);
		}
	}

	@GET
	public String get() { 
		return String.format("%d tables", tables.size());
	}

	@Path("{tableId}")
	public TableResource getTable(@PathParam("tableId") String tableId) throws IOException {
		LOG.info(String.format("findTable(%s)", tableId));
		Table t = findTable(tableId);   // this will look it up in 'tables', if it's already been loaded
		if(t == null) { throw new NotFoundException(tableId); }
		if(!tables.containsKey(tableId)) { tables.put(tableId, t); }
		return new TableResource(tables.get(tableId));
	}
}
