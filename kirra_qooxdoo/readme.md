This is a generic mobile-styled HTML5/JavaScript client for any Kirra-compliant applications. It is implemented using the [Qooxdoo](http://qooxdoo.org) Javascript framework.

In order to work on this project, you need to:

- download and install the [Qooxdoo Mobile SDK](http://qooxdoo.org/downloads) (latest we tested with was Qooxdoo SDK 3.5).
- create a symbolic link called 'qooxdoo' from ../../qooxdoo to the path where you installed the Qooxdoo SDK
- run "./generate.py" whenever you add new dependencies to Qooxdoo classes
- run "./generate.py source-server" to run a local webserver where you can test the application (under [this](http://localhost:9999/kirra-api/kirra_qooxdoo/source/?app-path=/kirra-api/com.abstratt.kirra.fixtures/src/main/resources/fixtures/index.json) localhost URL - note it is read-only as it will run against static files). You must use either the app-path (in the same server/port or behind a common reverse proxy) or app-uri (required whenever the UI server and the REST API server are running under a different host and/or port) query parameters. 

You can also try it out against a Cloudfier-based application using this URL:

http://develop.cloudfier.com/kirra-api/kirra_qooxdoo/build/?app-path=/services/api-v2/demo-cloudfier-examples-taxi-fleet
