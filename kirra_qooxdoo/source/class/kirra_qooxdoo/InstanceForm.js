qx.Class.define("kirra_qooxdoo.InstanceForm", {
    extend: qx.ui.mobile.page.NavigationPage,

    construct: function (repository) {
        this.base(arguments);
        this.repository = repository;
        this.setShowBackButton(true);
        this.setBackButtonText("Back");
    },


    events: {
        /** The page to show */
        "show": "qx.event.type.Data"
    },


    members: {
        _entityName: null,
        _objectId: null,
        _instance: null,
        _entity: null,
        _widgets: {},
        _relationshipTabs : {}, 
        _relationshipMenuButton : null,
        _actions: [],
        _relationshipDomains: null,
        _backPath: null,
        _showRelationships : false,
        // overridden
        _initialize: function () {
            this.base(arguments);
        },
        _start: function () {
            this.base(arguments);
        },
        _back: function () {
            qx.core.Init.getApplication().goBack(this._backPath);
        },
        showFor: function (entityName, id) {
            var me = this;
            me._widgets = {},
            me._entityName = entityName;
            me._objectId = id;
            me._entity = null;
            me._instance = null;
            me._relationshipDomains = {};
            me._relationshipTabs = {},
            me._showRelationships = true;
            me._backPath = "/entities/" + entityName + "/instances/";
            me.repository.loadEntity(entityName, function (entity) {
                me._entity = entity;
                me.buildForm();
                me.repository.loadInstance(entity, me._objectId, me.instanceLoader());
            });
            this.show();
        },
        showForRelated: function (parentEntityName, parentObjectId, relationshipName, childObjectId) {
            var me = this;
            me._widgets = {},
            me._entityName = null;
            me._objectId = null;
            me._entity = null;
            me._instance = null;
            me._relationshipDomains = {};
            me._relationshipTabs = {},
            me._showRelationships = true;
            me._backPath = "/entities/" + parentEntityName + "/instances/" + parentObjectId + "/relationships/" + relationshipName;
            me.repository.loadEntity(parentEntityName, function (parentEntity) {
                var relationship = parentEntity.relationships[relationshipName];
                me._entityName = relationship.typeRef.fullName;
                me._objectId = childObjectId;
                me.repository.loadEntity(relationship.typeRef.fullName, function (childEntity) {
                    me._entity = childEntity;
                    me.buildForm();
                    me.repository.loadInstance(childEntity, me._objectId, me.instanceLoader());
                });
            });
            this.show();
        },
        instanceLoaded: function (instance) {
            var me = this;
            me._instance = instance;
            me.populateForm();
        },
        instanceLoader: function () {
            var me = this;
            return function (instance) {
                me.instanceLoaded(instance);
            };
        },
        populateForm: function () {
            var me = this;
            if (!this._instance || !this._entity) {
                return;
            }
            for (var propertyName in this._instance.values) {
                var property = this._entity.properties[propertyName];
                var value = this._instance.values[propertyName];
                var widget = this._widgets[propertyName];
                if (widget) {
                    if (!me.isNewInstance() || property.editable) {
                        if (property.typeRef && property.typeRef.typeName === 'Date') {
                            try {
                                value = kirra_qooxdoo.DateFormats.getYMDFormatter().format(kirra_qooxdoo.DateFormats.getISOFormatter().parse(value));
                            } catch (e) {
                                console.log(e);
                            }
                        }
                        widget.setValue(value);
                        widget.setVisible && widget.setVisible(true);
                    } else {
                        widget.setVisible && widget.setVisible(false);
                    }
                }
            }
            for (var relationshipName in this._entity.relationships) {
                var relationship = this._entity.relationships[relationshipName];
                var widget = this._widgets[relationshipName];
                if (widget) {
                    var links = this._instance.links[relationshipName];
                    if (!me.isNewInstance() || relationship.editable) {
                        widget.setValue(links && links.length && links[0].shorthand);
                        widget.setVisible && widget.setVisible(true);
                    } else {
                        widget.setVisible && widget.setVisible(false);
                    }
                }
            }

            me.buildActions();
        },

        buildActions: function () {
            var me = this;
            this.addToolbar();
            if (me.isNewInstance()) {
                return;
            }
            var actionMenuButton = this.actionMenuButton = new qx.ui.mobile.form.Button("...");
            var actionMenu = this.actionMenu = new qx.ui.mobile.dialog.Menu(new qx.data.Array([]), actionMenuButton);

            var menuItems = [];
            this._actions = [];
            var firstLevelItems = [];

            for (var actionName in this._entity.operations) {
                var operation = this._entity.operations[actionName];
                if (operation.instanceOperation && operation.kind === "Action" && this._instance.disabledActions[actionName] === undefined)
                    this._actions.push(operation);
            }

            var overflowStartsAt = this._actions.length > 2 ? 1 : Infinity;
            for (var i in this._actions) {
                var action = this._actions[i];
                if (i >= overflowStartsAt) {
                    menuItems.push(action.label);
                } else {
                    firstLevelItems.push(action.label);
                }
            }

            for (var i in firstLevelItems) {
                var actionButton = new qx.ui.mobile.form.Button(firstLevelItems[i]);
                actionButton.addListener("tap", function (e) {
                    var selected = me.getOperationByLabel(e.getTarget().getLabel());
                    me.invokeAction(selected);
                });
                this.toolbar.add(actionButton);
            }

            this.actionMenu.hide();
            this.actionMenu.setItems(new qx.data.Array(menuItems));
            if (menuItems.length > 0) {
                actionMenuButton.addListener("tap", function (e) {
                    actionMenu.show();
                }, this);
                actionMenu.addListener("tap", function (e) {
                    var selected = me._actions[actionMenu.getSelectedIndex() + firstLevelItems.length];
                    me.invokeAction(selected);
                }, this);
                this.toolbar.add(this.actionMenuButton);
            }
        },
        
        invokeAction : function (action) {
            var me = this;
            if (action.parameters.length == 0) {
                me.repository.sendAction(me._entity, me._objectId, action, {},  me.instanceLoader());
            } else {
                qx.core.Init.getApplication().getRouting().executeGet("/entities/" + me._entityName + "/instances/" + me._objectId + "/actions/" + action.name); 
            }
        },

        getOperationByLabel: function (operationLabel) {
            for (var i in this._entity.operations) {
                var op = this._entity.operations[i];
                if (op.label === operationLabel) {
                    return op;
                }
            }
        },

        buildForm: function () {

            var me = this;

            if (!this.getContent() || !this._entity) {
                return;
            }

            this.getContent().removeAll();
            if (me._relationshipMenuButton) {
                me._relationshipMenuButton.destroy();
            }

            var form = this.form = new qx.ui.mobile.form.Form();
            for (var i in this._entity.properties) {
                if (!me.isNewInstance() || this._entity.properties[i].editable) { 
                    this.buildWidgetFor(form, this._entity.properties[i]);
                }
            }
            var multiRelationships = this._multiRelationships = [];
            for (var i in this._entity.relationships) {
                var relationship = this._entity.relationships[i];
                if (relationship.navigable) {
                    if (relationship.multiple) {
                        multiRelationships.push(relationship);
                    } else {
                        this.buildWidgetFor(form, relationship);
                    }
                }
            }
            var instanceFormRenderer = new qx.ui.mobile.form.renderer.Single(form);
            if (this._showRelationships) {
                this.buildMultiRelationshipViews(instanceFormRenderer);
            }
            this.getContent().add(instanceFormRenderer);
            this.show();
        },

        buildMultiRelationshipViews : function (instanceFormRenderer) {
            var me = this;
            
            var multiRelationships = this._multiRelationships;
            if (multiRelationships.length === 0) {
                return;
            }
        
            var relationshipMenuButton = me._relationshipMenuButton = new qx.ui.mobile.navigationbar.Button("Related");
            var relationshipMenuItems = [];
            for (var i in multiRelationships) {
                var relationship = multiRelationships[i];
                relationshipMenuItems.push(relationship.label);
            }
            var relationshipMenu = new qx.ui.mobile.dialog.Menu(new qx.data.Array(relationshipMenuItems), relationshipMenuButton);
            relationshipMenuButton.addListener("tap", function() {
                relationshipMenu.show();
            }, this);
            relationshipMenu.addListener("tap", function() {
                var selected = me._multiRelationships[relationshipMenu.getSelectedIndex()];
                qx.core.Init.getApplication().getRouting().executeGet("/entities/" + me._entityName + "/instances/" + me._objectId + "/relationships/" + selected.name);
            }, this);
            
            this.getRightContainer().add(relationshipMenuButton);
        },
        
        isNewInstance : function () {
            return this._objectId === '_template';
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
            var saveButton = new qx.ui.mobile.form.Button("Save");
            saveButton.addListener("tap", function () {
                me.saveInstance();
            });
            this.toolbar.add(saveButton);

            if (!me.isNewInstance()) {
                var deleteButton = new qx.ui.mobile.form.Button("Delete");
                var deleteConfirmationPopup = me.buildConfirmation(deleteButton, function () {
                    me.repository.deleteInstance(me._instance, function () {
                        me._back();    
                    });
                });
                deleteButton.addListener("tap", function () {
                    deleteConfirmationPopup.show();
                });
    
                this.toolbar.add(deleteButton);
            }
        },

        buildConfirmation : function(anchorWidget, toDo) {
          var popupWidget = new qx.ui.mobile.container.Composite(new qx.ui.mobile.layout.VBox());
          popupWidget.add(new qx.ui.mobile.basic.Label("Are you sure?"));

          var buttonsWidget = new qx.ui.mobile.container.Composite(new qx.ui.mobile.layout.HBox());
          var okButton = new qx.ui.mobile.form.Button("Yes");
          var cancelButton = new qx.ui.mobile.form.Button("No");

          buttonsWidget.add(okButton, { flex: 1 });
          buttonsWidget.add(cancelButton, { flex: 1 });
          popupWidget.add(buttonsWidget);

          var anchorPopup;
          okButton.addListener("tap", function() {
              anchorPopup.hide();
              toDo();
          }, this);
          cancelButton.addListener("tap", function() {
              anchorPopup.hide();
          }, this);
          return anchorPopup = new qx.ui.mobile.dialog.Popup(popupWidget, anchorWidget);
        },

        saveInstance: function () {
            var me = this;
            for (var i in this._entity.properties) {
                if (me._entity.properties[i].editable) {
                    me._instance.values[i] = me._widgets[i].getValue();
                }    
            }
            for (var i in this._entity.relationships) {
                if (me._entity.relationships[i].editable && !me._entity.relationships[i].multiple && me._relationshipDomains[i]) {
                    var referenceIndex = me._widgets[i]._getAttribute("index");
                    var related = me._relationshipDomains[i][referenceIndex];
                    me._instance.links[i] = [ related ];
                }    
            }
            me.repository.saveInstance(me._entity, me._instance, function (created) { 
                qx.core.Init.getApplication().getRouting().executeGet("/entities/" + me._entityName + "/instances/" + created.objectId)
            });
        },

        buildWidgetFor: function (form, property) {
            var widget = kirra_qooxdoo.Widgets.createWidget(property, this.instanceEnumerator(property));
            this._widgets[property.name] = widget;
            if (widget.setRequired)
                widget.setRequired(property.required === true);
            if (widget.setReadOnly)
                widget.setReadOnly(this.isNewInstance() ? (property.initializable === false) : (property.editable === false));
            if (widget.setEnabled)
                widget.setEnabled(this.isNewInstance() ? (property.initializable === true) : (property.editable === true));
            form.add(widget, property.label);
        },
        
        instanceEnumerator: function (property) {
            var me = this;
            var kind = property.typeRef && property.typeRef.kind;
            if (kind !== 'Entity') {
                return me.noOpValueEnumerator;
            }
            return function(property, callback) {
                me.repository.listRelationshipDomain(me._entity, me._objectId, property, function (objects) {
                    me._relationshipDomains[property.name] = objects.contents;
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
