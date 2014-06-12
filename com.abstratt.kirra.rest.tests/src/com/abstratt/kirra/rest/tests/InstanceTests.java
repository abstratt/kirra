package com.abstratt.kirra.rest.tests;

import java.util.stream.Stream;

import junit.framework.TestCase;

import com.abstratt.kirra.Instance;

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

    public void testGetInstances() {
        int count = 10;
        int before = instanceManagement.getInstances("expenses", "Expense", false).size();
        Stream.generate(() -> instanceManagement.createInstance(new Instance("expenses", "Expense"))).limit(count).count();
        int after = instanceManagement.getInstances("expenses", "Expense", false).size();
        TestCase.assertEquals(count, after - before);
    }
}