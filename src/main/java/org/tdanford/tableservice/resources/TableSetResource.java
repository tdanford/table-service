package org.tdanford.tableservice.resources;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.tdanford.tableservice.domain.TextFileTable;

import com.sun.jersey.api.NotFoundException;

@Component
@Path("tables")
public class TableSetResource {
	
	private Logger LOG = Logger.getLogger(TableSetResource.class);
	
	private File dir;
	private Map<String,TextFileTable> tables;
	
	public TableSetResource(String path) throws IOException { 
		this(new File(path));
	}
	
	public TableSetResource(File dir) throws IOException { 
		this.dir = dir;
		if(!this.dir.isDirectory() || !this.dir.canRead()) { 
			throw new IOException(String.format("%s is either not a directory, or is unreadable", 
					dir.getAbsolutePath()));
		}
		tables = new TreeMap<String,TextFileTable>();
	}
	
	private void findTable(String tableId) throws IOException { 
		if(!tables.containsKey(tableId)) { 
			File filename = new File(dir, String.format("%s.txt", tableId));
			LOG.info(String.format("Looking for file %s", filename.getAbsolutePath()));
			if(filename.exists() && !filename.isDirectory() && filename.canRead()) { 
				TextFileTable t = new TextFileTable(filename, "\t");
				tables.put(tableId, t);
				LOG.info(String.format("Loaded file %s", filename.getAbsolutePath()));
			} else { 
				LOG.warn(String.format("Can't find or read file %s", filename.getAbsolutePath()));
			}
		}
	}
	
	@GET
	public String get() { 
		return String.format("%d tables", tables.size());
	}

	@Path("{tableId}")
	public TableResource getTable(@PathParam("tableId") String tableId) throws IOException {
		LOG.info(String.format("findTable(%s)", tableId));
		findTable(tableId);
		if(!tables.containsKey(tableId)) { 
			throw new NotFoundException(tableId);
		}
		return new TableResource(tables.get(tableId));
	}
}
