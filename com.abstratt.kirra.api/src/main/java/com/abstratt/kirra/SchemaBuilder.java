package com.abstratt.kirra;

/**
 * Something that knows how to build a schema on-demand.
 */
public interface SchemaBuilder {
    /**
     * Builds a schema on request. What schema to build is defined by
     * implementation-specific context.
     * 
     * @return the schema built
     */
    Schema build();
}
