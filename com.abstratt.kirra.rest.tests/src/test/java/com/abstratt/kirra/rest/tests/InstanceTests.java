package com.abstratt.kirra.rest.tests;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.InstanceManagement.PageRequest;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.TypeRef;

public class InstanceTests extends AbstractRestTests {
	private String uniqueString = UUID.randomUUID().toString();
	
	private String unique(String string) {
		return string + "-" + getName() + "-" + uniqueString;
	}
	
    public void testCreateInstance() {
        Instance newInstance = new Instance("expenses", "Category");
        newInstance.setValue("name", unique("My Category"));
        Instance created = instanceManagement.createInstance(newInstance);
        assertFalse(created.isNew());
        assertEquals(newInstance.getValue("name"), created.getValue("name"));
    }
    
    public void testGetTemplate() throws IOException {
        Instance template = instanceManagement.getInstance("expenses", "Expense", "_template", DataProfile.Full);
        assertEquals(LocalDate.now(), template.getValue("date"));
    }

    public void testGetInstance() throws IOException {
        Instance newInstance = new Instance("expenses", "Category");
        newInstance.setValue("name", unique("My Category"));
        Instance created = instanceManagement.createInstance(newInstance);
        Instance retrieved = instanceManagement.getInstance("expenses", "Category", created.getObjectId(), true);
        assertEquals(unique("My Category"), retrieved.getValue("name"));
    }
    
    public void testPutInstance() {
        Instance newInstance = new Instance("expenses", "Category");
        newInstance.setValue("name", unique("My Category"));
        Instance created = instanceManagement.createInstance(newInstance);
        created.setValue("name", newInstance.getValue("name") + "foobar");
        Instance updated = instanceManagement.updateInstance(created);
        assertEquals(newInstance.getValue("name") + "foobar", updated.getValue("name"));
        Instance retrieved = instanceManagement.getInstance("expenses", "Category", created.getObjectId(), false);
        assertEquals(newInstance.getValue("name") + "foobar", retrieved.getValue("name"));
    }

    public void testGetInstances() {
        int count = 2;
        int before = instanceManagement.getInstances("expenses", "Category", false).size();
        IntStream.range(0, count).forEach(index -> {
        	Instance newInstance = instanceManagement.getInstance("expenses", "Category", "_template");
            newInstance.setValue("name", unique("My Category " + "-" + index));
            instanceManagement.createInstance(newInstance);
    	});
        int after = instanceManagement.getInstances("expenses", "Category", false).size();
        assertEquals(count, after - before);
    }
    
    public void testCurrentUser() throws IOException {
    	Instance currentUser = instanceManagement.getCurrentUser();
		assertNotNull(currentUser);
    	login(null, null);
    	currentUser = instanceManagement.getCurrentUser();
    	assertNull(currentUser);
    }
    
    public void testCurrentUserRoles() throws IOException {
    	List<Instance> roles = instanceManagement.getCurrentUserRoles();
		assertEquals(1, roles.size());
		assertEquals(new TypeRef("expenses.Administrator", TypeKind.Entity), roles.get(0).getTypeRef());
		Instance role = instanceManagement.getInstance("expenses", "Administrator", roles.get(0).getObjectId(), DataProfile.Full);
		assertNotNull(role);
		
    	login(null, null);
    	roles = instanceManagement.getCurrentUserRoles();
    	assertEquals(0, roles.size());
    }
    
    public void testInvokeAction() throws IOException {
    	Instance employee = getAnyInstance("expenses", "Employee");
		Instance category = getAnyInstance("expenses", "Category");
        Instance expense = createInstance("expenses", "Expense", it -> {
        	it.setSingleRelated("category", category);
        	it.setSingleRelated("employee", employee);
        	it.setValue("description", "Some expense");
        	it.setValue("date", LocalDate.now().toString());
        	it.setValue("amount", 150.50);
        });
        
        Entity entity = schemaManagement.getEntity(expense.getTypeRef());
        Operation operation = findByName(entity.getOperations(), "submit");
        
        assertEquals("Draft", expense.getValue("status"));
        instanceManagement.executeOperation(operation, expense.getObjectId(), Arrays.asList());
        Instance afterSubmitted = instanceManagement.getInstance(expense.getEntityNamespace(), expense.getEntityName(), expense.getObjectId(), true);
        assertEquals("Submitted", afterSubmitted.getValue("status"));
    }

    public void testInvokeEntityAction() throws IOException {
    	login(kirraEmployeeUsername, kirraEmployeePassword);
        Entity entity = schemaManagement.getEntity("expenses", "Expense");
        Operation operation = findByName(entity.getOperations(), "newExpense");
        // provide the number of arguments expected by the operation
        Instance category = getAnyInstance("expenses", "Category");
        assertNotNull(category);
        Instance employee = getAnyInstance("expenses", "Employee");
        assertNotNull(category);
        List<?> result = instanceManagement.executeOperation(operation, null, Arrays.asList("Some expense", 200d, LocalDate.now(), category, employee));
        assertEquals(1, result.size());
        assertTrue(result.get(0).getClass().getSimpleName(), result.get(0) instanceof Map<?, ?>);
    }

    public void testExecuteQuery() throws IOException {
    	Instance employee = getAnyInstance("expenses", "Employee");
		Instance category = getAnyInstance("expenses", "Category");
        Instance expense = createInstance("expenses", "Expense", it -> {
        	it.setSingleRelated("category", category);
        	it.setSingleRelated("employee", employee);
        	it.setValue("description", "Some expense");
        	it.setValue("date", LocalDate.now().toString());
        	it.setValue("amount", 150.50);
        });

        Entity entity = schemaManagement.getEntity(expense.getTypeRef());
        Operation operation = findByName(entity.getOperations(), "findByStatus");
        // provide the number of arguments expected by the operation
        List<?> result1 = instanceManagement.executeQuery(operation, null, Arrays.asList("Draft"));
        assertTrue(result1.size() > 0);
        assertTrue(result1.stream().map(it -> (Instance) it).anyMatch(it -> it.getReference().equals(expense.getReference())));
        
        List<?> result2 = instanceManagement.executeQuery(operation, null, Arrays.asList("Submitted"));
        assertFalse(result2.stream().map(it -> (Instance) it).anyMatch(it -> it.getReference().equals(expense.getReference())));
    }

}