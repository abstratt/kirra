package com.abstratt.kirra.rest.tests;

import java.util.List;

import junit.framework.TestCase;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Operation;

public class SchemaTests extends AbstractRestTests {

    public void testEntities() {
        List<Entity> allEntities = schemaManagement.getAllEntities();
        TestCase.assertEquals(3, allEntities.size());
        findByName(allEntities, "Category");
        findByName(allEntities, "Expense");
        findByName(allEntities, "Employee");
    }

    public void testEntity() {
        Entity expense = schemaManagement.getEntity("expenses", "Expense");
        TestCase.assertNotNull(expense);
    }

    public void testEntityOperations() {
        List<Operation> operations = schemaManagement.getEntityOperations("expenses", "Expense");

        TestCase.assertEquals(8, operations.size());
        TestCase.assertFalse(findByName(operations, "newExpense").isInstanceOperation());
        TestCase.assertTrue(findByName(operations, "review").isInstanceOperation());
    }

    public void testNamespaces() {
        List<String> namespaces = schemaManagement.getNamespaces();
        TestCase.assertEquals(1, namespaces.size());
        TestCase.assertTrue(namespaces.contains("expenses"));
    }
}