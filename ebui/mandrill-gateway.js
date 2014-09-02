var https = require("https");

var MandrillGateway = function (mandrillKey, defaultFromEmail, defaultFromName) {
    var self = this;
    
    self.handleInboundEmail = function (req, res, messageStore) {
        var events = req.body.mandrill_events || [];
        if (typeof events === "string") {
            events = JSON.parse(events);
        }
        events.forEach(function (event) {
            var newMessage = {
                received: new Date(),
                account: event.msg.email,
                fromEmail: event.msg.from_email,
                fromName: event.msg.from_name,
                toEmail: event.msg.to,
                subject: event.msg.subject,
                text: event.msg.text,
                status: 'Pending',
                // so we can reply in context later
                _contextMessageId: event.msg.headers['Message-Id']
            };
            messageStore.saveMessage(newMessage);
        });
        return res.send(204);
    };
    
    self.replyToSender = function(message, body, senderEmail) {
        var payload = {
            key : mandrillKey,
            message: {
                text: "This is an automated response to your message to "+ message.account + "\n\n" + body,
                from_email: senderEmail || defaultFromEmail,
                from_name: defaultFromName,
                subject: (message.subject && message.subject.indexOf("Re:") === -1) ? ("Re: "+ message.subject) : message.subject,
                to: [{
                    email: message.fromEmail,     
                    name: message.fromName,
                    type: "to"
                }],
                headers: { 
                    'In-Reply-To': message._contextMessageId
                }
            }
        };
	    var options = {
	      hostname: 'mandrillapp.com',
	      path: '/api/1.0/messages/send.json',
	      method: 'POST',
              headers: { 'content-type': 'application/json' }
	    };
        var req = https.request(options, function(res) {
              res.on('data', function(d) {
                  process.stdout.write(d);
              });
        });
        req.write(JSON.stringify(payload)); 
        req.end();
	    req.on('error', function(e) {
	      console.error(e);
	    });
    };
    
    return self;
}

var exports = module.exports = MandrillGateway;
