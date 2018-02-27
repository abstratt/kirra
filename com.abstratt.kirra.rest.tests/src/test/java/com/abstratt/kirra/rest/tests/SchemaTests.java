package com.abstratt.kirra.rest.tests;

import java.util.List;

import junit.framework.TestCase;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.TypeRef;

public class SchemaTests extends AbstractRestTests {

    public void testEntities() {
        List<Entity> allEntities = schemaManagement.getAllEntities();
        assertTrue(!allEntities.isEmpty());
        findByName(allEntities, "Category");
        findByName(allEntities, "Expense");
        findByName(allEntities, "Employee");
    }

    public void testEntity() {
        Entity category = schemaManagement.getEntity("expenses", "Category");
        assertNotNull(category);
        assertEquals("Category", category.getLabel());
        Property categoryName = category.getProperty("name");
        assertTrue(categoryName.isUserVisible());
        
    }

    public void testEntityOperations() {
        List<Operation> operations = schemaManagement.getEntityOperations("expenses", "Expense");
        assertTrue(!operations.isEmpty());

        Operation reviewOperation = findByName(operations, "review");
		assertTrue(reviewOperation.isInstanceOperation());
        List<Parameter> reviewParameters = reviewOperation.getParameters();
		assertEquals(0, reviewParameters.size());

        Operation newExpenseOperation = findByName(operations, "newExpense");
		assertFalse(newExpenseOperation.isInstanceOperation());
        List<Parameter> newExpenseParameters = newExpenseOperation.getParameters();
        assertEquals(5, newExpenseParameters.size());
        assertEquals("description", newExpenseParameters.get(0).getName());
        assertEquals("amount", newExpenseParameters.get(1).getName());
        assertEquals("date", newExpenseParameters.get(2).getName());
        assertEquals("category", newExpenseParameters.get(3).getName());
        assertEquals("employee", newExpenseParameters.get(4).getName());
    }
    
    public void testEntityProperties() {
        List<Property> properties = schemaManagement.getEntityProperties("expenses", "Expense");
        assertTrue(!properties.isEmpty());
        Property amount = findByName(properties, "amount");
		assertTrue(amount.isUserVisible());
		assertFalse(amount.isHasDefault());
		assertTrue(amount.isInitializable());
		assertFalse(amount.isEditable());
		assertTrue(amount.isRequired());
		assertEquals("Double", amount.getTypeRef().getTypeName());
		assertEquals(TypeRef.TypeKind.Primitive, amount.getTypeRef().getKind());
		
		Property expenseDate = findByName(properties, "date");
		assertTrue(expenseDate.isUserVisible());
		assertTrue(expenseDate.isHasDefault());
		assertTrue(expenseDate.isInitializable());
		assertTrue(expenseDate.isEditable());
		assertTrue(expenseDate.isRequired());
		assertEquals(TypeRef.TypeKind.Primitive, expenseDate.getTypeRef().getKind());
		assertEquals("Date", expenseDate.getTypeRef().getTypeName());
    }

    public void testNamespaces() {
        List<String> namespaces = schemaManagement.getNamespaces();
        assertTrue(!namespaces.isEmpty());
        assertTrue(namespaces.contains("expenses"));
    }
}