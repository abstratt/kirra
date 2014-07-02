/* ************************************************************************

   Copyright:

   License:

   Authors:

************************************************************************ */

/**
 * This is the main application class of your custom application "kirra_qooxdoo"
 *
 * @require(kirra_qooxdoo.Repository)
 * @asset(kirra_qooxdoo/*)
 * @asset(qx/mobile/js/iscroll.js) 
 */
qx.Class.define("kirra_qooxdoo.Application",
{
  extend : qx.application.Mobile,
  
  
  properties : {
    routing : {
      init: null
    }
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */
  
  statics : {
    getInstance : function () { return this.$$instance }
  },

  members :
  {
  
    repository : null,
    
    inAppHistory : [],
    
    goBack : function (fallback) {
        // discard current
        this.inAppHistory.pop();
        if (this.inAppHistory.length <= 1) {
            console.log("No inAppHistory");
            console.log("Falling back to " + fallback);
            qx.bom.History.getInstance().setState(fallback);
            return;
        }
        var goTo = this.inAppHistory.pop();
        console.log("Found inAppHistory");
        console.log(this.inAppHistory);
        qx.bom.History.getInstance().setState(goTo);
        console.log("current: " + qx.bom.History.getInstance().getState());
    }, 
    
    /**
     * This method contains the initial application code and gets called 
     * during startup of the application
     * 
     * @lint ignoreDeprecated(alert)
     */
    main : function()
    {
      // Call super class
      this.base(arguments);

      // Enable logging in debug variant
      if (qx.core.Environment.get("qx.debug"))
      {
        // support native logging capabilities, e.g. Firebug for Firefox
        qx.log.appender.Native;
        // support additional cross-browser console. Press F7 to toggle visibility
        qx.log.appender.Console;
      }

      /*
      -------------------------------------------------------------------------
        Below is your actual application code...
      -------------------------------------------------------------------------
      */
      var me = this;
      this.$$instance = this;
      
      var uriMatches = window.location.search.match("[?&]?app-uri\=(.*)\&?");
      var pathMatches = window.location.search.match("[?&]?app-path\=(.*)\&?");
      if (!uriMatches && !pathMatches) {
          var abortMessage = "You must specify an application URI or path (same server) using the app-uri or app-path query parameters, like '...?app-uri=http://myserver.com/myapp/rest/' or '...?app-path=/myapp/rest/'.";
          alert(abortMessage);
          throw Error(abortMessage);
      }
      var apiBaseUri = uriMatches ? uriMatches[1] : (window.location.origin + pathMatches[1]);
      
      this.repository = new kirra_qooxdoo.Repository(apiBaseUri);
      this.mainPage = new kirra_qooxdoo.EntityNavigator(this.repository);
      this.instanceNavigator = new kirra_qooxdoo.InstanceNavigator(this.repository);
      this.instanceForm = new kirra_qooxdoo.InstanceForm(this.repository);
      this.relatedInstanceNavigator = new kirra_qooxdoo.RelatedInstanceNavigator(this.repository);
      this.actionForm = new kirra_qooxdoo.ActionForm(this.repository);
      
      this.repository.loadApplication(function (application) {
          document.title = application.applicationName;
      });
      
      var manager = new qx.ui.mobile.page.Manager(false);
      manager.addMaster(this.mainPage);
      manager.addDetail([this.instanceNavigator, this.instanceForm, this.actionForm, this.relatedInstanceNavigator]);
      var nm = new qx.application.Routing();

      nm.onGet("/", function(data) {
        this.mainPage.show();
      },this);
      
      nm.onGet("/entities/{entity}/instances/", function(data) {
        this.instanceNavigator.showFor(data.params.entity);
      }, this);
      
      nm.onGet("/entities/{entity}/instances/{objectId}", function(data) {
        this.instanceForm.showFor(data.params.entity, data.params.objectId);
      }, this);
      
      nm.onGet("/entities/{entity}/instances/{objectId}/relationships/{relationshipName}", function(data) {
        this.relatedInstanceNavigator.showFor(data.params.entity, data.params.relationshipName, data.params.objectId);
      }, this);
      
      nm.onGet("/entities/{entity}/instances/{objectId}/actions/{actionName}", function(data) {
        this.actionForm.showFor(data.params.entity, data.params.actionName, data.params.objectId);
      }, this);
      
      nm.onGet("/entities/{entity}/instances/{objectId}/relationships/{relationshipName}/{relatedObjectId}", function(data) {
          this.instanceForm.showForRelated(data.params.entity, data.params.objectId, data.params.relationshipName, data.params.relatedObjectId);
      }, this);
      
/*      
      nm.onGet("/entities/{entity}/instances/{objectId}/relationships/{relationshipName}/{relatedObjectId}/actions/{actionName}", function(data) {
        this.actionForm.showForRelated(data.params.entity, data.params.actionName, data.params.objectId);
      }, this);
*/     
      nm.onGet("/entities/{entity}/actions/{actionName}", function(data) {
        this.actionForm.showFor(data.params.entity, data.params.actionName, data.params.objectId);
      }, this);
      
      nm.onAny(".*", function (data) {
          console.log("data.customData:");
          console.log(data.customData);
          if (me.inAppHistory.indexOf(data.path) >= 0) {
              me.inAppHistory = me.inAppHistory.slice(0, me.inAppHistory.indexOf(data.path) + 1);
          } else {
              if (!data.customData || (!data.customData.fromHistory && !data.customData.reverse)) {  
                  me.inAppHistory.push(data.path);
              }
          }
          console.log(me.inAppHistory);
      });
      
      this.setRouting(nm);
      
      nm.init();
    }
  }
});
