qx.Class.define("kirra_qooxdoo.RelatedInstanceNavigator", {
    extend: kirra_qooxdoo.AbstractInstanceNavigator,

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
        _parentEntityName: null,
        _parentEntity : null,
        _childEntityName: null,
        _childEntity : null,
        _parentObjectId: null,
        _toolbar : null,
        _instanceList : null,
        // overridden
        _initialize: function () {
            this.base(arguments);
            var me = this;
            var list = me._buildInstanceList();
            list.addListener("changeSelection", function (evt) {
                var instanceSelected = me._instanceList.getModel().getItem(evt.getData());
                qx.core.Init.getApplication().getRouting().executeGet("/entities/" + me._parentEntityName + "/instances/" + me._parentObjectId + "/relationships/" + me._relationshipName + "/" + instanceSelected.objectId);
            }, this);
        },
        _start: function () {
            this.base(arguments);
        },
        _back: function () {
            var me = this;
            qx.core.Init.getApplication().getRouting().executeGet("/entities/" + me._parentEntityName + "/instances/" + me._parentObjectId, {
                reverse: true
            });
        },
        showFor: function (parentEntityName, relationshipName, parentObjectId) {
            var me = this;
            this._parentEntity = null;
            this._childEntityName = null;
            this._childEntity = null;
            this._parentEntityName = parentEntityName;
            this._relationshipName = relationshipName;
            this._parentObjectId = parentObjectId;
            this.show();
            this.repository.loadEntity(parentEntityName, function (parentEntity) { 
                me._parentEntity = parentEntity;
                me._relationship = parentEntity.relationships[me._relationshipName];
                me._childEntityName = me._relationship.typeRef.fullName;
                me.setTitle(me._relationship.label);
                me.repository.loadEntity(me._childEntityName, function (childEntity) {
                    me._childEntity = childEntity;
                    me.buildDetailProperties(me._childEntity);
                    me.reloadInstances();
                });
            });
        },
        _loadInstances : function (callback) {
            var me = this;
            this.repository.listRelatedInstances(this._parentEntity, this._parentObjectId, this._relationship, callback);
        },
        _getInstanceListEntity : function () {
            return this._childEntity;
        }
    }
});
