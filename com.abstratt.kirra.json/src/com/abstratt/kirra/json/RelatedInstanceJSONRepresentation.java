package com.abstratt.kirra.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class RelatedInstanceJSONRepresentation extends InstanceJSONRepresentation {
    @JsonProperty
    public String relatedUri;
}