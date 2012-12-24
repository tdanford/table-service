package org.tdanford.tableservice.domain;

import java.util.ArrayList;
import java.util.Arrays;

public class OrQuery implements Query {
	
	private Query[] subs;
	
	public OrQuery(Query... qs) { 
		subs = qs.clone();
	}

	public boolean returnsRow(String[] row) {
		for(Query q : subs) { 
			if(q.returnsRow(row)) { return true; }
		}
		return false;
	}
	
	public void addQuery(Query q) { 
		ArrayList<Query> qs = new ArrayList<Query>(Arrays.asList(subs));
		qs.add(q);
		subs = qs.toArray(new Query[0]);
	}

}
