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

  members :
  {
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

      var uriMatches = window.location.search.match("[?&]?app-uri\=(.*)\&?");
      var pathMatches = window.location.search.match("[?&]?app-path\=(.*)\&?");
      if (!uriMatches && !pathMatches) {
          throw Error("You must specify an application URI or path (same server) using the app-uri or app-path query parameters, like '...?app-uri=http://myserver.com/myapp/rest/' or '...?app-path=/myapp/rest/'.");
      }
      var apiBaseUri = uriMatches ? uriMatches[1] : (window.location.origin + pathMatches[1]);
      
      var isTablet = (qx.core.Environment.get("device.type") == "tablet");
      var isDesktop = (qx.core.Environment.get("device.type") == "desktop");

      this.repository = new kirra_qooxdoo.Repository(apiBaseUri);
      this.mainPage = new kirra_qooxdoo.EntityNavigator(this.repository);
      this.instanceNavigator = new kirra_qooxdoo.InstanceNavigator(this.repository);
      this.instanceForm = new kirra_qooxdoo.InstanceForm(this.repository);
      
      this.repository.loadApplication(function (application) {
          document.title = application.applicationName;
      });
      
      var manager = new qx.ui.mobile.page.Manager(isTablet);
      manager.addMaster(this.mainPage);
      manager.addDetail(this.instanceNavigator);
      manager.addDetail(this.instanceForm);
      var nm = new qx.application.Routing();

      nm.onGet("/", function(data) {
        this.mainPage.show();
      },this);
      
      nm.onGet("/entity/{entity}/instances/", function(data) {
        this.instanceNavigator.showFor(data.params.entity);
      }, this);
      
      nm.onGet("/entity/{entity}/instances/{objectId}", function(data) {
        this.instanceForm.showFor(data.params.entity, data.params.objectId);
      }, this);
      
      this.setRouting(nm);
      
      nm.init();
    }
  }
});
