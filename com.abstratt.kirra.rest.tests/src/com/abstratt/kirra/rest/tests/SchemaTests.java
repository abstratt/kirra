package com.abstratt.kirra.rest.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.ext.servlet.ServerServlet;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.rest.client.SchemaManagementOnREST;
import com.abstratt.kirra.rest.resources.KirraContext;
import com.abstratt.kirra.rest.resources.KirraJaxRsApplication;

public class SchemaTests {
	
	private static final int TEST_SERVER_PORT = 38080;
	private static final String TEST_SERVER_PATH = "/kirra/appName/";
	private static final URI TEST_SERVER_URI = URI.create("http://localhost:" + TEST_SERVER_PORT + TEST_SERVER_PATH);

	
	private Server server;
	private SchemaManagement serverSchemaManagement;
	private SchemaManagement clientSchemaManagement;


	@Before
	public void setUp() throws Exception {
		this.serverSchemaManagement = new SchemaManagementOnFixtures();
		Server server = new Server(TEST_SERVER_PORT);
		ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/kirra");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new ServerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
        	protected Application createApplication(Context parentContext) {
        		JaxRsApplication jaxRsApplication = new JaxRsApplication(parentContext);
        		KirraJaxRsApplication jaxApplication = new KirraJaxRsApplication();
        		jaxRsApplication.add(jaxApplication);
        		return jaxRsApplication;
        	}
			
			@Override
			public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				KirraContext.setSchemaManagement(serverSchemaManagement);
				KirraContext.setBaseURI(TEST_SERVER_URI);
				try {
					super.service(request, response);
				} finally {
					KirraContext.setSchemaManagement(null);
					KirraContext.setBaseURI(null);
				}
			}
        }),"/*");
        server.start();
		this.server = server;
		this.clientSchemaManagement = new SchemaManagementOnREST(TEST_SERVER_URI);
	}
	
	@After
	public void tearDown() throws Exception {
		server.stop();
		server.join();
	}
	
	@Test
	public void testNamespaces() {
		SchemaManagement schemaManagement = clientSchemaManagement;
		List<String> namespaces = schemaManagement.getNamespaces();
		assertEquals(1, namespaces.size());
		assertTrue(namespaces.contains("expenses"));
	}
	
	@Test
	public void testEntities() {
		SchemaManagement schemaManagement = clientSchemaManagement;
		List<Entity> allEntities = schemaManagement.getAllEntities();
		assertEquals(3, allEntities.size());
		findByName(allEntities, "Category");
		findByName(allEntities, "Expense");
		findByName(allEntities, "Employee");
	}
	
	
	@Test
	public void testEntiyOperations() {
		SchemaManagement schemaManagement = clientSchemaManagement;
		List<Operation> operations = schemaManagement.getEntityOperations("expenses", "Expense");
		
		assertEquals(8, operations.size());
		assertFalse(findByName(operations, "newExpense").isInstanceOperation());
		assertTrue(findByName(operations, "review").isInstanceOperation());
	}
	
	@Test
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