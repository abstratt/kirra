package com.abstratt.kirra.rest.tests;

import java.util.List;

import junit.framework.TestCase;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Operation;

public class SchemaTests extends AbstractRestTests {

    public void testEntities() {
        List<Entity> allEntities = schemaManagement.getAllEntities();
        assertTrue(!allEntities.isEmpty());
        findByName(allEntities, "Category");
        findByName(allEntities, "Expense");
        findByName(allEntities, "Employee");
    }

    public void testEntity() {
        Entity expense = schemaManagement.getEntity("expenses", "Expense");
        assertNotNull(expense);
        assertEquals("Expense", expense.getLabel());
        
    }

    public void testEntityOperations() {
        List<Operation> operations = schemaManagement.getEntityOperations("expenses", "Expense");
        assertTrue(!operations.isEmpty());
        assertFalse(findByName(operations, "newExpense").isInstanceOperation());
        assertTrue(findByName(operations, "review").isInstanceOperation());
    }

    public void testNamespaces() {
        List<String> namespaces = schemaManagement.getNamespaces();
        assertTrue(!namespaces.isEmpty());
        assertTrue(namespaces.contains("expenses"));
    }
}