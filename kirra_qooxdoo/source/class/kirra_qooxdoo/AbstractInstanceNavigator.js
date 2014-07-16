qx.Class.define("kirra_qooxdoo.AbstractInstanceNavigator", {
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
        _getInstanceListEntity : function () { throw Error("implement this") },
        _loadInstances : function (callback) { throw Error("implement this") },
        _buildInstanceList: function () {
            var me = this;
            this._instanceList = new qx.ui.mobile.list.List({
                configureItem: function (item, data, row) {
                    item.setTitle(data.shorthand);
                    item.setSubtitle("");
                    item.setShowArrow(true);
                    item.data = data;
                    
                    var details = [], value, detail;
                    for (name in me._detailProperties) {
                        value = data.values[name];
                        var entity = me._getInstanceListEntity();
                        if (entity.properties[name] && entity.properties[name].typeRef && entity.properties[name].typeRef.typeName === 'Date') {
                              try {
                                  value = kirra_qooxdoo.DateFormats.getYMDFormatter().format(kirra_qooxdoo.DateFormats.getISOFormatter().parse(value));
                              } catch (e) {}
                        } else if (data.links[name] && data.links[name].length > 0) {
                            value = data.links[name][0].shorthand;
                        }
                        if (value) {
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
            this.getContent().add(me._instanceList);
            return me._instanceList;
        },
        _start: function () {
            this.base(arguments);
        },
        _back: function () {
            throw Error("Implement this");
        },
        reloadInstances : function () {
            var me = this;
            this._loadInstances(function (instances) {
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
        buildDetailProperties : function (entity) {
            var me = this;
            var detailProperties = {};
            // we ignore the first property as it shows as title
            var skippedMnemonic = false;
            for (var p in entity.properties) {
                if (entity.properties[p].userVisible) {
                    if (skippedMnemonic) {
                        detailProperties[p] = { label: entity.properties[p].label };
                    }
                    skippedMnemonic = true;
                }
            }
            for (var r in entity.relationships) {
                if (!entity.relationships[r].multiple) {
                    detailProperties[r] = { label: entity.relationships[r].label };
                }
            }
            me._detailProperties = detailProperties;
        }
    }
});
