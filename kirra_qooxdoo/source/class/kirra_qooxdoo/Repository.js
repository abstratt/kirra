qx.Class.define("kirra_qooxdoo.Repository", {
    extend: qx.core.Object,

    construct: function (applicationUri) {
        this.base(arguments);
        //applicationUri = applicationUri.replace(/\/$/, "");
        this._parsedApplicationUri = qx.util.Uri.parseUri(applicationUri);
        this._applicationUri = applicationUri;
        var busyIndicator = new qx.ui.mobile.dialog.BusyIndicator("Please wait");
        this._busyPopup = new qx.ui.mobile.dialog.Popup(busyIndicator)
    },

    members: {
        _level: 0,
        _application: {},
        _entityList: [],
        _applicationUri: null,
        _parsedApplicationUri: null,
        _busyPopup: null,

        loadApplication: function (callback) {
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

        loadEntity: function (entityName, callback, retry) {
            if (!entityName)
                throw Error("Missing entityName: " + entityName);
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

        loadInstances: function (entityName, callback, retry) {
            if (!entityName)
                throw Error("Missing entityName: " + entityName);
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

        loadInstance: function (entity, objectId, callback) {
            if (!entity || !entity.extentUri)
                throw Error("Missing entity or extentUri");
            if (!objectId)
                throw Error("Missing objectId");
            var instanceUri = entity.instanceUriTemplate || (entity.extentUri + objectId);
            this.load(instanceUri.replace("(objectId)", objectId), callback);
        },

        saveInstance: function (entity, instance, callback) {
            if (!instance || !instance.uri)
                throw Error("Missing instance or instance URI");
            if (instance.objectId) {
                this.put(instance.uri, instance, callback);
            } else {
                this.post(entity.extentUri, instance, callback);
            }
        },

        deleteInstance: function (instance, callback) {
            if (!instance || !instance.uri)
                throw Error("Missing instance or instance URI");
            this.remove(instance.uri, callback);
        },

        sendAction: function (entity, objectId, operation, arguments, callback) {
            var me = this;
            var instanceActionUri = entity.instanceActionUriTemplate || (entity.extentUri + "(objectId)/actions/(actionName)");
            this.post(instanceActionUri.replace("(objectId)", objectId).replace("(actionName)", operation.name), arguments || {}, function () {
                me.loadInstance(entity, objectId, callback);
            });
        },

        sendStaticAction: function (entity, operation, arguments, callback) {
            var me = this;
            var entityActionUri = entity.entityActionUriTemplate || (entity.uri + "/actions/(actionName)");
            this.post(entityActionUri.replace("(actionName)", operation.name), arguments || {}, callback);
        },

        /* Generic helper for performing Ajax invocations. */
        load: function (uri, callback, slotName) {
            var req = this.buildRequest(uri);
            this.sendRequest(req, callback, slotName);
        },

        remove: function (uri, callback) {
            var req = this.buildRequest(uri, "DELETE");
            this.sendRequest(req, callback);
        },

        post: function (uri, data, callback) {
            var req = this.buildRequest(uri, "POST");
            req.setRequestHeader("Content-Type", "application/json");
            req.setRequestData(qx.util.Serializer.toJson(data, (function () {}), new qx.util.format.DateFormat("yyyy/MM/dd")));
            this.sendRequest(req, callback);
        },

        put: function (uri, data, callback) {
            var req = this.buildRequest(uri, "PUT");
            req.setRequestHeader("Content-Type", "application/json");
            req.setRequestData(qx.util.Serializer.toJson(data, (function () {}), new qx.util.format.DateFormat("yyyy/MM/dd")));
            this.sendRequest(req, callback);
        },

        sendRequest: function (req, callback, slotName) {
            var me = this;

            if (me._level == 0)
                me._busyPopup.toggleVisibility();
            me._level = me._level + 1

            req.addListener("success", function (e) {
                var req = e.getTarget();
                var response = undefined
                if (req.getStatus() !== 204) {
		        response = req.getResponse();
		        if (slotName)
		            me[slotName] = response;
		        if (typeof (response) === 'string')
		            response = JSON.parse(response);
                }
                console.log(req.method + " " + req.getUrl());
                console.log(response);
                if (callback)
                    callback(response);
            }, this);

            req.addListener("loadEnd", function (e) {
                me._level = me._level - 1;
                if (me._level === 0)
                    me._busyPopup.hide();
            }, this);
            req.send();
        },

        buildRequest: function (uri, method) {
            var parsedUri = qx.util.Uri.parseUri(uri);
            if (!parsedUri.protocol) {
                uri = this._parsedApplicationUri.protocol + "://" + this._parsedApplicationUri.host + (this._parsedApplicationUri.port ? (":" + this._parsedApplicationUri.port) : "") + this._parsedApplicationUri.directory + uri;
            }
            return new qx.io.request.Xhr(uri, method || 'GET');
        }
    }
});
