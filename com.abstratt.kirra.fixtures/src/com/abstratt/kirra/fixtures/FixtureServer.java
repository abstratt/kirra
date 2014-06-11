package com.abstratt.kirra.fixtures;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.ext.servlet.ServerServlet;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.service.StatusService;

import com.abstratt.kirra.rest.resources.KirraContext;
import com.abstratt.kirra.rest.resources.KirraJaxRsApplication;
import com.abstratt.kirra.rest.resources.KirraRestException;

public class FixtureServer extends Server {
	public static final int TEST_SERVER_PORT = 38080;
	public static final String TEST_CONTEXT_PATH = "/kirra";
	public static final String TEST_APPLICATION_PATH = TEST_CONTEXT_PATH + "/appName/";
	public static final URI TEST_SERVER_URI = URI.create("http://localhost:" + TEST_SERVER_PORT + TEST_APPLICATION_PATH);

    private SchemaManagementOnFixtures serverSchemaManagement;

    public FixtureServer() {
    	super(TEST_SERVER_PORT);
		ServletContextHandler context = new ServletContextHandler();
        context.setContextPath(TEST_CONTEXT_PATH);
        StatusService statusService = new StatusService() {
        	@Override
        	public Representation getRepresentation(Status status, Request request, Response response) {
				return new StringRepresentation("{ \"message\": \"" + status.getDescription() + "\"}", MediaType.APPLICATION_JSON);
        	}
        	
        	@Override
        	public Status getStatus(Throwable throwable, Request request, Response response) {
        		Throwable original = throwable;
        		while (throwable.getCause() instanceof KirraRestException)
        			throwable = throwable.getCause();
        		if (throwable instanceof KirraRestException) {
        			KirraRestException restException = (KirraRestException) throwable;
        			return new Status(Status.valueOf(restException.getStatus().getStatusCode()), restException.getMessage());
        		}
        		return super.getStatus(original, request, response);
        	}

        };
        context.addServlet(new ServletHolder(new ServerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
        	protected Application createApplication(Context parentContext) {
        		JaxRsApplication jaxRsApplication = new JaxRsApplication(parentContext);
        		jaxRsApplication.setStatusService(statusService);
        		jaxRsApplication.add(new KirraJaxRsApplication());
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
        setHandler(context);
        this.serverSchemaManagement = new SchemaManagementOnFixtures();
    }
    
    public static void main(String... args) throws Exception {
		FixtureServer server = new FixtureServer();
		server.start();
		System.out.println("Server at "+ TEST_SERVER_URI);
	}
}
