package com.abstratt.kirra.rest.tests;

import java.util.List;
import java.util.stream.Stream;

import com.abstratt.kirra.Instance;

public class InstanceTests extends AbstractRestTests {
	public void testCreateInstance() {
		Instance newInstance = new Instance("expenses", "Expense");
		newInstance.setValue("amount", 10);
		assertTrue(newInstance.isNew());
		Instance created = instanceManagement.createInstance(newInstance);
		assertFalse(created.isNew());
		assertEquals(10.0, created.getValue("amount"));
	}

	public void testGetInstance() {
		Instance newInstance = new Instance("expenses", "Expense");
		newInstance.setValue("amount", 10);
		Instance created = instanceManagement.createInstance(newInstance);
		Instance retrieved = instanceManagement.getInstance("expenses", "Expense", created.getObjectId(), false);
		assertEquals(10.0, retrieved.getValue("amount"));
	}

	public void testGetInstances() {
		int count = 10;
		Stream.generate(() -> instanceManagement.createInstance(new Instance("expenses", "Expense"))).limit(count).count();
		List<Instance> retrieved = instanceManagement.getInstances("expenses", "Expense", false);
		assertEquals(count, retrieved.size());
	}
}