package com.abstratt.kirra.rest.common;

import java.util.List;

public class Page<T> {
    public final List<T> contents;
    public final long offset;

    public Page(List<T> contents) {
        this.contents = contents;
        this.offset = 0;
    }
}
