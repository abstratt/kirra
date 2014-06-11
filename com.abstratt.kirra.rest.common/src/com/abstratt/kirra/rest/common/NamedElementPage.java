package com.abstratt.kirra.rest.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.abstratt.kirra.NamedElement;

public abstract class NamedElementPage<T extends NamedElement<?>>  {
	public NamedElementPage(List<T> contents) {
		this.contents = new LinkedHashMap<String, T>();
		for (NamedElement<?> namedElement : contents)
			this.contents.put(namedElement.getName(), (T) namedElement);
		this.offset = 0;
	}
    public final long offset;
    public final Map<String, T> contents;
}
