var mongo = require('mongodb');
var monk = require('monk');
var util = require('util');

var MessageStore = function (dbhost, dbport, dbname, dbusername, dbpassword) {
    var self = this;
    self.dbhost = dbhost;
    self.dbport = dbport;
    self.dbname = dbname;
    self.dbusername = dbusername;
    self.dbpassword = dbpassword;
    self.dbcreds = self.dbusername ? (self.dbusername + ':' + self.dbpassword + '@') : '';
    self.dbConnectionString = self.dbcreds + self.dbhost + ':' + self.dbport + '/' + self.dbname;
    self.db = monk(self.dbConnectionString);
    
    self.saveMessage = function(message) {
        console.log("Saving message");
        console.log(JSON.stringify(message));
        var messages = self.db.get('messages');
        if (message._id) {
            return messages.updateById(message._id, message);
        } else {
            return messages.insert(message);
        }
    };
    
    self.getPendingMessages = function () {
        return self.db.get('messages').find({ 
            $or: [ 
                { status: { $exists: false } }, 
                { status: 'Pending' } 
            ] 
        });
    };
    
    self.getAllMessages = function () {
        return self.db.get('messages').find({});
    };
    
    return self;
};


var exports = module.exports = MessageStore;
