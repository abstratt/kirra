qx.Class.define("kirra_qooxdoo.InstanceNavigator", {
    extend: qx.ui.mobile.page.NavigationPage,

    construct: function (repository) {
        this.base(arguments);
        this.repository = repository;
        this.setTitle("Instances");
        this.setShowBackButton(true);
        this.setBackButtonText("Back");
    },


    events: {
        /** The page to show */
        "show": "qx.event.type.Data"
    },


    members: {
        _entityName: null,
        _entity : null,
        _toolbar : null,
        _instanceList : null,
        // overridden
        _initialize: function () {
            this.base(arguments);
            var me = this;
            var list = this._instanceList = new qx.ui.mobile.list.List({
                configureItem: function (item, data, row) {
                    item.setTitle(data.shorthand);
                    item.setSubtitle("");
                    item.setShowArrow(true);
                    item.data = data;
                }
            });
            this.getContent().add(list);
            list.addListener("changeSelection", function (evt) {
                var instanceSelected = me._instanceList.getModel().getItem(evt.getData());
                qx.core.Init.getApplication().getRouting().executeGet("/entity/" + me._entityName + "/instances/" + instanceSelected.objectId);
            }, this);
        },
        _start: function () {
            this.base(arguments);
        },
        _back: function () {
            qx.core.Init.getApplication().getRouting().executeGet("/", {
                reverse: true
            });
        },
        showFor: function (entityName) {
            var me = this;
            this._entityName = entityName;
            me.show();
            me.reloadInstances();
            this.repository.loadEntity(this._entityName, function (entity) { 
                me._entity = entity;
                me.buildActions();
            });
        },
        reloadInstances : function () {
            var me = this;
            this.repository.loadInstances(this._entityName, function (instances) {
                me._instanceList.setModel(new qx.data.Array(instances.contents));
                me.setTitle(me._entityName);
            });
        },
        buildActions : function () {
            var me = this;
            if (this._toolbar)
                this._toolbar.destroy();
            var toolbar = this._toolbar = new qx.ui.mobile.toolbar.ToolBar();
            this.add(toolbar);

            if (this._entity.instantiable) {
                var actionButton = new qx.ui.mobile.form.Button("New");
                actionButton.addListener("tap", function () {
                    qx.core.Init.getApplication().getRouting().executeGet("/entity/" + me._entityName + "/instances/_template");
                });
                toolbar.add(actionButton);
            } 

            var allOps = this._entity.operations;
            var staticActions = [];
            for (var opName in allOps) {
                var operation = allOps[opName];
                if (!operation.instanceOperation && operation.kind === "Action") {
                    staticActions.push(operation);
                }
            }
            if (staticActions.length == 0)
                return;
            for (var i in staticActions) {
                var actionButton = new qx.ui.mobile.form.Button(staticActions[i].label);
                actionButton.addListener("tap", function () {
                    if (staticActions[i].parameters.length === 0) {
                        me.repository.sendStaticAction(me._entity, staticActions[i], {}, function () { me.reloadInstances(); });
                    } else {
                        qx.core.Init.getApplication().getRouting().executeGet("/entity/" + me._entityName + "/actions/" + staticActions[i].name); 
                    }
                });
                toolbar.add(actionButton);
            }
        }

    }
});
