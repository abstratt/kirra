qx.Class.define("kirra_qooxdoo.InstanceNavigator",
{
  extend : qx.ui.mobile.page.NavigationPage,

  construct : function()
  {
    this.base(arguments);
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
      var list = this.list = new qx.ui.mobile.list.List({
        configureItem : function(item, data, row)
        {
          item.setTitle(data.getName());
          item.setSubtitle(data.getDescription());
          item.setShowArrow(true);
          item.data = data;
        }
      });
      this.entityStore = new qx.data.store.Json(null);
      this.entityStore.bind("model", list, "model");
      this.getContent().add(list);
      list.addListener("changeSelection", function(evt) {
        console.log(evt.getData());
      }, this);
    },
    _start : function() {
        this.base(arguments);
        this.entityStore.setUrl(window.location.origin + "/services/api/demo-cloudfier-examples-expenses/entities/");
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
