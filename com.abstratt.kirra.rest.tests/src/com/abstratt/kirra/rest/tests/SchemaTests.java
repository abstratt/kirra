package com.abstratt.kirra.rest.tests;

import java.util.List;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Operation;

public class SchemaTests extends AbstractRestTests {
	
	public void testNamespaces() {
		List<String> namespaces = schemaManagement.getNamespaces();
		assertEquals(1, namespaces.size());
		assertTrue(namespaces.contains("expenses"));
	}
	
	public void testEntities() {
		List<Entity> allEntities = schemaManagement.getAllEntities();
		assertEquals(3, allEntities.size());
		findByName(allEntities, "Category");
		findByName(allEntities, "Expense");
		findByName(allEntities, "Employee");
	}
	
	
	public void testEntityOperations() {
		List<Operation> operations = schemaManagement.getEntityOperations("expenses", "Expense");
		
		assertEquals(8, operations.size());
		assertFalse(findByName(operations, "newExpense").isInstanceOperation());
		assertTrue(findByName(operations, "review").isInstanceOperation());
	}
	
	public void testEntity() {
		Entity expense = schemaManagement.getEntity("expenses", "Expense");
		assertNotNull(expense);
	}
}