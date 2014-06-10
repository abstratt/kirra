package com.abstratt.kirra.rest.resources;

import java.util.List;

public class Page<T> {
	public Page(List<T> contents) {
		this.contents = contents;
		this.offset = 0;
	}
    public final long offset;
    public final List<T> contents;
}
