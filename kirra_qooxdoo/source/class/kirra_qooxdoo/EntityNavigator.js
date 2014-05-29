qx.Class.define("kirra_qooxdoo.EntityNavigator",
{
  extend : qx.ui.mobile.page.NavigationPage,

  construct : function(entityManagement)
  {
    this.base(arguments);
    this.entityManagement = entityManagement;
    this.setTitle("Overview");
    this.setShowBackButton(true);
    this.setBackButtonText("Log out");
  },


  events :
  {
    /** The page to show */
    "show" : "qx.event.type.Data"
  },


  members :
  {
    // overridden
    _initialize : function()
    {
      this.base(arguments);
      var me = this;
      var list = this.entityList = new qx.ui.mobile.list.List({
        configureItem : function(item, data, row)
        {
          item.setTitle(data.name);
          item.setSubtitle(data.description);
          item.setShowArrow(true);
          item.data = data;
        }
      });
      this.getContent().add(list);
      list.addListener("changeSelection", function(evt) {
        console.log(evt.getData());
      }, this);
      this.entityManagement.addEntityListListener(function(entities) {
          me.entityList.setModel(new qx.data.Array(entities));
      });
    },
    _start : function() {
        this.base(arguments);
    },
    _back : function()
    {
        if (this.getBackButtonText() == "Log out") {
            this.setBackButtonText("Log in");
        } else if (this.getBackButtonText() == "Log in") {
            this.setBackButtonText("Log out");
        }
    }
  }
});
