package com.abstratt.kirra.rest.tests;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import junit.framework.TestCase;

import org.eclipse.jetty.server.Server;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.fixtures.FixtureServer;
import com.abstratt.kirra.rest.client.SchemaManagementOnREST;

public class SchemaTests extends TestCase {
	
	private Server server;
	private SchemaManagement clientSchemaManagement;
	
	@Override
	public void setUp() throws Exception {
		Server server = new FixtureServer();
        server.start();
		this.server = server;
		this.clientSchemaManagement = new SchemaManagementOnREST(FixtureServer.TEST_SERVER_URI);
	}
	
	@Override
	public void tearDown() throws Exception {
		server.stop();
		server.join();
	}
	
	public void testNamespaces() {
		SchemaManagement schemaManagement = clientSchemaManagement;
		List<String> namespaces = schemaManagement.getNamespaces();
		assertEquals(1, namespaces.size());
		assertTrue(namespaces.contains("expenses"));
	}
	
	public void testEntities() {
		SchemaManagement schemaManagement = clientSchemaManagement;
		List<Entity> allEntities = schemaManagement.getAllEntities();
		assertEquals(3, allEntities.size());
		findByName(allEntities, "Category");
		findByName(allEntities, "Expense");
		findByName(allEntities, "Employee");
	}
	
	
	public void testEntityOperations() {
		SchemaManagement schemaManagement = clientSchemaManagement;
		List<Operation> operations = schemaManagement.getEntityOperations("expenses", "Expense");
		
		assertEquals(8, operations.size());
		assertFalse(findByName(operations, "newExpense").isInstanceOperation());
		assertTrue(findByName(operations, "review").isInstanceOperation());
	}
	
	public void testEntity() {
		SchemaManagement schemaManagement = clientSchemaManagement;
		Entity expense = schemaManagement.getEntity("expenses", "Expense");
		assertNotNull(expense);
	}
	
    private <T extends NamedElement<?>, C extends Collection<T>> T findByName(C collection, String name) {
    	Predicate<NamedElement<?>> byName = named -> name.equals(named.getName());
    	return collection.stream().filter(byName).findAny().get(); 
    }
}