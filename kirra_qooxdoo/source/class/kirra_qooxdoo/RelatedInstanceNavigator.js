qx.Class.define("kirra_qooxdoo.RelatedInstanceNavigator", {
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
                    
                      var details = [], value, detail;
                      for (name in me._detailProperties) {
                          value = data.values[name];
                          if (me._entity.properties[name].typeRef && me._entity.properties[name].typeRef.typeName === 'Date') {
                              try {
                                  value = kirra_qooxdoo.DateFormats.getYMDFormatter().format(kirra_qooxdoo.DateFormats.getISOFormatter().parse(value));
                              } catch (e) {}
                          }
                          
                          if (value && value != null) {
                              detail = me._detailProperties[name].label;
                              if (value !== true)
                                  detail += ": " + value
                              details.push(detail);
                          }
                      }
                      item.setSubtitle(details.join(", "));
                      item.setShowArrow(true);
                }
            });
            this.getContent().add(list);
            list.addListener("changeSelection", function (evt) {
                var instanceSelected = me._instanceList.getModel().getItem(evt.getData());
                qx.core.Init.getApplication().getRouting().executeGet("/entities/" + me._entityName + "/instances/" + me._objectId + "/relationships/" + me._relationshipName + "/" + instanceSelected.objectId);
            }, this);
        },
        _start: function () {
            this.base(arguments);
        },
        _back: function () {
            var me = this;
            qx.core.Init.getApplication().getRouting().executeGet("/entities/" + me._entityName + "/instances/" + me._objectId, {
                reverse: true
            });
        },
        showFor: function (entityName, relationshipName, objectId) {
            var me = this;
            console.log("showFor: "+ entityName);
            this._entityName = entityName;
            this._relationshipName = relationshipName;
            this._objectId = objectId;
            me.show();
            this.repository.loadEntity(this._entityName, function (entity) { 
                me._entity = entity;
                me._relationship = entity.relationships[me._relationshipName];
                me.buildDetailProperties();
                me.reloadInstances();
            });
        },
        reloadInstances : function () {
            var me = this;
            this.repository.listRelatedInstances(this._entity, this._objectId, this._relationship, function (instances) {
                me.getContent().removeAll();
                if (instances.contents.length === 0) {
                    var nothingToSeeHere = new qx.ui.mobile.form.Label("No records found");
                    me.getContent().add(nothingToSeeHere);
                } else {
                    me._instanceList.setModel(new qx.data.Array(instances.contents));
                    me.getContent().add(me._instanceList);
                }
            });
        },
        buildDetailProperties : function () {
            var me = this;
            var detailProperties = {};
            // we ignore the first property as it shows as title
            var skippedMnemonic = false;
            for (var p in me._entity.properties) {
                if (me._entity.properties[p].userVisible) {
                    if (skippedMnemonic) {
                        detailProperties[p] = { label: me._entity.properties[p].label };
                    }
                    skippedMnemonic = true;
                }
            }
            me._detailProperties = detailProperties;
        }
    }
});
