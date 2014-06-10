package com.abstratt.kirra.sampledata;

import java.util.List;
import java.util.Map;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Schema;
import com.abstratt.kirra.TypeRef;

public interface SampleBuilder {
    Schema getSchema();
    
	Map<TypeRef, List<Instance>> getInstances();
}
