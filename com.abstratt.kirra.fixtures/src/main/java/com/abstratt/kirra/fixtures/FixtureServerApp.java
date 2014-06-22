package com.abstratt.kirra.fixtures;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class FixtureServerApp implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		FixtureServer server = new FixtureServer();
		server.start();
		System.out.println("Server at " + FixtureServer.TEST_SERVER_URI);
		return null;
	}

	public void stop() {
		// nothing to do here
	}

}
