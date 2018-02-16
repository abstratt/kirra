package com.abstratt.kirra.rest.tests;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.function.Consumer;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.KirraApplication;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.InstanceManagement.PageRequest;
import com.abstratt.kirra.rest.client.InstanceManagementOnREST;
import com.abstratt.kirra.rest.client.RestClient;
import com.abstratt.kirra.rest.client.SchemaManagementOnREST;
import com.abstratt.kirra.rest.common.KirraContext;

import junit.framework.TestCase;

public class AbstractRestTests extends TestCase {

    private static final String KIRRA_SERVER_URI_STRING = System.getProperty("kirra.uri");
    protected URI kirraServerUri;
    private String kirraRealm = System.getProperty("kirra.realm", "default");
    protected String kirraAdminUsername = System.getProperty("kirra.admin.username");
    protected String kirraAdminPassword = System.getProperty("kirra.admin.password");
    protected String kirraEmployeeUsername = System.getProperty("kirra.employee.username");
    protected String kirraEmployeePassword = System.getProperty("kirra.employee.password");
    private RestClient restClient;
	protected InstanceManagement instanceManagement;
    protected SchemaManagement schemaManagement;

    @Override
    public void setUp() throws Exception {
    	assertNotNull("kirra.uri not set", KIRRA_SERVER_URI_STRING);
    	assertNotNull("kirra.realm not set", kirraRealm);
    	assertNotNull("kirra.admin.username not set", kirraAdminUsername);
    	assertNotNull("kirra.admin.password not set", kirraAdminPassword);
    	assertNotNull("kirra.employee.username not set", kirraEmployeeUsername);
    	assertNotNull("kirra.employee.password not set", kirraEmployeePassword);
    	
    	this.kirraServerUri = URI.create(KIRRA_SERVER_URI_STRING);
    	login(kirraAdminUsername, kirraAdminPassword);
    }

	private void configure() throws IOException {
		restClient = new RestClient(kirraServerUri);
        this.schemaManagement = new SchemaManagementOnREST(restClient, kirraServerUri);
        this.instanceManagement = new InstanceManagementOnREST(restClient, kirraServerUri);
        KirraContext.setInstanceManagement(this.instanceManagement);
        KirraContext.setSchemaManagement(this.schemaManagement);
        KirraContext.setBaseURI(kirraServerUri);
        KirraContext.setApplication(new KirraApplication(getName()));
	}
    
    protected void login(String username, String password) throws IOException {
    	configure();
    	restClient.setCredentials(this.kirraServerUri, this.kirraRealm, username, password);
    }

    @Override
    public void tearDown() throws Exception {
    }

    protected <T extends NamedElement<?>, C extends Collection<T>> T findByName(C collection, String name) {
        return collection.stream().filter(named -> name.equals(named.getName())).findAny().get();
    }
    
	protected Instance getAnyInstance(String namespace, String entityName) {
		return instanceManagement.getInstances(namespace, entityName, new PageRequest(0L, 1, DataProfile.Empty, false)).get(0);
	}
    
    protected Instance createInstance(String namespace, String entity, Consumer<Instance> configurer) {
        Instance newInstance = new Instance(namespace, entity);
        configurer.accept(newInstance);
        Instance created = instanceManagement.createInstance(newInstance);
        return created;
    }

}
