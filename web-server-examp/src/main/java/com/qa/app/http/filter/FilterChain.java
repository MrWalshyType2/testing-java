package com.qa.app.http.filter;

import java.util.List;

public class FilterChain {

	private List<Filter<?>> filters;
	
	public FilterChain(List<Filter<?>> filters) {
		this.filters = filters;
	}
	
	public void add(Filter<?> filter) {
		filters.add(filter);
	}
	
	public void remove(Filter<?> filter) {
		filters.remove(filter);
	}
	
	public void remove(int i) {
		filters.remove(i);
	}
}
