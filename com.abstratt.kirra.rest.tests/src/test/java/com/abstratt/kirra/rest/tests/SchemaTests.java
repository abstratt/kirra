package com.abstratt.kirra.rest.tests;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Operation.OperationKind;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;

public class SchemaTests extends AbstractRestTests {

    public void testEntities() {
        List<Entity> allEntities = schemaManagement.getAllEntities();
        assertTrue(!allEntities.isEmpty());
        List<String> expectedEntities = Arrays.asList("Category", "Expense", "Employee");
        expectedEntities.forEach(it -> assertTrue(it, findByName(allEntities, it).isUserVisible()));
    }

    public void testEntity() {
        Entity category = schemaManagement.getEntity("expenses", "Category");
        assertNotNull(category);
        assertEquals("Category", category.getLabel());
        Property categoryName = category.getProperty("name");
        assertTrue(categoryName.isUserVisible());
    }

    public void testEntityOperations() {
    	Entity expenseEntity = schemaManagement.getEntity(new TypeRef("expenses.Expense", TypeKind.Entity));
    	Entity employeeEntity = schemaManagement.getEntity(new TypeRef("expenses.Employee", TypeKind.Entity));

        Operation reviewOperation = expenseEntity.getOperation("review");
        assertEquals("Review", reviewOperation.getLabel());
		assertTrue(reviewOperation.isInstanceOperation());
		assertEquals(OperationKind.Action, reviewOperation.getKind());
        List<Parameter> reviewParameters = reviewOperation.getParameters();
		assertEquals(0, reviewParameters.size());
		assertNull(reviewOperation.getTypeRef());
		reviewParameters.forEach(it -> assertTrue(it.getInAllSets()));
        
        Operation newExpenseOperation = expenseEntity.getOperation("newExpense");
        assertEquals("New Expense", newExpenseOperation.getLabel());
        assertFalse(newExpenseOperation.isInstanceOperation());
		assertEquals(OperationKind.Action, newExpenseOperation.getKind());
        List<Parameter> newExpenseParameters = newExpenseOperation.getParameters();
        assertEquals(5, newExpenseParameters.size());
        assertEquals("description", newExpenseParameters.get(0).getName());
        assertEquals("amount", newExpenseParameters.get(1).getName());
        assertEquals("date", newExpenseParameters.get(2).getName());
        assertEquals("category", newExpenseParameters.get(3).getName());
        assertEquals("employee", newExpenseParameters.get(4).getName());
        assertEquals(expenseEntity.getTypeRef(), newExpenseOperation.getTypeRef());
        newExpenseParameters.forEach(it -> assertTrue(it.getInAllSets()));
        
        Operation findByStatusOperation = expenseEntity.getOperation("byStatus");
        assertNotNull(findByStatusOperation);
        assertFalse(findByStatusOperation.isInstanceOperation());
		assertEquals(OperationKind.Finder, findByStatusOperation.getKind());
        List<Parameter> findByStatusParameters = findByStatusOperation.getParameters();
        assertEquals(1, findByStatusParameters.size());
        assertEquals("status", findByStatusParameters.get(0).getName());
        findByStatusParameters.forEach(it -> assertTrue(it.getInAllSets()));
        
        Operation declareExpenseOperation = employeeEntity.getOperation("declareExpense");
		assertTrue(declareExpenseOperation.isInstanceOperation());
		assertEquals(OperationKind.Action, declareExpenseOperation.getKind());
        List<Parameter> declareExpenseParameters = declareExpenseOperation.getParameters();
	        assertEquals(4, declareExpenseParameters.size());
        assertEquals("description", declareExpenseParameters.get(0).getName());
        assertEquals("amount", declareExpenseParameters.get(1).getName());
        assertEquals("date", declareExpenseParameters.get(2).getName());
        assertEquals("category", declareExpenseParameters.get(3).getName());
        declareExpenseParameters.forEach(it -> assertTrue(it.getInAllSets()));
    }

    

    public void testEntityRelationships() {
        Entity employeeEntity = schemaManagement.getEntity("expenses", "Employee");
        List<Relationship> relationships = employeeEntity.getRelationships();
        assertTrue(!relationships.isEmpty());
        
        Relationship allExpenses = findByName(relationships, "expenses");
        assertTrue(allExpenses.isMultiple());
        assertFalse(allExpenses.isDerived());

        Relationship myExpenses = findByName(relationships, "recordedExpenses");
        assertTrue(myExpenses.isMultiple());
        assertTrue(myExpenses.isDerived());

    }

    
    public void testEntityProperties() {
        Entity expenseEntity = schemaManagement.getEntity("expenses", "Expense");
		List<Property> properties = expenseEntity.getProperties();
        assertTrue(!properties.isEmpty());
        
        assertEquals("moniker", expenseEntity.getMnemonicSlot());
        
        Property moniker = findByName(properties, "moniker");
        assertEquals("Moniker", moniker.getLabel());
		assertTrue(moniker.isUserVisible());
		// being derived is not the same as having a default value
		assertFalse(moniker.isHasDefault());
		// what is the use case for having this bit available in the metadata?
		assertTrue(moniker.isDerived());
		assertFalse(moniker.isInitializable());
		assertFalse(moniker.isEditable());
		assertFalse(moniker.isRequired());
		assertTrue(moniker.isMnemonic());
		assertEquals("String", moniker.getTypeRef().getTypeName());
		assertEquals(TypeRef.TypeKind.Primitive, moniker.getTypeRef().getKind());
		
		Property amount = findByName(properties, "amount");
        assertEquals("Amount", amount.getLabel());
		assertTrue(amount.isUserVisible());
		assertFalse(amount.isHasDefault());
		assertFalse(amount.isDerived());
		assertTrue(amount.isInitializable());
		assertFalse(amount.isEditable());
		assertTrue(amount.isRequired());
		assertFalse(amount.isMnemonic());
		assertEquals("Double", amount.getTypeRef().getTypeName());
		assertEquals(TypeRef.TypeKind.Primitive, amount.getTypeRef().getKind());
		
		
		Property expenseDate = findByName(properties, "date");
		assertEquals("Date", expenseDate.getLabel());
		assertTrue(expenseDate.isUserVisible());
		assertTrue(expenseDate.isHasDefault());
		assertFalse(expenseDate.isDerived());
		assertTrue(expenseDate.isInitializable());
		assertTrue(expenseDate.isEditable());
		assertTrue(expenseDate.isRequired());
		assertFalse(expenseDate.isMnemonic());
		assertEquals(TypeRef.TypeKind.Primitive, expenseDate.getTypeRef().getKind());
		assertEquals("Date", expenseDate.getTypeRef().getTypeName());
		
        List<String> orderedDataElements = expenseEntity.getOrderedDataElements();
		assertNotNull(orderedDataElements);
        assertTrue(!orderedDataElements.isEmpty());
        assertTrue(orderedDataElements.containsAll(Arrays.asList("amount", "date", "description", "employee")));
        
    }

    public void testNamespaces() {
        List<String> namespaces = schemaManagement.getNamespaces();
        assertTrue(!namespaces.isEmpty());
        assertTrue(namespaces.contains("expenses"));
    }
}