var yaml = require("js-yaml");
var url = require("url");

var Kirra = require("./kirra-client.js");
var ebuiUtil = require("./util.js");

var assert = ebuiUtil.assert;
var merge = ebuiUtil.merge;

var MessageProcessor = function (emailGateway, messageStore, kirraBaseUrl) {
    var self = this;
    
    assert(emailGateway, "emailGateway missing");
    assert(messageStore, "messageStore missing");
    assert(kirraBaseUrl, "kirraBaseUrl missing");
    
    self.messageStore = messageStore;
    self.emailGateway = emailGateway;
    
    self.parseMessage = function (message) {
        var text = message.text;
        var comment = '';
        var processingRules = [];
        var isProcessing = false;
        if (text) {
		    text.split("\n").forEach(function (current) {        
		        if (isProcessing) {
                    if (current.indexOf('--') === 0) {
                        isProcessing = false;
                    } else {
	                    processingRules.push(current);
                    }
		        } else {
		            if (current.indexOf('--') === 0) {
		                isProcessing = true;
		            } else {
		                comment += current + '\n';
		            }
		        }
		    });
        }
        var values = yaml.safeLoad(processingRules.join('\n'));
        message.comment = comment;
        message.values = merge(merge({}, values), message.values);
        // (entity)(-instanceid)?.(application)@<domain>
        //  Examples: issue.my-application@foo.bar.com and issue-43234cc221ad.my-application@foo.bar.com
        var elements = /^([a-z_A-Z]+)(?:-([^.]+))?\.([^@^.]+)@.*$/.exec(message.account);
        if (elements !== null) {
            message.entity = elements[1].replace("_", ".");
            message.instanceId = elements[2];
            message.application = elements[3];
        }
        return message;

    };

    self.processPendingMessage = function (message) {
        console.log("Processing " + message._id + "...");

        self.parseMessage(message);

        if (!message.application) {
            message.status = 'Invalid';
            emailGateway.replyToSender(message, "Unfortunately, your message could not be processed.");
            return messageStore.saveMessage(message).then(function() { return message; });    
        }
        message.status = 'Processing';
        return messageStore.saveMessage(message).then(function () {
            if (message.instanceId) {
                return self.processUpdateMessage(message);
            } else {
                return self.processCreationMessage(message);
            }
        }).then(function() { return message; });
    };
    
    self.makeEmailForInstance = function(message) {
        return message.entity.replace('.', '_') + '-' + message.instanceId + '.' + message.application + '@inbox.cloudfier.com';
    };

    self.processCreationMessage = function(message) {
        var kirraApp = new Kirra(kirraBaseUrl, message.application);
        console.log("kirraApp: "+ JSON.stringify(kirraApp));
        return kirraApp.createInstance(message).then(function (d) {
            message.instanceId = d.objectId;	
            message.status = "Processed";
            self.messageStore.saveMessage(message);
            self.replyToSender(message, "Message successfully processed. Object was created.\n" + yaml.safeDump(d.values, { skipInvalid: true }), self.makeEmailForInstance(message));
            return d;             
        }, self.onError(message, "Error processing your message, object not created."));
    };

    self.processUpdateMessage = function(message) {
        var kirraApp = new Kirra(kirraBaseUrl, message.application);
        return kirraApp.updateInstance(message).then(function (d) {
	        self.replyToSender(message, "Message successfully processed. Object was updated.\n" + yaml.safeDump(d.values, { skipInvalid: true }), self.makeEmailForInstance(message));
	        message.status = "Processed";
	        self.messageStore.saveMessage(message);
	        return d;
        }, self.onError(message, "Error processing your message, object not updated."));
    };
    
    self.replyToSender = function(message, body, senderEmail) {
        emailGateway.replyToSender(message, body, senderEmail);
    };
    
    self.onError = function(message, errorMessage) {
	    return function (e) {
	        console.log("Error: " + errorMessage);
	        console.log(JSON.stringify(e));
		    message.status = "Failure";
		    message.error = e;
		    messageStore.saveMessage(message);
		    self.replyToSender(message, errorMessage + " Reason: " + e.message);
		    return new Error(e.message); 
	    };
    };  
    
    return self;
};


var exports = module.exports = MessageProcessor;
