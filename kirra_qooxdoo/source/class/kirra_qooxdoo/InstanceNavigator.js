qx.Class.define("kirra_qooxdoo.InstanceNavigator",
{
  extend : qx.ui.mobile.page.NavigationPage,

  construct : function(repository)
  {
    this.base(arguments);
    this.repository = repository;
    this.setTitle("Instances");
    this.setShowBackButton(true);
    this.setBackButtonText("Back");
  },


  events :
  {
    /** The page to show */
    "show" : "qx.event.type.Data"
  },


  members :
  {
    _entityName : null,
    // overridden
    _initialize : function()
    {
      this.base(arguments);
      var me = this;
      var list = this.instanceList = new qx.ui.mobile.list.List({
        configureItem : function(item, data, row)
        {
          item.setTitle(data.shorthand);
          item.setSubtitle("");
          item.setShowArrow(true);
          item.data = data;
        }
      });
      this.getContent().add(list);
      list.addListener("changeSelection", function(evt) {
        var instanceSelected = me.instanceList.getModel().getItem(evt.getData());
        console.log(instanceSelected);
        qx.core.Init.getApplication().getRouting().executeGet("/entity/" + me._entityName + "/instances/" + instanceSelected.objectId);
      }, this);
    },
    _start : function() {
        this.base(arguments);
    },
    _back : function()
    {
	     qx.core.Init.getApplication().getRouting().executeGet("/", {reverse:true});
    },
    showFor : function (entityName) {
        var me = this;
        this._entityName = entityName;
        this.show();
        this.repository.loadInstances(this._entityName, function(instances) {
	        me.instanceList.setModel(new qx.data.Array(instances.contents));
	        me.setTitle(entityName);
	    });
    }
  }
});
