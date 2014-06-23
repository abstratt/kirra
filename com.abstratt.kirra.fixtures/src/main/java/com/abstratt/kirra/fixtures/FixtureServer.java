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

import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.rest.resources.KirraContext;
import com.abstratt.kirra.rest.resources.KirraJaxRsApplication;
import com.abstratt.kirra.rest.resources.KirraRestException;

/**
 * A simple Jetty-powered server that exposes one application based on sample
 * data collected from Java resources.
 */
public class FixtureServer {
    public static void main(String... args) throws Exception {
        FixtureServer server = new FixtureServer();
        server.start();
        System.out.println("Server at " + FixtureServer.TEST_SERVER_URI);
    }

    public static final String TEST_CONTEXT_PATH = "/kirra";
    public static final String TEST_APPLICATION_PATH = FixtureServer.TEST_CONTEXT_PATH + "/app/";
    public static final int TEST_SERVER_PORT = Integer.parseInt(System.getProperty("kirra.fixtures.port", "38080"));
    public static final URI TEST_DEFAULT_SERVER_URI = URI.create("http://localhost:" + FixtureServer.TEST_SERVER_PORT
            + FixtureServer.TEST_APPLICATION_PATH);

    public static final URI TEST_SERVER_URI = URI.create(System.getProperty("kirra.fixtures.uri",
            FixtureServer.TEST_DEFAULT_SERVER_URI.toString()));
    private InstanceManagement serverInstanceManagement;
    private SchemaManagement serverSchemaManagement;

    private Server webServer;

    public FixtureServer() {
        webServer = new Server(FixtureServer.TEST_SERVER_PORT);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath(FixtureServer.TEST_CONTEXT_PATH);
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
            public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                KirraContext.setSchemaManagement(serverSchemaManagement);
                KirraContext.setInstanceManagement(serverInstanceManagement);
                KirraContext.setBaseURI(FixtureServer.TEST_SERVER_URI);
                try {
                    super.service(request, response);
                } finally {
                    KirraContext.setSchemaManagement(null);
                    KirraContext.setInstanceManagement(null);
                    KirraContext.setBaseURI(null);
                }
            }

            @Override
            protected Application createApplication(Context parentContext) {
                JaxRsApplication jaxRsApplication = new JaxRsApplication(parentContext);
                jaxRsApplication.setStatusService(statusService);
                jaxRsApplication.add(new KirraJaxRsApplication());
                return jaxRsApplication;
            }
        }), "/*");
        webServer.setHandler(context);
        this.serverSchemaManagement = new SchemaManagementOnFixtures();
        this.serverInstanceManagement = new InMemoryInstanceManagement(this.serverSchemaManagement);
    }

    public void start() throws Exception {
        webServer.start();
    }

    public void stop() throws Exception {
        webServer.stop();
        webServer.join();
    }
}
