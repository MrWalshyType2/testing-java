package com.qa.app.http.filter;

import java.util.function.Predicate;

public interface Filter<T> {

	public boolean filter(T t);
}
