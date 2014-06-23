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
        _widgets: {},
        
        _back: function () {
            var sourcePath = "/entity/" + this._entityName + "/instances/"
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
            var okButton = new qx.ui.mobile.form.Button(this._action.label);
            okButton.addListener("tap", function () {
                me.executeAction();
            });
            this.toolbar.add(okButton);

            var cancelButton = new qx.ui.mobile.form.Button("Cancel");
            cancelButton.addListener("tap", function () {
                me._back();
            });
            this.toolbar.add(cancelButton);
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
                var parameterName = this._action.parameters[i].name
                arguments[parameterName] = me._widgets[parameterName].getValue();
            }
            if (me._action.instanceOperation) {
                me.repository.sendAction(me._entity, me._objectId, me._action, arguments, me.resultHandler());
            } else {
                me.repository.sendStaticAction(me._entity, me._action, arguments, me.resultHandler());
            }
        },

        buildWidgetFor: function (form, property) {
            var widget = kirra_qooxdoo.Widgets.createWidget(property);
            this._widgets[property.name] = widget;
            form.add(widget, property.label);
        }

    }
});
