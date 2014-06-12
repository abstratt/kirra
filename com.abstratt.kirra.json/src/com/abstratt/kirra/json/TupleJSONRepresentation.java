package com.abstratt.kirra.json;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class TupleJSONRepresentation {

    @JsonProperty
    public String typeName;

    @JsonProperty
    public Map<String, Object> values;
}
