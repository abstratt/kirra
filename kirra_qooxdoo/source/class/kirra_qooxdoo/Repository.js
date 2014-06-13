qx.Class.define("kirra_qooxdoo.Repository",
{
  extend : qx.core.Object,
  
  construct : function(applicationUri)
  {
    this.base(arguments);
    applicationUri = applicationUri.replace(/\/$/, "");
    this._parsedApplicationUri = qx.util.Uri.parseUri(applicationUri);
    this._applicationUri = applicationUri;
  },
  
  members :
  {
    _application : {},
    _entityList : [],
    _applicationUri : null, 
    _parsedApplicationUri : null,
    
    loadApplication : function(callback) {
        this.load(this._applicationUri, callback, "_application");    
    },
    
    loadEntities : function(callback, retry) {
        var me = this;
        if (!me._application.entities) {
            if (retry === false)
                return;
            this.loadApplication(function () {
                me.loadEntities(callback, false);    
            });
            return;
        }
        this.load(me._application.entities, callback, "_entityList");    
    },
    
    loadEntity : function(entityName, callback, retry) {
        if (!entityName)
		    throw Error("Missing entityName: "+ entityName);
        for (var i in this._entityList) {
            if (this._entityList[i].name == entityName) {
                var entityUri = this._entityList[i].uri;
                this.load(entityUri, callback, "entity." + entityName);  
                return;
            }
        }
        if (retry === false)
            throw Error("Entity not found: " + entityName);
        // entity not found - reload entities before giving up
        var me = this;
        this.loadEntities(function () {
            // turn retry off
            me.loadEntity(entityName, callback, false);
        });
    },
    
    loadInstances : function(entityName, callback, retry) {
        if (!entityName)
		    throw Error("Missing entityName: "+ entityName);
        for (var i in this._entityList) {
            if (this._entityList[i].name == entityName) {
                var extentUri = this._entityList[i].extentUri;
                this.load(extentUri, function (instances) {
                    callback(instances);             
                });  
                return;
            }
        }
        if (retry === false)
            throw Error("Entity not found: " + entityName);
                // entity not found - reload entities before giving up
        var me = this;
        this.loadEntities(function () {
            // turn retry off
            me.loadInstances(entityName, callback, false);
        });
    },
    
    loadInstance : function(entity, objectId, callback) {
        if (!entity || !entity.extentUri)
            throw Error("Missing entity or extentUri");
        if (!objectId)
            throw Error("Missing objectId");
        var instanceUri = entity.instanceUriTemplate || (entity.extentUri + objectId);
        this.load(instanceUri.replace("(objectId)", objectId), callback);  
    },
    
    sendAction : function(entity, objectId, operation, callback) {
        var me = this;
        console.log("Running action");
        console.log(objectId);
        console.log(operation);
        var instanceActionUri = entity.instanceActionUriTemplate || (entity.extentUri + objectId + "/actions/" + operation.name) ;
        this.post(instanceActionUri, {}, function () {
            me.loadInstance(entity, objectId, callback);
        });
    },
    
    
    /* Generic helper for performing Ajax invocations. */
    load : function(uri, callback, slotName) {
        var req = this.buildRequest(uri);
	    
		req.addListener("success", function(e) {
		  var req = e.getTarget();
		  var response = req.getResponse();
		  if (slotName)
		      this[slotName] = response;
		  console.log(uri);
		  console.log(response);
		  if (typeof(response) === 'string')
		      response = JSON.parse(response);
		  callback(response);
		}, this);    
        req.send();
    },
    
    post : function(uri, data, callback) {
        var req = this.buildRequest(uri, "POST");
        req.setRequestHeader("Content-Type", "application/json");
        req.setRequestData(qx.util.Serializer.toJson(data, (function() {}), new qx.util.format.DateFormat("yyyy/MM/dd")));
        req.addListener("success", function(e) {
          var req = e.getTarget();
          var response = req.getResponse();
          console.log(response);
          if (callback)
              callback(response);
        }, this);    
        req.send();
    },
    
    
    buildRequest : function (uri, method) {
        var parsedUri = qx.util.Uri.parseUri(uri);
        if (!parsedUri.protocol) {
            uri = this._parsedApplicationUri.protocol + "://" + this._parsedApplicationUri.host + (this._parsedApplicationUri.port ? (":"+this._parsedApplicationUri.port) : "") + this._parsedApplicationUri.directory + uri;
        }
        return new qx.io.request.Xhr(uri, method);
    
    }
  }
});
