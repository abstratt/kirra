qx.Class.define("kirra_qooxdoo.ActionForm", {
    extend: qx.ui.mobile.page.NavigationPage,

    construct: function (repository) {
        this.base(arguments);
        this.repository = repository;
    },


    events: {
        /** The page to show */
        "show": "qx.event.type.Data"
    },


    members: {
        _entityName: null,
        _actionName: null,
        _objectId: null,
        _instance: null,
        _entity: null,
        _action: null,
        _parameterDomains: null,
        _widgets: {},
        
        _back: function () {
            var sourcePath = "/entities/" + this._entityName + "/instances/"
            if (this._objectId) {
                sourcePath += this._objectId;
            }
            qx.core.Init.getApplication().getRouting().executeGet(sourcePath, {
                reverse: true
            });
        },
        
        // overridden
        _initialize: function () {
            this.base(arguments);
        },
        _start: function () {
            this.base(arguments);
        },
        showFor: function (entityName, actionName, objectId) {
            var me = this;
            me._widgets = {},
            me._entityName = entityName;
            me._actionName = actionName;
            me._objectId = objectId;
            me._entity = null;
            me._instance = null;
            me._action = null;
            me._parameterDomains = {};
            me.repository.loadEntity(entityName, function (entity) {
                me._entity = entity;
                me._action = entity.operations[me._actionName];
                me.buildForm();
            });
            this.show();
        },

        buildForm: function () {

            var me = this;

            if (!this.getContent() || !this._entity) {
                return;
            }
            
            this.setTitle(this._action.description ? this._action.description : this._action.label);

            this.getContent().removeAll();

            var form = this.form = new qx.ui.mobile.form.Form();
            for (var i in this._action.parameters) {
                this.buildWidgetFor(form, this._action.parameters[i]);
            }
            var actionFormRenderer = new qx.ui.mobile.form.renderer.Single(form);
            this.getContent().add(actionFormRenderer);
            
            this.addToolbar();

            this.show();
        },
        
        addToolbar: function () {
            if (this.toolbar)
                this.toolbar.destroy();
            var toolbar = this.toolbar = new qx.ui.mobile.toolbar.ToolBar();
            this.add(toolbar);
            this.addBasicButtons();
        },

        addBasicButtons: function () {
            var me = this;
            var cancelButton = new qx.ui.mobile.form.Button("Cancel");
            cancelButton.addListener("tap", function () {
                me._back();
            });
            this.toolbar.add(cancelButton);
            var okButton = new qx.ui.mobile.form.Button(this._action.label);
            okButton.addListener("tap", function () {
                me.executeAction();
            });
            this.toolbar.add(okButton);
            
        },
        
        resultHandler : function () {
            var me = this;
            return function () {
                me._back();
            };
        },

        executeAction: function () {
            var me = this;
            var arguments = {};
            for (var i in this._action.parameters) {
                var parameterName = this._action.parameters[i].name;
                if (this._action.parameters[i].typeRef.kind == 'Entity') {
                    var referenceIndex = me._widgets[parameterName]._getAttribute("index");
                    arguments[parameterName] = me._parameterDomains[parameterName][referenceIndex];
                } else {
                    arguments[parameterName] = me._widgets[parameterName].getValue();
                }
            }
            if (me._action.instanceOperation) {
                me.repository.sendAction(me._entity, me._objectId, me._action, arguments, me.resultHandler());
            } else {
                me.repository.sendStaticAction(me._entity, me._action, arguments, me.resultHandler());
            }
        },

        buildWidgetFor: function (form, parameter) {
            var widget = kirra_qooxdoo.Widgets.createWidget(parameter, this.instanceEnumerator(parameter));
            this._widgets[parameter.name] = widget;
            form.add(widget, parameter.label);
        },
        
        instanceEnumerator: function (parameter) {
            var me = this;
            var kind = parameter.typeRef && parameter.typeRef.kind;
            if (kind !== 'Entity') {
                return me.noOpValueEnumerator;
            }
            return function(parameter, callback) {
                me.repository.listParameterDomain(me._entity, me._objectId, me._action, parameter, function (objects) {
                    me._parameterDomains[parameter.name] = objects.contents;
                    var shorthands = [];
                    for (var i in objects.contents) {
                        shorthands.push(objects.contents[i].shorthand);
                    }
                    callback(shorthands);
                });
            };
        },
        
        noOpValueEnumerator : function (target, callback) {
            callback([]);
        }
    }
});
