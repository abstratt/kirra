var kirra = {};

kirra.newRepository = function(applicationUri) {
    var newRepo = Object.create(this.template);
    newRepo._applicationUri = applicationUri;
    return newRepo;
};

kirra.template = {
    _application: {},
    _entityList: [],
    _applicationUri: null,

    loadApplication: function (callback)	 {
        this.load(this._applicationUri, callback, "_application");
    },
    
    loadEntities: function (callback, retry) {
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

    loadEntity: function (entityFullName, callback, retry) {
        if (!entityFullName)
            throw Error("Missing entityFullName: " + entityFullName);
        for (var i in this._entityList) {
            if (this._entityList[i].fullName == entityFullName) {
                var entityUri = this._entityList[i].uri;
                this.load(entityUri, callback, "entity." + entityFullName);
                return;
            }
        }
        if (retry === false)
            throw Error("Entity not found: " + entityFullName);
        // entity not found - reload entities before giving up
        var me = this;
        this.loadEntities(function () {
            // turn retry off
            me.loadEntity(entityFullName, callback, false);
        });
    },
    
    /* Generic helper for performing Ajax invocations. */
    load: function (uri, callback, slotName) {
        var me = this;
        var request = new XMLHttpRequest();
        console.log(uri);
        request.open("GET", uri, true);
        request.onreadystatechange = function() {
            console.log(request.readyState);
            console.log(request.responseURL);            
            if (request.readyState !== 4) {
                return;
            }
            if (request.responseText) {
                var parsedResponse = JSON.parse(request.responseText);
		        if (slotName) {
		            me[slotName] = parsedResponse;
	            }
            	callback(parsedResponse);
        	}
        };
        request.send();
    }
};
