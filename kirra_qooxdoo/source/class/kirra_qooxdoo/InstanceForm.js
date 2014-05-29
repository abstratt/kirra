qx.Class.define("kirra_qooxdoo.InstanceForm",
{
  extend : qx.ui.mobile.page.NavigationPage,

  construct : function(repository)
  {
    this.base(arguments);
    this.repository = repository;
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
    _objectId : null,    
    _instance : null,  
    _entity : null,      
    // overridden
    _initialize : function()
    {
      this.base(arguments);
    },
    _start : function() {
        this.base(arguments);
    },
    _back : function()
    {
	     qx.core.Init.getApplication().getRouting().executeGet("/entity/" + this._entityName + "/instances/", {reverse:true});
    },
    showFor : function (entityName, id) {
        var me = this;
        this._entityName = entityName;
        this._objectId = id;
        this.repository.loadEntity(entityName, function (entity) {
           me._entity = entity;
           me.build();
        });
        this.show();
    },
    build : function () {
    
      if (!this.getContent() || !this._entity) {
          return;
      }
    
      this.getContent().removeAll();
      if (this.toolbar)
          this.toolbar.destroy();
    
      var toolbar = this.toolbar = new qx.ui.mobile.toolbar.ToolBar();
      this.add(toolbar);

      var actionMenuButton = this.actionMenuButton = new qx.ui.mobile.form.Button("Actions");
      var actionMenu = this.actionMenu = new qx.ui.mobile.dialog.Menu(new qx.data.Array([]), actionMenuButton);
      actionMenu.setTitle("Actions");
      actionMenuButton.addListener("tap", function(e) {
          actionMenu.show();
      }, this);
      actionMenu.addListener("tap", function(e) {
         alert("Actions not implemented yet");
      }, this);
      this.actionMenu = actionMenu; 
      toolbar.add(actionMenuButton);

      var form = this.form = new qx.ui.mobile.form.Form();
      
      for (var i in this._entity.properties) {
          this.buildWidgetFor(form, this._entity.properties[i]); 
      }
      var instanceFormRenderer = new qx.ui.mobile.form.renderer.Single(form);
      
      this.getContent().add(instanceFormRenderer);
      
      var button = new qx.ui.mobile.form.Button("Save");
      button.addListener("tap", function () {
          alert("Saving not implemented yet");
      });
      toolbar.add(button);
    
      this.show(); 
    },
    
    buildWidgetFor : function (form, property) {
	    var widget = new qx.ui.mobile.form.TextField()
	    form.add(widget, property.name);
    }
  }
});
