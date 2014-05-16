package com.abstratt.kirra.sampledata;

import java.util.List;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Schema;

public interface SampleBuilder {
    Schema getSchema();
    
    public List<Instance> getInstances(String namespace, String name);
}
