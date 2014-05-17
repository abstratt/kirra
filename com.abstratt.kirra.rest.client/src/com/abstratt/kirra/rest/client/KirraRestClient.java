package com.abstratt.kirra.rest.client;

import java.util.List;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Schema;
import com.abstratt.kirra.TypeRef;

public interface KirraRestClient {
    Schema getSchema();
    List<Instance> getInstances(TypeRef type);
    Instance getInstance(TypeRef type, String id);
    void updateInstance(Instance updated);
    void createInstance(Instance newInstance);
    void deleteInstance(TypeRef type, String id);
    
}
