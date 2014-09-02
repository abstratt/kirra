#!/bin/env node

var q = require('q');
var express = require('express');
var fs = require('fs');
var bodyParser = require('body-parser');

var url = require("url");

var yaml = require("js-yaml");

var MessageStore = require("./message-store.js");

var MessageProcessor = require("./message-processor.js");

var MandrillGateway = require("./mandrill-gateway.js");

var EBUIApp = function() {

    var self = this;


    /*  ================================================================  */
    /*  Helper functions.                                                 */
    /*  ================================================================  */

    /**
     *  Set up server IP address and port # using env variables/defaults.
     */
    self.setupVariables = function() {
        //  Set the environment variables we need.
        self.ipaddress = process.env.OPENSHIFT_NODEJS_IP || "127.0.0.1";
        self.port = process.env.OPENSHIFT_NODEJS_PORT || 8080;
        self.dbhost = process.env.OPENSHIFT_MONGODB_DB_HOST || self.ipaddress;
        self.dbport = process.env.OPENSHIFT_MONGODB_DB_PORT || 27017;
        self.dbname = process.env.OPENSHIFT_MONGODB_DB_NAME || 'mailflowjs';
        self.dbusername = process.env.OPENSHIFT_MONGODB_DB_USERNAME || '';
        self.dbpassword = process.env.OPENSHIFT_MONGODB_DB_PASSWORD || '';
        self.mandrillKey = process.env.MANDRILL_API_KEY || '';
        self.fromEmail = process.env.FROM_EMAIL || 'support@cloudfier.com';  
        self.fromName = process.env.FROM_NAME || 'Cloudfier Support';
        self.baseUrl = process.env.BASE_URL || ("http://" + self.ipaddress + "/");
        self.kirraBaseUrl = process.env.KIRRA_API_URL || "http://develop.cloudfier.com/services/api-v2/";

        console.log("base url: \"" + self.baseUrl + '"');
        console.log("Kirra API url: \"" + self.kirraBaseUrl + '"');
        console.log("fromEmail: \"" + self.fromEmail + '"');
        console.log("fromName: \"" + self.fromName + '"');
    };

    /**
     *  terminator === the termination handler
     *  Terminate server on receipt of the specified signal.
     *  @param {string} sig  Signal to terminate on.
     */
    self.terminator = function(sig) {
        if (typeof sig === "string") {
            console.log('%s: Received %s - terminating the app ...',
                Date(Date.now()), sig);
            process.exit(1);
        }
        console.log('%s: Node server stopped.', Date(Date.now()));
    };


    /**
     *  Setup termination handlers (for exit and a list of signals).
     */
    self.setupTerminationHandlers = function() {
        //  Process on exit and signals.
        process.on('exit', function() {
            self.terminator();
        });

        // Removed 'SIGPIPE' from the list - bugz 852598.
        ['SIGHUP', 'SIGINT', 'SIGQUIT', 'SIGILL', 'SIGTRAP', 'SIGABRT',
            'SIGBUS', 'SIGFPE', 'SIGUSR1', 'SIGSEGV', 'SIGUSR2', 'SIGTERM'
        ].forEach(function(element, index, array) {
            process.on(element, function() {
                self.terminator(element);
            });
        });
    };


    /**
     *  Initialize the server (express) and create the routes and register
     *  the handlers.
     */
    self.initializeServer = function() {
        self.app = express();
        self.app.use(bodyParser.urlencoded({
            extended: true
        }));
        self.app.use(bodyParser.json());

        self.app.use(function(req, res, next) {
            req.messageStore = self.messageStore;
            next();
        });
        self.app.get("/", function(req, res) {
            res.json({
                messages: self.resolveUrl('messages/')
            });
        });

        self.app.post("/events/", function(req, res) {
            return self.mandrillGateway.handleInboundEmail(req, res, self.messageStore);
        });

        self.app.get("/events/", function(req, res) {
            // mandrill sends a HEAD
            res.send(204);
        });


        self.app.get("/messages/", function(req, res) {
            req.messageStore.getAllMessages().then(function(docs) {
                res.json(docs || []);
            });
        });
    };


    /**
     *  Initializes the application.
     */
    self.initialize = function() {
        self.setupVariables();
        self.setupTerminationHandlers();

        self.messageStore = new MessageStore(self.dbhost, self.dbport, self.dbname, self.dbusername, self.dbpassword);        
        self.mandrillGateway = new MandrillGateway(self.mandrillKey, self.fromEmail, self.fromName);
        self.messageProcessor = new MessageProcessor(self.mandrillGateway, self.messageStore, self.kirraBaseUrl);

        // Create the express server and routes.
        self.initializeServer();
    };


    /**
     *  Start the server (starts up the application).
     */
    self.start = function() {
        //  Start the app on the specific interface (and port).
        self.app.listen(self.port, self.ipaddress, function() {
            console.log('%s: Node server started on %s:%d ...',
                Date(Date.now()), self.ipaddress, self.port);
        });
        
        var interval;
        interval = setInterval(function() {
            console.log('Processing pending messages');
            self.processPendingMessages();
            console.log('Done');
        }, 10000);
    };

    self.replyToSender = function(message, body, senderEmail) {
        return self.mandrillGateway.replyToSender(message, body, senderEmail);
    };

    self.processPendingMessages = function () {
        return self.messageStore.getPendingMessages('messages').each(function (message) {
            self.processPendingMessage(message);
        });
    };

    self.processPendingMessage = function (message) {
        console.log("Processing " + message._id + "...");
        return self.messageProcessor.processPendingMessage(message);
    };

    self.resolveUrl = function(relative) {
        return self.baseUrl + relative;
    };
};


var ebuiApp = new EBUIApp();
ebuiApp.initialize();
ebuiApp.start();

var app = exports = module.exports = ebuiApp;
