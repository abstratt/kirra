This is a generic mobile-styled HTML5/JavaScript client for any Kirra-compliant applications. It is implemented using the [Qooxdoo](http://qooxdoo.org) Javascript framework.

In order to work on this project, you need to:

- download and install the [Qooxdoo SDK](http://qooxdoo.org/downloads).
- create a symbolic link called 'qooxdoo' from ../../qooxdoo to the path where you installed the Qooxdoo SDK
- run "./generate.py" whenever you add new dependencies to Qooxdoo classes
- run "./generate.py source-server" to run a local webserver where you can test the application (under [this](http://localhost:9999/kirra-api/kirra_qooxdoo/source/?app-path=/kirra-api/com.abstratt.kirra.fixtures/src/fixtures/index.json) localhost URL)

You can also try it out against (in read-only mode) against static test fixtures at this URL:

http://abstratt.github.io/kirra/kirra_qooxdoo/source/?app-path=/kirra/com.abstratt.kirra.fixtures/src/fixtures/index.json
