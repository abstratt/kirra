package com.abstratt.kirra.rest.tests;

import java.util.Arrays;
import java.util.stream.Stream;

import junit.framework.TestCase;

import com.abstratt.kirra.DataElement;
import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Operation;

public class InstanceTests extends AbstractRestTests {
    public void testCreateInstance() {
        Instance newInstance = new Instance("expenses", "Expense");
        newInstance.setValue("amount", 10);
        TestCase.assertTrue(newInstance.isNew());
        Instance created = instanceManagement.createInstance(newInstance);
        TestCase.assertFalse(created.isNew());
        TestCase.assertEquals(10.0, created.getValue("amount"));
    }

    public void testGetInstance() {
        Instance newInstance = new Instance("expenses", "Expense");
        newInstance.setValue("amount", 10);
        Instance created = instanceManagement.createInstance(newInstance);
        Instance retrieved = instanceManagement.getInstance("expenses", "Expense", created.getObjectId(), false);
        TestCase.assertEquals(10.0, retrieved.getValue("amount"));
    }
    
    public void testPutInstance() {
        Instance newInstance = new Instance("expenses", "Expense");
        newInstance.setValue("amount", 10);
        Instance created = instanceManagement.createInstance(newInstance);
        created.setValue("amount", 20);
        Instance updated = instanceManagement.updateInstance(created);
        TestCase.assertEquals(20.0, updated.getValue("amount"));
        Instance retrieved = instanceManagement.getInstance("expenses", "Expense", created.getObjectId(), false);
        TestCase.assertEquals(20.0, retrieved.getValue("amount"));
    }

    public void testGetInstances() {
        int count = 10;
        int before = instanceManagement.getInstances("expenses", "Expense", false).size();
        Stream.generate(() -> instanceManagement.createInstance(new Instance("expenses", "Expense"))).limit(count).count();
        int after = instanceManagement.getInstances("expenses", "Expense", false).size();
        TestCase.assertEquals(count, after - before);
    }
    
    public void testInvokeAction() {
        Instance instance = instanceManagement.createInstance(new Instance("expenses", "Expense"));
        Entity entity = schemaManagement.getEntity(instance.getTypeRef());
        DataElement property = findByName(entity.getProperties(), "status");
        Operation operation = findByName(entity.getOperations(), "submit");
        
        assertEquals("Draft", instance.getValue(property.getName()));
        instanceManagement.executeOperation(operation, instance.getObjectId(), Arrays.asList());
        Instance afterSubmitted = instanceManagement.getInstance(instance.getEntityNamespace(), instance.getEntityName(), instance.getObjectId(), true);
        assertEquals("Submitted", afterSubmitted.getValue(property.getName()));
    }
}