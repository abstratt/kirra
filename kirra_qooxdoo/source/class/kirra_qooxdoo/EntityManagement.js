qx.Class.define("kirra_qooxdoo.EntityManagement",
{
  extend : qx.core.Object,
  
  members :
  {
    entityListListeners : [],
    
    addEntityListListener : function(listener) {
        this.entityListListeners.push(listener);
    },

    loadApplication : function(applicationUri) {
        var me = this;
        this.load(applicationUri, function (response) {
            me.loadEntities(response.entities);
    	});    
    },
    
    loadEntities : function(entityListUri) {
        var me = this;
        this.load(entityListUri, function (response) {
            for (var i in me.entityListListeners) {
                me.entityListListeners[i](response);
            } 
	    });    
    },
    
    /* Generic helper for performing Ajax invocations. */
    load : function(uri, successHandler) {
	    var req = new qx.io.request.Xhr(uri);
		req.addListener("success", function(e) {
		  var req = e.getTarget();
		  var response = req.getResponse();
		  successHandler(response);
		}, this);    
        req.send();
    }
  }
});
