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
    _widgets : {},
    _actions : [],
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
        me._widgets = {},
        me._entityName = entityName;
        me._objectId = id;
        me._entity = null;
        me._instance = null;
        me.repository.loadEntity(entityName, function (entity) {
           me._entity = entity;
           me.buildForm();
           me.repository.loadInstance(entity, me._objectId, function (instance) {
               me._instance = instance;
               me.populateForm();    
           });
        });
        this.show();
    },
    populateForm : function () {
      if (!this._instance || !this._entity) {
          return;
      }
      for (var propertyName in this._instance.values) {
          var value = this._instance.values[propertyName];
          if (this._widgets[propertyName]) {
              this._widgets[propertyName].setValue(value);
          }
      }
      var menuItems = [];
      this._actions = [];
      for (var actionName in this._entity.operations) {
          var operation = this._entity.operations[actionName];
          if (operation.instanceOperation && operation.kind === "Action" && this._instance.disabledActions[actionName] === undefined) {
              menuItems.push(operation.label);
              this._actions.push(operation);
          }
      }
      this.actionMenu.hide();
      this.actionMenu.setItems(new qx.data.Array(menuItems));
      this.actionMenuButton.setEnabled(menuItems.length > 0);
    },
    buildForm : function () {
    
      var me = this;
    
      if (!this.getContent() || !this._entity) {
          return;
      }
    
      this.getContent().removeAll();
      if (this.toolbar)
          this.toolbar.destroy();
    
      var toolbar = this.toolbar = new qx.ui.mobile.toolbar.ToolBar();
      this.add(toolbar);

      
      console.log(this._entity.actions);
      var actionMenuButton = this.actionMenuButton = new qx.ui.mobile.form.Button("Actions");
      var actionMenu = this.actionMenu = new qx.ui.mobile.dialog.Menu(new qx.data.Array([]), actionMenuButton);
      this.actionMenuButton.setEnabled(false);
      actionMenu.setTitle("Actions");
      actionMenuButton.addListener("tap", function(e) {
          actionMenu.show();
      }, this);
      actionMenu.addListener("tap", function(e) {
          me.repository.sendAction(me._entity, me._objectId, me._actions[actionMenu.getSelectedIndex()], function (instance) {
               me._instance = instance;
               me.populateForm();
          });
          console.log(e);
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
          me.saveInstance();
      });   
      toolbar.add(button);
    
      this.show(); 
    },
    
    saveInstance : function () {
        var me = this;
        var updates = { values : {}, uri : me._instance.uri };
        for (var i in this._entity.properties) {
            updates.values[i] = me._widgets[i].getValue(); 
        }
        me.repository.saveInstance(me._entity, updates, function (updated) {
            me._instance = updated;
            me.populateForm();
        });
    },
    
    buildWidgetFor : function (form, property) {
        var widget = this.createWidget(property);
        this._widgets[property.name] = widget;
        if (widget.setRequired)
            widget.setRequired(property.required === true);
        if (widget.setReadOnly) 
            widget.setReadOnly(property.editable === false);
        if (widget.setEnabled) 
            widget.setEnabled(property.editable !== false);
        form.add(widget, property.label);
    },
    
    createWidget : function (property) {
        var kind = property.typeRef && property.typeRef.kind
        var factoryMethodName = 'create' + kind + 'Widget';
        var factory = this[factoryMethodName];
        if (typeof(factory) !== 'function') {
            console.log("No factory found for: " +  + property.name + " : " + kind);
            return this.createStringWidget();
        }
        return factory.call(this, property);
    },
    
    createPrimitiveWidget : function (property) {
        var typeName = property.typeRef && property.typeRef.typeName
        var factoryMethodName = 'create' + typeName + 'Widget';
        var factory = this[factoryMethodName];
        if (typeof(factory) !== 'function') {
            console.log("No factory found for: " + property.name + " : " + typeName);
            return this.createStringWidget();
        }
        return factory.call(this, property);
    },
    
    createStringWidget : function(attribute) { return new qx.ui.mobile.form.TextField(); },
    
    createIntegerWidget : function(attribute) { return new qx.ui.mobile.form.TextField(); },
    
    createDoubleWidget : function(attribute) { return new qx.ui.mobile.form.TextField(); },
    
    createDateWidget : function(attribute) { return new qx.ui.mobile.form.TextField(); },    
    
    createMemoWidget : function(attribute) { return new qx.ui.mobile.form.TextArea(); },

    createBooleanWidget : function(attribute) { return new qx.ui.mobile.form.ToggleButton(); },
/*    
    createDateWidget : function(attribute) { 
        var days = [];
        for (var day = 1;day <= 31;day++) {
            days.push("" + day);
        } 
        var daySlot = new qx.data.Array(days);
        var monthSlot = new qx.data.Array(["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]);
        var years = [];
        for (var year = 1900 + new Date().getYear();year > 1900;year--) {
            years.push("" + year);
        }
        var yearSlot = new qx.data.Array(years);
        var picker = new qx.ui.mobile.dialog.Picker();
        picker.addSlot(daySlot);
        picker.addSlot(monthSlot);
        picker.addSlot(yearSlot);
        return picker;
    },
*/
    createStateMachineWidget : function(attribute) {
        return new qx.ui.mobile.form.TextField();
    },

    createEnumerationWidget : function(property) {
        var widget = new qx.ui.mobile.form.SelectBox();
        var values = [];
        if (property.enumerationLiterals) {
            if (!property.required) {
                values.push('- None -');
            }
            for (var i in property.enumerationLiterals) {
                values.push(property.enumerationLiterals[i]);
            }
        }
        widget.setModel(new qx.data.Array(values));
        return widget;
    }
  }
});
