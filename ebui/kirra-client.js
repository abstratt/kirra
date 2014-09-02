var http = require("http");
var url = require("url");
var q = require('q');

var ebuiUtil = require("./util.js");

var assert = ebuiUtil.assert;
var merge = ebuiUtil.merge;

var Kirra = function (baseUrl, application) {
    var self = this;
    
    assert(baseUrl, "baseUrl missing");
    assert(application, "application missing");
    
    self.baseUrl = baseUrl;
    
    self.application = application;    
    
    self.performRequest = function(path, method, expectedStatus, body) {
        var parsedKirraBaseUrl = url.parse(self.baseUrl);	        
        var options = {
          hostname: parsedKirraBaseUrl.hostname,
          path: parsedKirraBaseUrl.pathname + self.application + path,
          method: method || 'GET',
          headers: { 'content-type': 'application/json' }
        };
        console.log("Kirra request: " + JSON.stringify(options));
        var deferred = q.defer();
        var req = http.request(options, function(res) {
            res.on('data', function(d) {
                var parsed = JSON.parse(d);
                if (
                    (typeof expectedStatus === 'number' && expectedStatus !== res.statusCode) || 
                    (typeof expectedStatus === 'object' && expectedStatus.indexOf(res.statusCode) === -1)
                ) {
                    console.log("Expected: " + expectedStatus + " - Actual: " + res.statusCode);
                    deferred.reject(parsed);
                } else {
                    deferred.resolve(parsed);
                }
            });
        });
        if (body) {
            req.write(JSON.stringify(body)); 
        }
        req.end();
        req.on('error', function(e) {
            deferred.reject(e);
        });
        return deferred.promise;
    };
    
    self.createInstance = function(message) {
        return self.getInstanceTemplate(message).then(function (template) {
		    var mergedValues = merge(merge({}, message.values), template.values);
            return self.performRequest('/entities/' + message.entity + '/instances/', 'POST', [201, 200], 
                { values: mergedValues }
            );
	    });
    };

    self.updateInstance = function(message) {
        return self.getInstance(message).then(function (existing) {
		    var mergedValues = merge(merge({}, message.values), existing.values);
		    return self.performRequest('/entities/' + message.entity + '/instances/' + message.objectId, 'PUT', 200, 
		        { values: mergedValues }
	        );
	    });
    };

    self.getInstance = function(message) {
        return self.performRequest('/entities/' + message.entity + '/instances/' + message.objectId, undefined, 200);
    };

    self.getInstanceTemplate = function(message) {
        return self.performRequest('/entities/' + message.entity + '/instances/_template', undefined, 200);
    };
    
    return self;
};

var exports = module.exports = Kirra;
