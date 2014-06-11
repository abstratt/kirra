package com.abstratt.kirra.rest.tests;

import java.util.Collection;

import junit.framework.TestCase;

import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.fixtures.FixtureServer;
import com.abstratt.kirra.rest.client.InstanceManagementOnREST;
import com.abstratt.kirra.rest.client.SchemaManagementOnREST;

public class AbstractRestTests extends TestCase {

	private FixtureServer server;
	protected SchemaManagement schemaManagement;
	protected InstanceManagement instanceManagement;
	
	@Override
	public void setUp() throws Exception {
		server = new FixtureServer();
        server.start();
		this.schemaManagement = new SchemaManagementOnREST(FixtureServer.TEST_SERVER_URI);
		this.instanceManagement = new InstanceManagementOnREST(FixtureServer.TEST_SERVER_URI);
	}
	
	@Override
	public void tearDown() throws Exception {
		server.stop();
		server.join();
	}
	
	
    protected <T extends NamedElement<?>, C extends Collection<T>> T findByName(C collection, String name) {
    	return collection.stream().filter(named -> name.equals(named.getName())).findAny().get(); 
    }
}
