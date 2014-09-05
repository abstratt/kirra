package com.abstratt.kirra.rest.common;

import java.util.List;

public class Page<T> {
    public final long offset;
    public final long length;
    public final List<T> contents;
    
    public Page(List<T> contents) {
        this.contents = contents;
        this.offset = 0;
        this.length = contents.size();
    }
}
