/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2008 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)

************************************************************************ */

/**
 * Contains some common methods available to all log appenders.
 */
qx.Bootstrap.define("qx.log.appender.Util",
{
  statics :
  {
    /**
     * Converts a single log entry to HTML
     *
     * @signature function(entry)
     * @param entry {Map} The entry to process
     */
    toHtml : function(entry)
    {
      var output = [];
      var item, msg, sub, list;

      output.push("<span class='offset'>", this.formatOffset(entry.offset, 6), "</span> ");

      if (entry.object)
      {
        var obj = entry.win.qx.core.ObjectRegistry.fromHashCode(entry.object);
        if (obj) {
          output.push("<span class='object' title='Object instance with hash code: " + obj.$$hash + "'>", obj.classname, "[" , obj.$$hash, "]</span>: ");
        }
      }
      else if (entry.clazz)
      {
        output.push("<span class='object'>" + entry.clazz.classname, "</span>: ");
      }

      var items = entry.items;
      for (var i=0, il=items.length; i<il; i++)
      {
        item = items[i];
        msg = item.text;

        if (msg instanceof Array)
        {
          var list = [];

          for (var j=0, jl=msg.length; j<jl; j++)
          {
            sub = msg[j];
            if (typeof sub === "string") {
              list.push("<span>" + this.escapeHTML(sub) + "</span>");
            } else if (sub.key) {
              list.push("<span class='type-key'>" + sub.key + "</span>:<span class='type-" + sub.type + "'>" + this.escapeHTML(sub.text) + "</span>");
            } else {
              list.push("<span class='type-" + sub.type + "'>" + this.escapeHTML(sub.text) + "</span>");
            }
          }

          output.push("<span class='type-" + item.type + "'>");

          if (item.type === "map") {
            output.push("{", list.join(", "), "}");
          } else {
            output.push("[", list.join(", "), "]");
          }

          output.push("</span>");
        }
        else
        {
          output.push("<span class='type-" + item.type + "'>" + this.escapeHTML(msg) + "</span> ");
        }
      }

      var wrapper = document.createElement("DIV");
      wrapper.innerHTML = output.join("");
      wrapper.className = "level-" + entry.level;

      return wrapper;
    },


    /**
     * Formats a numeric time offset to 6 characters.
     *
     * @param offset {Integer} Current offset value
     * @param length {Integer?6} Refine the length
     * @return {String} Padded string
     */
    formatOffset : function(offset, length)
    {
      var str = offset.toString();
      var diff = (length||6) - str.length;
      var pad = "";

      for (var i=0; i<diff; i++) {
        pad += "0";
      }

      return pad+str;
    },


    /**
     * Escapes the HTML in the given value
     *
     * @param value {String} value to escape
     * @return {String} escaped value
     */
    escapeHTML : function(value) {
      return String(value).replace(/[<>&"']/g, this.__escapeHTMLReplace);
    },


    /**
     * Internal replacement helper for HTML escape.
     *
     * @param ch {String} Single item to replace.
     * @return {String} Replaced item
     */
    __escapeHTMLReplace : function(ch)
    {
      var map =
      {
        "<" : "&lt;",
        ">" : "&gt;",
        "&" : "&amp;",
        "'" : "&#39;",
        '"' : "&quot;"
      };

      return map[ch] || "?";
    },


    /**
     * Converts a single log entry to plain text
     *
     * @param entry {Map} The entry to process
     * @return {String} the formatted log entry
     */
    toText : function(entry) {
      return this.toTextArray(entry).join(" ");
    },


    /**
     * Converts a single log entry to an array of plain text
     *
     * @param entry {Map} The entry to process
     * @return {Array} Argument list ready message array.
     */
    toTextArray : function(entry)
    {
      var output = [];

      output.push(this.formatOffset(entry.offset, 6));

      if (entry.object)
      {
        var obj = entry.win.qx.core.ObjectRegistry.fromHashCode(entry.object);
        if (obj) {
          output.push(obj.classname + "[" + obj.$$hash + "]:");
        }
      }
      else if (entry.clazz) {
        output.push(entry.clazz.classname + ":");
      }

      var items = entry.items;
      var item, msg;
      for (var i=0, il=items.length; i<il; i++)
      {
        item = items[i];
        msg = item.text;

        if (item.trace && item.trace.length > 0) {
          if (typeof(this.FORMAT_STACK) == "function") {
            qx.log.Logger.deprecatedConstantWarning(qx.log.appender.Util,
              "FORMAT_STACK",
              "Use qx.dev.StackTrace.FORMAT_STACKTRACE instead");
            msg += "\n" + this.FORMAT_STACK(item.trace);
          } else {
            msg += "\n" + item.trace;
          }
        }

        if (msg instanceof Array)
        {
          var list = [];
          for (var j=0, jl=msg.length; j<jl; j++) {
            list.push(msg[j].text);
          }

          if (item.type === "map") {
            output.push("{", list.join(", "), "}");
          } else {
            output.push("[", list.join(", "), "]");
          }
        }
        else
        {
          output.push(msg);
        }
      }

      return output;
    }
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2008 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)

************************************************************************ */

/**
 * Processes the incoming log entry and displays it by means of the native
 * logging capabilities of the client.
 *
 * Supported browsers:
 * * Firefox <4 using FireBug (if available).
 * * Firefox >=4 using the Web Console.
 * * WebKit browsers using the Web Inspector/Developer Tools.
 * * Internet Explorer 8+ using the F12 Developer Tools.
 * * Opera >=10.60 using either the Error Console or Dragonfly
 *
 * Currently unsupported browsers:
 * * Opera <10.60
 *
 * @require(qx.log.appender.Util)
 * @require(qx.bom.client.Html)
 */
qx.Bootstrap.define("qx.log.appender.Native",
{
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /**
     * Processes a single log entry
     *
     * @param entry {Map} The entry to process
     */
    process : function(entry)
    {
      if (qx.core.Environment.get("html.console")) {
        // Firefox 4's Web Console doesn't support "debug"
        var level = console[entry.level] ? entry.level : "log";
        if (console[level]) {
          var args = qx.log.appender.Util.toText(entry);
          console[level](args);
        }
      }
    }
  },




  /*
  *****************************************************************************
     DEFER
  *****************************************************************************
  */

  defer : function(statics) {
    qx.log.Logger.register(statics);
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2008 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)

************************************************************************ */

/**
 * Feature-rich console appender for the qooxdoo logging system.
 *
 * Creates a small inline element which is placed in the top-right corner
 * of the window. Prints all messages with a nice color highlighting.
 *
 * * Allows user command inputs.
 * * Command history enabled by default (Keyboard up/down arrows).
 * * Lazy creation on first open.
 * * Clearing the console using a button.
 * * Display of offset (time after loading) of each message
 * * Supports keyboard shortcuts F7 or Ctrl+D to toggle the visibility
 *
 * @require(qx.event.handler.Window)
 * @require(qx.event.handler.Keyboard)
 */
qx.Class.define("qx.log.appender.Console",
{
  statics :
  {
    /*
    ---------------------------------------------------------------------------
      INITIALIZATION AND SHUTDOWN
    ---------------------------------------------------------------------------
    */

   __main : null,

   __log : null,

   __cmd : null,

   __lastCommand : null,

    /**
     * Initializes the console, building HTML and pushing last
     * log messages to the output window.
     *
     */
    init : function()
    {
      // Build style sheet content
      var style =
      [
        '.qxconsole{z-index:10000;width:600px;height:300px;top:0px;right:0px;position:absolute;border-left:1px solid black;color:black;border-bottom:1px solid black;color:black;font-family:Consolas,Monaco,monospace;font-size:11px;line-height:1.2;}',

        '.qxconsole .control{background:#cdcdcd;border-bottom:1px solid black;padding:4px 8px;}',
        '.qxconsole .control a{text-decoration:none;color:black;}',

        '.qxconsole .messages{background:white;height:100%;width:100%;overflow:auto;}',
        '.qxconsole .messages div{padding:0px 4px;}',

        '.qxconsole .messages .user-command{color:blue}',
        '.qxconsole .messages .user-result{background:white}',
        '.qxconsole .messages .user-error{background:#FFE2D5}',
        '.qxconsole .messages .level-debug{background:white}',
        '.qxconsole .messages .level-info{background:#DEEDFA}',
        '.qxconsole .messages .level-warn{background:#FFF7D5}',
        '.qxconsole .messages .level-error{background:#FFE2D5}',
        '.qxconsole .messages .level-user{background:#E3EFE9}',
        '.qxconsole .messages .type-string{color:black;font-weight:normal;}',
        '.qxconsole .messages .type-number{color:#155791;font-weight:normal;}',
        '.qxconsole .messages .type-boolean{color:#15BC91;font-weight:normal;}',
        '.qxconsole .messages .type-array{color:#CC3E8A;font-weight:bold;}',
        '.qxconsole .messages .type-map{color:#CC3E8A;font-weight:bold;}',
        '.qxconsole .messages .type-key{color:#565656;font-style:italic}',
        '.qxconsole .messages .type-class{color:#5F3E8A;font-weight:bold}',
        '.qxconsole .messages .type-instance{color:#565656;font-weight:bold}',
        '.qxconsole .messages .type-stringify{color:#565656;font-weight:bold}',

        '.qxconsole .command{background:white;padding:2px 4px;border-top:1px solid black;}',
        '.qxconsole .command input{width:100%;border:0 none;font-family:Consolas,Monaco,monospace;font-size:11px;line-height:1.2;}',
        '.qxconsole .command input:focus{outline:none;}'
      ];

      // Include stylesheet
      qx.bom.Stylesheet.createElement(style.join(""));

      // Build markup
      var markup =
      [
        '<div class="qxconsole">',
        '<div class="control"><a href="javascript:qx.log.appender.Console.clear()">Clear</a> | <a href="javascript:qx.log.appender.Console.toggle()">Hide</a></div>',
        '<div class="messages">',
        '</div>',
        '<div class="command">',
        '<input type="text"/>',
        '</div>',
        '</div>'
      ];

      // Insert HTML to access DOM node
      var wrapper = document.createElement("DIV");
      wrapper.innerHTML = markup.join("");
      var main = wrapper.firstChild;
      document.body.appendChild(wrapper.firstChild);

      // Make important DOM nodes available
      this.__main = main;
      this.__log = main.childNodes[1];
      this.__cmd = main.childNodes[2].firstChild;

      // Correct height of messages frame
      this.__onResize();

      // Finally register to log engine
      qx.log.Logger.register(this);

      // Register to object manager
      qx.core.ObjectRegistry.register(this);
    },


    /**
     * Used by the object registry to dispose this instance e.g. remove listeners etc.
     *
     */
    dispose : function()
    {
      qx.event.Registration.removeListener(document.documentElement, "keypress", this.__onKeyPress, this);
      qx.log.Logger.unregister(this);
    },





    /*
    ---------------------------------------------------------------------------
      INSERT & CLEAR
    ---------------------------------------------------------------------------
    */

    /**
     * Clears the current console output.
     *
     */
    clear : function()
    {
      // Remove all messages
      this.__log.innerHTML = "";
    },


    /**
     * Processes a single log entry
     *
     * @signature function(entry)
     * @param entry {Map} The entry to process
     */
    process : function(entry)
    {
      // Append new content
      this.__log.appendChild(qx.log.appender.Util.toHtml(entry));

      // Scroll down
      this.__scrollDown();
    },


    /**
     * Automatically scroll down to the last line
     */
    __scrollDown : function() {
      this.__log.scrollTop = this.__log.scrollHeight;
    },





    /*
    ---------------------------------------------------------------------------
      VISIBILITY TOGGLING
    ---------------------------------------------------------------------------
    */

    /** @type {Boolean} Flag to store last visibility status */
    __visible : true,


    /**
     * Toggles the visibility of the console between visible and hidden.
     *
     */
    toggle : function()
    {
      if (!this.__main)
      {
        this.init();
      }
      else if (this.__main.style.display == "none")
      {
        this.show();
      }
      else
      {
        this.__main.style.display = "none";
      }
    },


    /**
     * Shows the console.
     *
     */
    show : function()
    {
      if (!this.__main) {
        this.init();
      } else {
        this.__main.style.display = "block";
        this.__log.scrollTop = this.__log.scrollHeight;
      }
    },


    /*
    ---------------------------------------------------------------------------
      COMMAND LINE SUPPORT
    ---------------------------------------------------------------------------
    */

    /** @type {Array} List of all previous commands. */
    __history : [],


    /**
     * Executes the currently given command
     *
     */
    execute : function()
    {
      var value = this.__cmd.value;
      if (value == "") {
        return;
      }

      if (value == "clear") {
        this.clear();
        return;
      }

      var command = document.createElement("div");
      command.innerHTML = qx.log.appender.Util.escapeHTML(">>> " + value);
      command.className = "user-command";

      this.__history.push(value);
      this.__lastCommand = this.__history.length;
      this.__log.appendChild(command);
      this.__scrollDown();

      try {
        var ret = window.eval(value);
      }
      catch (ex) {
        qx.log.Logger.error(ex);
      }

      if (ret !== undefined) {
        qx.log.Logger.debug(ret);
      }
    },




    /*
    ---------------------------------------------------------------------------
      EVENT LISTENERS
    ---------------------------------------------------------------------------
    */

    /**
     * Event handler for resize listener
     *
     * @param e {Event} Event object
     */
    __onResize : function(e) {
      this.__log.style.height = (this.__main.clientHeight - this.__main.firstChild.offsetHeight - this.__main.lastChild.offsetHeight) + "px";
    },


    /**
     * Event handler for keydown listener
     *
     * @param e {Event} Event object
     */
    __onKeyPress : function(e)
    {
      var iden = e.getKeyIdentifier();

      // Console toggling
      if ((iden == "F7") || (iden == "D" && e.isCtrlPressed()))
      {
        this.toggle();
        e.preventDefault();
      }

      // Not yet created
      if (!this.__main) {
        return;
      }

      // Active element not in console
      if (!qx.dom.Hierarchy.contains(this.__main, e.getTarget())) {
        return;
      }

      // Command execution
      if (iden == "Enter" && this.__cmd.value != "")
      {
        this.execute();
        this.__cmd.value = "";
      }

      // History managment
      if (iden == "Up" || iden == "Down")
      {
        this.__lastCommand += iden == "Up" ? -1 : 1;
        this.__lastCommand = Math.min(Math.max(0, this.__lastCommand), this.__history.length);

        var entry = this.__history[this.__lastCommand];
        this.__cmd.value = entry || "";
        this.__cmd.select();
      }
    }
  },




  /*
  *****************************************************************************
     DEFER
  *****************************************************************************
  */

  defer : function(statics) {
    qx.event.Registration.addListener(document.documentElement, "keypress", statics.__onKeyPress, statics);
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Tino Butz (tbtz)
     * Christopher Zuendorf (czuendorf)

************************************************************************ */

/**
 * The page manager decides automatically whether the added pages should be
 * displayed in a master/detail view (for tablet) or as a plain card layout (for
 * smartphones).
 *
 * *Example*
 *
 * Here is a little example of how to use the manager.
 *
 * <pre class='javascript'>
 *  var manager = new qx.ui.mobile.page.Manager();
 *  var page1 = new qx.ui.mobile.page.NavigationPage();
 *  var page2 = new qx.ui.mobile.page.NavigationPage();
 *  var page3 = new qx.ui.mobile.page.NavigationPage();
 *  manager.addMaster(page1);
 *  manager.addDetail([page2,page3]);
 *
 *  page1.show();
 * </pre>
 *
 *
 */
qx.Class.define("qx.ui.mobile.page.Manager",
{
  extend : qx.core.Object,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param isTablet {Boolean?} flag which triggers the manager to layout for tablet (or big screens/displays) or mobile devices. If parameter is null,
   * qx.core.Environment.get("device.type") is called for decision.
   * @param root {qx.ui.mobile.core.Widget?} widget which should be used as root for this manager.
   */
  construct : function(isTablet, root)
  {
    this.base(arguments);

    root = root || qx.core.Init.getApplication().getRoot();

    if (typeof isTablet !== "undefined" && isTablet !== null) {
      this.__isTablet = isTablet;
    } else {
      // If isTablet is undefined, call environment variable "device.type".
      // When "tablet" or "desktop" type >> do tablet layouting.
      this.__isTablet =
      qx.core.Environment.get("device.type") == "desktop" ||
      qx.core.Environment.get("device.type") == "tablet";
    }

    this.__detailNavigation = this._createDetailNavigation();
    this.__detailNavigation.getNavigationBar().hide();

    if (this.__isTablet) {
      this.__masterNavigation = this._createMasterNavigation();
      this.__masterNavigation.getNavigationBar().hide();

      this.__masterContainer = this._createMasterContainer();
      this.__detailContainer = this._createDetailContainer();

      this.__masterButton = this._createMasterButton();
      this.__masterButton.addListener("tap", this._onMasterButtonTap, this);

      this.__hideMasterButton = this._createHideMasterButton();
      this.__hideMasterButton.addListener("tap", this._onHideMasterButtonTap, this);

      this.__masterNavigation.addListener("update", this._onMasterContainerUpdate, this);
      this.__detailNavigation.addListener("update", this._onDetailContainerUpdate, this);

      root.add(this.__detailContainer, {flex:1});
      this.__masterContainer.add(this.__masterNavigation, {flex:1});
      this.__detailContainer.add(this.__detailNavigation, {flex:1});

      qx.event.Registration.addListener(window, "orientationchange", this._onLayoutChange, this);
      this.__masterContainer.addListener("resize", this._onLayoutChange, this);

      // On Tablet Mode, no Animation should be shown by default.
      this.__masterNavigation.getLayout().setShowAnimation(false);
      this.__detailNavigation.getLayout().setShowAnimation(false);

      this._onLayoutChange();
    } else {
      root.add(this.__detailNavigation, {flex:1});
    }
  },


  properties : {

    /**
     * The caption/label of the Master Button and Popup Title.
     */
    masterTitle : {
      init : "Master",
      check : "String",
      apply : "_applyMasterTitle"
    },


    /**
     * This flag indicates whether the masterContainer is hidden or not.
     */
    masterContainerHidden : {
      init : false,
      check : "Boolean",
      apply : "_onLayoutChange"
    },


    /**
     * The caption/label of the Hide Master Button.
     */
    hideMasterButtonCaption : {
      init : "Hide",
      check : "String",
      apply : "_applyHideMasterButtonCaption"
    },


    /**
     * This flag controls whether the MasterContainer can be hidden on Landscape.
     */
    allowMasterHideOnLandscape : {
      init : true,
      check : "Boolean",
      apply : "__updateMasterButtonVisibility"
    },


    /**
     *  This flag controls whether the MasterContainer hides on portrait view,
     *  when a Detail Page fires the lifecycle event "start".
     */
    hideMasterOnDetailStart : {
      init : true,
      check : "Boolean"
    }
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    __isTablet : null,
    __detailNavigation : null,
    __masterNavigation : null,
    __masterButton : null,
    __hideMasterButton : null,
    __masterPages : null,
    __detailPages : null,
    __masterContainer : null,
    __detailContainer : null,


    /**
     * Creates the master container.
     *
     * @return {qx.ui.mobile.container.Composite} The created container
     */
    _createMasterContainer : function() {
      var masterContainer = new qx.ui.mobile.container.Drawer(null, new qx.ui.mobile.layout.HBox());
      masterContainer.addCssClass("master-detail-master");
      masterContainer.setHideOnParentTouch(false);
      return masterContainer;
    },


    /**
     * Creates the detail container.
     *
     * @return {qx.ui.mobile.container.Composite} The created container
     */
    _createDetailContainer : function() {
      var detailContainer = new qx.ui.mobile.container.Composite(new qx.ui.mobile.layout.VBox());
      detailContainer.setDefaultCssClass("master-detail-detail");
      return detailContainer;
    },


    /**
     * Getter for the Master Container
     * @return {qx.ui.mobile.container.Drawer} The Master Container.
     */
    getMasterContainer : function() {
      return this.__masterContainer;
    },


    /**
     * Getter for the Detail Container
     * @return {qx.ui.mobile.container.Composite} The Detail Container.
     */
    getDetailContainer : function() {
      return this.__detailContainer;
    },


    /**
     * Returns the button for showing/hiding the masterContainer.
     * @return {qx.ui.mobile.navigationbar.Button}
     */
    getMasterButton : function() {
      return this.__masterButton;
    },


    /**
     * Returns the masterNavigation.
     * @return {qx.ui.mobile.container.Navigation}
     */
    getMasterNavigation : function() {
      return this.__masterNavigation;
    },


    /**
     * Returns the detailNavigation.
     * @return {qx.ui.mobile.container.Navigation}
     */
    getDetailNavigation : function() {
      return this.__detailNavigation;
    },


     /**
     * Factory method for the master button, which is responsible for showing/hiding masterContainer.
     * @return {qx.ui.mobile.navigationbar.Button}
     */
    _createMasterButton : function() {
      return new qx.ui.mobile.navigationbar.Button(this.getMasterTitle());
    },


    /**
     * Factory method for the hide master button, which is responsible for hiding masterContainer on Landscape view.
     * @return {qx.ui.mobile.navigationbar.Button}
     */
    _createHideMasterButton : function() {
      return new qx.ui.mobile.navigationbar.Button("Hide");
    },


    /**
    * Factory method for masterNavigation.
    * @return {qx.ui.mobile.container.Navigation}
    */
    _createMasterNavigation : function() {
      return new qx.ui.mobile.container.Navigation();
    },


    /**
     * Factory method for detailNavigation.
     * @return {qx.ui.mobile.container.Navigation}
     */
    _createDetailNavigation : function() {
      return new qx.ui.mobile.container.Navigation();
    },


    /**
     * Adds an array of NavigationPages to masterContainer, if __isTablet is true. Otherwise it will be added to detailContainer.
     * @param pages {qx.ui.mobile.page.NavigationPage[]|qx.ui.mobile.page.NavigationPage} Array of NavigationPages or single NavigationPage.
     */
    addMaster : function(pages) {
      if (this.__isTablet) {
        if(pages) {
          if(!qx.lang.Type.isArray(pages)) {
            pages = [pages];
          }

          for(var i = 0; i < pages.length; i++) {
            var masterPage = pages[i];
            qx.event.Registration.addListener(masterPage, "start", this._onMasterPageStart, this);
          }

          if(this.__masterPages) {
            this.__masterPages.concat(pages);
          } else {
            this.__masterPages = pages;
          }

          this._add(pages, this.__masterNavigation);
        }
      } else {
        this.addDetail(pages);
      }
    },


    /**
     * Called when a masterPage reaches lifecycle state "start". Then property masterTitle will be update with masterPage's title.
     * @param evt {qx.event.type.Event} source event.
     */
    _onMasterPageStart : function(evt) {
      var masterPage = evt.getTarget();
      var masterPageTitle = masterPage.getTitle();
      this.setMasterTitle(masterPageTitle);
    },


    /**
     * Adds an array of NavigationPage to the detailContainer.
     * @param pages {qx.ui.mobile.page.NavigationPage[]|qx.ui.mobile.page.NavigationPage} Array of NavigationPages or single NavigationPage.
     */
    addDetail : function(pages) {
      this._add(pages, this.__detailNavigation);

      if(pages && this.__isTablet) {
        if (!qx.lang.Type.isArray(pages)) {
          pages = [pages];
        }

        for(var i = 0; i < pages.length; i++) {
          var detailPage = pages[i];
          qx.event.Registration.addListener(detailPage, "start", this._onDetailPageStart, this);
        }

        if(this.__detailPages) {
          this.__detailPages.concat(pages);
        } else {
          this.__detailPages = pages;
        }
      }
    },


    /**
     * Called when a detailPage reaches lifecycle state "start".
     * @param evt {qx.event.type.Event} source event.
     */
    _onDetailPageStart : function(evt) {
      if(qx.bom.Viewport.isPortrait() && this.isHideMasterOnDetailStart()) {
        this.__masterContainer.hide();
      }
    },


    /**
     * Adds an array of NavigationPage to the target container.
     * @param pages {qx.ui.mobile.page.NavigationPage[]|qx.ui.mobile.page.NavigationPage} Array of NavigationPages, or NavigationPage.
     * @param target {qx.ui.mobile.container.Navigation} target navigation container.
     */
    _add : function(pages, target) {
      if (!qx.lang.Type.isArray(pages)) {
        pages = [pages];
      }

      for (var i = 0; i < pages.length; i++) {
        var page = pages[i];

        if (qx.core.Environment.get("qx.debug"))
        {
          this.assertInstance(page, qx.ui.mobile.page.NavigationPage);
        }

        if(this.__isTablet && !page.getShowBackButtonOnTablet()) {
          page.setShowBackButton(false);
        }

        page.setIsTablet(this.__isTablet);
        target.add(page);
      }
    },


    /**
     * Called when masterContainer is updated.
     * @param evt {qx.event.type.Data} source event.
     */
    _onMasterContainerUpdate : function(evt) {
      var widget = evt.getData();
      widget.getRightContainer().remove(this.__hideMasterButton);
      widget.getRightContainer().add(this.__hideMasterButton);
    },


    /**
     * Called when detailContainer is updated.
     * @param evt {qx.event.type.Data} source event.
     */
    _onDetailContainerUpdate : function(evt) {
      var widget = evt.getData();
      widget.getLeftContainer().remove(this.__masterButton);
      widget.getLeftContainer().add(this.__masterButton);
    },


    /**
    * Called when user taps on masterButton.
    */
    _onMasterButtonTap : function() {
      this.__masterContainer.show();

      if (qx.bom.Viewport.isLandscape()) {
        this.setMasterContainerHidden(false);
        this._createDetailContainerGap();
        this.__masterButton.exclude();
      }
    },


    /**
    * Called when user taps on hideMasterButton.
    */
    _onHideMasterButtonTap : function() {
      this.__masterContainer.hide();

      if (qx.bom.Viewport.isLandscape()) {
        this.__masterButton.show();
        this.setMasterContainerHidden(true);
        this._removeDetailContainerGap();
      }
    },


    /**
    * Called when layout of masterDetailContainer changes.
    */
    _onLayoutChange : function() {
      if(!this.__isTablet) {
        return;
      }

      if(qx.bom.Viewport.isLandscape()) {
        this.__masterContainer.setTransitionDuration(0);
        if(this.isMasterContainerHidden() === false) {
          this._createDetailContainerGap();
          this.__masterContainer.show();
        } else {
          this._removeDetailContainerGap();
          this.__masterContainer.hide();
        }
        this.__masterContainer.setHideOnParentTouch(false);
      } else {
        this.__masterContainer.setTransitionDuration(500);
        this.__masterContainer.setHideOnParentTouch(true);
        this.__masterContainer.hide();
        this._removeDetailContainerGap();
      }

      this.__updateMasterButtonVisibility();
    },


    /**
     * Moves detailContainer to the right edge of MasterContainer.
     * Creates spaces for aligning master and detail container aside each other.
     */
    _createDetailContainerGap : function() {
      qx.bom.element.Style.set(this.__detailContainer.getContainerElement(), "paddingLeft", this.__masterContainer.getSize()/16+"rem");

      qx.event.Registration.fireEvent(window, "resize");
    },


    /**
     * Moves detailContainer to the left edge of viewport.
     */
    _removeDetailContainerGap : function() {
      qx.bom.element.Style.set(this.__detailContainer.getContainerElement(), "paddingLeft", null);

      qx.event.Registration.fireEvent(window, "resize");
    },


    /**
    * Show/hides master button.
    */
    __updateMasterButtonVisibility : function()
    {
      if(!this.__isTablet) {
        return;
      }

      if (qx.bom.Viewport.isPortrait()) {
        this.__masterButton.show();
        this.__hideMasterButton.show();
      } else {
        this.__masterButton.exclude();
        this.__hideMasterButton.exclude();

        if(this.isAllowMasterHideOnLandscape()) {
          if(this.isMasterContainerHidden()) {
            this.__masterButton.show();
          } else {
            this.__hideMasterButton.show();
          }
        }
      }
    },


    /**
    * Called on property changes of hideMasterButtonCaption.
    * @param value {String} new caption
    * @param old {String} previous caption
    */
    _applyHideMasterButtonCaption : function(value, old) {
      if(this.__isTablet) {
        this.__hideMasterButton.setLabel(value);
      }
    },


    /**
    * Called on property changes of masterTitle.
    * @param value {String} new title
    * @param old {String} previous title
    */
    _applyMasterTitle : function(value, old) {
      if(this.__isTablet) {
        this.__masterButton.setLabel(value);
      }
    }
  },


  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function()
  {
    if(this.__masterPages) {
      for(var i = 0; i < this.__masterPages.length; i++) {
        var masterPage = this.__masterPages[i];

        qx.event.Registration.removeListener(masterPage, "start", this._onMasterPageStart, this);
      }
    }
    if(this.___detailPages) {
      for(var j = 0; j < this.___detailPages.length; j++) {
        var detailPage = this.___detailPages[j];

        qx.event.Registration.removeListener(detailPage, "start", this._onDetailPageStart, this);
      }
    }

    if(this.__isTablet) {
      this.__masterContainer.removeListener("resize", this._onLayoutChange, this);
      qx.event.Registration.removeListener(window, "orientationchange", this._onLayoutChange, this);
    }

    this.__masterPages = this.__detailPages =  null;

    this._disposeObjects("__detailNavigation", "__masterNavigation", "__masterButton");
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2012 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Christopher Zuendorf (czuendorf)

************************************************************************ */

/**
 * Creates a drawer widget inside the given parent widget. The parent widget can
 * be assigned as a constructor argument. If no parent is set, the application's
 * root will be assumed as parent. A drawer widget can be assigned to left, right,
 * top or bottom edge of its parent by property <code>orientation</code>. The drawer floats
 * in on <code>show()</code> and floats out on <code>hide()</code>. Additionally the drawer is shown by
 * swiping in reverse direction on the parent edge to where the drawer is placed
 * to: Orientation: <code>left</code>, Swipe: <code>right</code> on parents edge: Drawer is shown etc.
 * The drawer is hidden when user touches the parent area outside of the drawer.
 * This behaviour can be deactivated by the property <code>hideOnParentTouch</code>.
 *
 * <pre class='javascript'>
 *
 *  var drawer = new qx.ui.mobile.container.Drawer();
 *  drawer.setOrientation("right");
 *  drawer.setTouchOffset(100);
 *
 *  var button = new qx.ui.mobile.form.Button("A Button");
 *  drawer.add(button);
 * </pre>
 *
 *
 */
qx.Class.define("qx.ui.mobile.container.Drawer",
{
  extend : qx.ui.mobile.container.Composite,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param parent {qx.ui.mobile.container.Composite?null} The widget to which
   * the drawer should be added, if null it is added to app root.
   * @param layout {qx.ui.mobile.layout.Abstract?null} The layout that should be
   * used for this container.
   */
  construct : function(parent, layout)
  {
    this.base(arguments);

    if (layout) {
      this.setLayout(layout);
    }

    this.initOrientation();
    this.initPositionZ();

    if(parent) {
      if (qx.core.Environment.get("qx.debug"))
      {
        this.assertInstance(parent, qx.ui.mobile.container.Composite);
      }

      parent.add(this);
    } else {
      qx.core.Init.getApplication().getRoot().add(this);
    }

    this.__parent = this.getLayoutParent();
    this.__parent.addCssClass("drawer-parent");

    this.__parent.addListener("swipe", this._onParentSwipe,this);
    this.__parent.addListener("touchstart", this._onParentTouchStart,this);
    this.__parent.addListener("back", this.forceHide, this);

    this.__touchStartPosition = [0,0];
    this.__inAnimation = false;

    this.forceHide();
  },


  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /**
     * Fired when the drawer changes its size.
     */
    resize : "qx.event.type.Data"
  },


  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */
  properties : {
    // overridden
    defaultCssClass : {
      refine : true,
      init : "drawer"
    },


    /** Property for setting the orientation of the drawer.
     * Allowed values are: <code>left</code>,<code>right</code>,<code>top</code>,<code>bottom</code> */
    orientation : {
      check : "String",
      init : "left",
      apply : "_applyOrientation"
    },


    /** The size of the drawer in <code>px</code>. This value is interpreted as width if
    * orientation is <code>left | right</code>, as height if orientation is
    * <code>top | bottom</code>. */
    size : {
      check : "Integer",
      init : 300,
      apply : "_applySize",
      event : "resize"
    },


    /** Indicates whether the drawer should hide when the parent area of it is touched.  */
    hideOnParentTouch : {
      check : "Boolean",
      init : true
    },


    /** Sets the size of the touching area, where the drawer reacts on swipes for opening itself. */
    touchOffset : {
      check : "Integer",
      init : 20
    },


    /** The duration time of the transition between shown/hidden state in ms. */
    transitionDuration : {
      check : "Integer",
      init : 500,
      apply : "_applyTransitionDuration"
    },


    /** Sets the drawer zIndex position relative to its parent. */
    positionZ : {
      check : [ "above", "below"],
      init : "above",
      apply : "_applyPositionZ"
    }
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */
  members :
  {
    __touchStartPosition : null,
    __parent : null,
    __inAnimation : null,
    __lastLandscape : null,
    __transitionEnabled : null,


    // property apply
    _applyOrientation : function(value, old) {
      this.removeCssClass(old);
      this.addCssClass(value);

      // Reapply width of height size depending on orientation.
      this._applySize(this.getSize());
    },


    // property apply
    _applyPositionZ : function(value,old) {
      this.removeCssClass(old);
      this.addCssClass(value);

      if(value == "above") {
        // Reset transitions for "below" mode.
        if(this.__parent) {
          this.__parent.setTranslateX(null);
          this.__parent.setTranslateY(null);
        }
        this.setTranslateX(null);
        this.setTranslateY(null);
      } else {
        this.__parent.setTranslateX(0);
        this.__parent.setTranslateY(0);
      }
    },


    /**
    * @deprecated {3.5} Please use setSize() instead.
    * Sets the user value of the property width.
    * @param value {Integer} New value for property
    */
    setWidth : function(value) {
      if (qx.core.Environment.get("qx.debug"))
      {
        qx.log.Logger.deprecatedMethodWarning(arguments.callee,"The method 'setWidth()' is deprecated. Please use 'setSize()' instead.");
      }
      this.setSize(value);
    },


    /**
    * @deprecated {3.5} Please use getSize() instead.
    * Gets the user value of the property width.
    * @return {Integer} the value.
    */
    getWidth : function() {
      if (qx.core.Environment.get("qx.debug"))
      {
        qx.log.Logger.deprecatedMethodWarning(arguments.callee,"The method 'getWidth()' is deprecated. Please use 'getSize()' instead.");
      }
      return this.getSize();
    },


    /**
    * @deprecated {3.5} Please use resetSize() instead.
    * Resets the user value of the property width.
    */
    resetWidth : function() {
      if (qx.core.Environment.get("qx.debug"))
      {
        qx.log.Logger.deprecatedMethodWarning(arguments.callee,"The method 'resetWidth()' is deprecated. Please use 'resetSize()' instead.");
      }
      this.resetSize();
    },


    /**
    * @deprecated {3.5} Please use setSize() instead.
    * Sets the user value of the property height.
    * @param value {Integer} New value for property
    */
    setHeight : function(value) {
      if (qx.core.Environment.get("qx.debug"))
      {
        qx.log.Logger.deprecatedMethodWarning(arguments.callee,"The method 'setHeight()' is deprecated. Please use 'setSize()' instead.");
      }
      this.setSize(value);
    },


    /**
    * @deprecated {3.5} Please use getSize() instead.
    * Gets the user value of the property height.
    * @return {Integer} the value.
    */
    getHeight : function() {
      if (qx.core.Environment.get("qx.debug"))
      {
        qx.log.Logger.deprecatedMethodWarning(arguments.callee,"The method 'getHeight()' is deprecated. Please use 'getSize()' instead.");
      }
      return this.getSize();
    },


    /**
    * @deprecated {3.5} Please use resetSize() instead.
    * Resets the user value of the property height.
    */
    resetHeight : function() {
      if (qx.core.Environment.get("qx.debug"))
      {
        qx.log.Logger.deprecatedMethodWarning(arguments.callee,"The method 'resetHeight()' is deprecated. Please use 'resetSize()' instead.");
      }
      this.resetSize();
    },


    /**
     * @deprecated {3.5} Please use this.__parent.toggleCssClass instead.
     */
    _toggleParentBlockedState : function() {
      this.__parent.toggleCssClass("blocked");
    },


    // property apply
    _applySize : function(value) {
      var height = null;
      var width = null;

      var remSize = (value / 16);

      if (this.getOrientation() == "left" || this.getOrientation() == "right") {
        width = remSize + "rem";
      } else {
        height = remSize + "rem";
      }

      this._setStyle("height", height);
      this._setStyle("width", width);
    },


    /**
    * Handler for the "transitionEnd" event.
    * @param evt {Event} the event.
    */
    _onTransitionEnd : function(evt) {
      if(evt) {
        qx.bom.Element.removeListener(evt.getTarget(), "transitionEnd", this._onTransitionEnd, this);
      }

      this.__inAnimation = false;
      this._disableTransition();

      if (this.isHidden()) {
        this.exclude();
        this.__parent.removeCssClass("blocked");
      }

      // Check for orientation change during transition.
      if(this.__lastLandscape != qx.bom.Viewport.isLandscape()) {
        this.show();
      }
    },


    // property apply
    _applyTransitionDuration : function(value,old) {
      this.__transitionEnabled = value > 0;
    },


    /**
     * Shows the drawer.
     */
    show : function()
    {
      if(this.__inAnimation || !this.isHidden()) {
        return;
      }

      this.base(arguments);

      this.__parent.addCssClass("blocked");

      this.__lastLandscape = qx.bom.Viewport.isLandscape();

      if (this.getPositionZ() == "below") {
        if(this.__parent) {
          this.__parent.setTranslateX(0);
          this.__parent.setTranslateY(0);
        }

        this.setTranslateX(0);
        this.setTranslateY(0);

        if(this.getOrientation() == "left") {
          this.__parent.setTranslateX(this.getSize());
          this.setTranslateX(-this.getSize());
        } else if(this.getOrientation() == "right") {
          this.__parent.setTranslateX(-this.getSize());
          this.setTranslateX(this.getSize());
        } else if(this.getOrientation() == "top") {
          this.__parent.setTranslateY(this.getSize());
          this.setTranslateY(-this.getSize());
        } else if(this.getOrientation() == "bottom") {
          this.__parent.setTranslateY(-this.getSize());
          this.setTranslateY(this.getSize());
        }
      }
      if (this.getTransitionDuration() > 0) {
        this._enableTransition();
        this.__inAnimation = true;
        qx.bom.Element.addListener(this._getTransitionTarget().getContentElement(), "transitionEnd", this._onTransitionEnd, this);
        setTimeout(function() {
          this.removeCssClass("hidden");
        }.bind(this), 0);
      } else {
        this.removeCssClass("hidden");
        this._onTransitionEnd();
      }
    },


    /**
     * Hides the drawer.
     */
    hide : function() {
      if(this.__inAnimation || this.isHidden()) {
        return;
      }

      this.__lastLandscape = qx.bom.Viewport.isLandscape();

      if (this.getPositionZ() == "below") {
        this.__parent.setTranslateX(0);
        this.__parent.setTranslateY(0);
      }

      if (this.getTransitionDuration() > 0) {
        this.__inAnimation = true;
        this._enableTransition();
        qx.bom.Element.addListener(this._getTransitionTarget().getContentElement(), "transitionEnd", this._onTransitionEnd, this);
        setTimeout(function() {
          this.addCssClass("hidden");
        }.bind(this), 0);
      } else {
        this.addCssClass("hidden");
        this._onTransitionEnd();
      }
    },


    /**
     * Strict way to hide this drawer. Removes the blocker from the parent,
     * and hides the drawer without any animation. Should be called when drawer's
     * parent is animated and drawer should hide immediately.
     */
    forceHide : function() {
      this._disableTransition();

      if (this.getPositionZ() == "below") {
        this.__parent.setTranslateX(0);
        this.__parent.setTranslateY(0);
      }

      this.__parent.removeCssClass("blocked");

      this.addCssClass("hidden");
      this.exclude();
    },


    // overridden
    isHidden : function() {
      return this.hasCssClass("hidden");
    },


    /**
     * Enables the transition on this drawer.
     */
    _enableTransition : function() {
      qx.bom.element.Style.set(this._getTransitionTarget().getContentElement(), "transition", "all "+this.getTransitionDuration()+"ms ease-in-out");
    },


   /**
     * Disables the transition on this drawer.
     */
    _disableTransition : function() {
      qx.bom.element.Style.set(this._getTransitionTarget().getContentElement(),"transition", null);
    },


    /**
    * Returns the target widget which is responsible for the transition handling.
    * @return {qx.ui.mobile.core.Widget} the transition target widget.
    */
    _getTransitionTarget : function() {
      if (this.getPositionZ() == "below") {
        return this.__parent;
      } else {
        return this;
      }
    },


    /**
     * Toggle the visibility of the drawer.
     * @return {Boolean} the new visibility state.
     */
    toggleVisibility : function() {
      if(this.isHidden()) {
        this.show();
        return true;
      } else {
        this.hide();
        return false;
      }
    },


    /**
     * Handles a touch on application's root.
     * @param evt {qx.module.event.Touch} Handled touch event.
     */
    _onParentTouchStart : function(evt) {
      var clientX = evt.getAllTouches()[0].clientX;
      var clientY = evt.getAllTouches()[0].clientY;

      this.__touchStartPosition = [clientX,clientY];

      var isShown = !this.hasCssClass("hidden");
      if(isShown && this.isHideOnParentTouch()) {
        var location = qx.bom.element.Location.get(this.getContainerElement());

        if (this.getOrientation() =="left" && this.__touchStartPosition[0] > location.right
        || this.getOrientation() =="top" && this.__touchStartPosition[1] > location.bottom
        || this.getOrientation() =="bottom" && this.__touchStartPosition[1] < location.top
        || this.getOrientation() =="right" && this.__touchStartPosition[0] < location.left)
        {
          // First touch on overlayed page should be ignored.
          evt.preventDefault();

          this.hide();
        }
      }
    },


    /**
     * Handles a swipe on layout parent.
     * @param evt {qx.module.event.Touch} Handled touch event.
     */
    _onParentSwipe : function(evt) {
      var direction = evt.getDirection();
      var isHidden = this.hasCssClass("hidden");
      if(isHidden) {
        var location = qx.bom.element.Location.get(this.getContainerElement());

        if (
          (direction == "right"
          && this.getOrientation() == "left"
          && this.__touchStartPosition[0] < location.right + this.getTouchOffset()
          && this.__touchStartPosition[0] > location.right)
          ||
          (direction == "left"
          && this.getOrientation() == "right"
          && this.__touchStartPosition[0] > location.left - this.getTouchOffset()
          && this.__touchStartPosition[0] < location.left)
          ||
          (direction == "down"
          && this.getOrientation() == "top"
          && this.__touchStartPosition[1] < this.getTouchOffset() + location.bottom
          && this.__touchStartPosition[1] > location.bottom)
          ||
          (direction == "up"
          && this.getOrientation() == "bottom"
          && this.__touchStartPosition[1] > location.top - this.getTouchOffset()
          && this.__touchStartPosition[1] < location.top)
        )
        {
          this.show();
        }
      }
    }
  },


  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */
  destruct : function()
  {
    this.__parent.removeListener("swipe", this._onParentSwipe, this);
    this.__parent.removeListener("touchstart", this._onParentTouchStart, this);
    this.__parent.removeListener("back", this.forceHide, this);

    this.__touchStartPosition = this.__inAnimation = this.__parent = this.__transitionEnabled = null;
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Tino Butz (tbtz)

************************************************************************ */

/**
 * The navigation controller includes already a {@link qx.ui.mobile.navigationbar.NavigationBar}
 * and a {@link qx.ui.mobile.container.Composite} container with a {@link qx.ui.mobile.layout.Card} layout.
 * All widgets that implement the {@link qx.ui.mobile.container.INavigation}
 * interface can be added to the container. The added widget provide the title
 * widget and the left/right container, which will be automatically merged into
 * navigation bar.
 *
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   var container = new qx.ui.mobile.container.Navigation();
 *   this.getRoot(container);
 *   var page = new qx.ui.mobile.page.NavigationPage();
 *   container.add(page);
 *   page.show();
 * </pre>
 */
qx.Class.define("qx.ui.mobile.container.Navigation",
{
  extend : qx.ui.mobile.container.Composite,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments, new qx.ui.mobile.layout.VBox());

    this.__navigationBar = this._createNavigationBar();
    if (this.__navigationBar) {
      this._add(this.__navigationBar);
    }

    this.__content = this._createContent();
    this._add(this.__content, {flex:1});
  },


  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */
  properties : {
    // overridden
    defaultCssClass : {
      refine : true,
      init : "navigation"
    }
  },


  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */
  events :
  {
    /** Fired when the navigation bar gets updated */
    "update" : "qx.event.type.Data"
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    __navigationBar : null,
    __content : null,
    __layout : null,


    // overridden
    add : function(widget) {
      if (qx.core.Environment.get("qx.debug"))
      {
        this.assertInterface(widget, qx.ui.mobile.container.INavigation);
      }

      this.getContent().add(widget);
    },


    // overridden
    remove : function(widget) {
      if (qx.core.Environment.get("qx.debug"))
      {
        this.assertInterface(widget, qx.ui.mobile.container.INavigation);
      }
      this.getContent().remove(widget);
    },


    /**
     * Returns the content container. Add all your widgets to this container.
     *
     * @return {qx.ui.mobile.container.Composite} The content container
     */
    getContent : function()
    {
      return this.__content;
    },


    /**
     * Returns the assigned card layout.
     * @return {qx.ui.mobile.layout.Card} assigned Card Layout.
     */
    getLayout : function() {
      return this.__layout;
    },


    /**
     * Returns the navigation bar.
     *
     * @return {qx.ui.mobile.navigationbar.NavigationBar} The navigation bar.
     */
    getNavigationBar : function()
    {
      return this.__navigationBar;
    },


    /**
     * Creates the content container.
     *
     * @return {qx.ui.mobile.container.Composite} The created content container
     */
    _createContent : function()
    {
      this.__layout = new qx.ui.mobile.layout.Card();
      var content = new qx.ui.mobile.container.Composite(this.__layout);
      this.__layout.addListener("updateLayout", this._onUpdateLayout, this);

      this.getLayout().addListener("animationStart", this._onAnimationStart, this);
      this.getLayout().addListener("animationEnd", this._onAnimationEnd, this);

      return content;
    },


    /**
    * Handler for the "animationStart" event on the layout.
    */
    _onAnimationStart : function() {
      this.addCssClass("blocked");
    },


    /**
    * Handler for the "animationEnd" event on the layout.
    */
    _onAnimationEnd : function() {
      this.removeCssClass("blocked");
    },


    /**
     * Event handler. Called when the "updateLayout" event occurs.
     *
     * @param evt {qx.event.type.Data} The causing event
     */
    _onUpdateLayout : function(evt) {
      var data = evt.getData();
      var widget = data.widget;
      var action = data.action;
      if (action == "visible") {
        this._update(widget);
      }
    },


    /**
     * Updates the navigation bar depending on the set widget.
     *
     * @param widget {qx.ui.mobile.core.Widget} The widget that should be merged into the navigation bar.
     */
    _update : function(widget) {
      var navigationBar = this.getNavigationBar();

      this._setStyle("transitionDuration", widget.getNavigationBarToggleDuration()+"s");

      if(widget.isNavigationBarHidden()) {
        this.addCssClass("hidden");
      } else {
        navigationBar.show();
        this.removeCssClass("hidden");
      }

      navigationBar.removeAll();

      var leftContainer = widget.getLeftContainer();
      if (leftContainer) {
        navigationBar.add(leftContainer);
      }

      var title = widget.getTitleWidget();
      if (title) {
        navigationBar.add(title, {flex:1});
      }

      var rightContainer = widget.getRightContainer();
      if (rightContainer) {
        navigationBar.add(rightContainer);
      }

      this.fireDataEvent("update", widget);
    },


    /**
     * Creates the navigation bar.
     *
     * @return {qx.ui.mobile.navigationbar.NavigationBar} The created navigation bar
     */
    _createNavigationBar : function()
    {
      return new qx.ui.mobile.navigationbar.NavigationBar();
    }
  },


  destruct : function()
  {
    this.getLayout().removeListener("animationStart",this._onAnimationStart, this);
    this.getLayout().removeListener("animationEnd",this._onAnimationEnd, this);

    this._disposeObjects("__navigationBar", "__content","__layout");
    this.__navigationBar = this.__content = this.__layout = null;
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Tino Butz (tbtz)
     * Christopher Zuendorf (czuendorf)

************************************************************************ */

/**
 * A card layout.
 *
 * The card layout lays out widgets in a stack. Call show to display a widget.
 * Only the widget which show method is called is displayed. All other widgets are excluded.
 *
 *
 * *Example*
 *
 * Here is a little example of how to use the Card layout.
 *
 * <pre class="javascript">
 * var layout = new qx.ui.mobile.layout.Card());
 * var container = new qx.ui.mobile.container.Composite(layout);
 *
 * var label1 = new qx.ui.mobile.basic.Label("1");
 * container.add(label1);
 * var label2 = new qx.ui.mobile.basic.Label("2");
 * container.add(label2);
 *
 * label2.show();
 * </pre>
 *
 * @use(qx.event.handler.Transition)
 */
qx.Class.define("qx.ui.mobile.layout.Card",
{
  extend : qx.ui.mobile.layout.Abstract,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */
  construct : function()
  {
    this.base(arguments);

    this.__cardAnimation = new qx.ui.mobile.layout.CardAnimation();
  },


  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /** Fired when the animation of a page transition starts */
    animationStart : "qx.event.type.Data",

    /** Fired when the animation of a page transition ends */
    animationEnd : "qx.event.type.Data"
  },


 /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** The default animation to use for page transition */
    defaultAnimation :
    {
      check : "String",
      init : "slide"
    },


    /** Flag which indicates, whether animation is needed, or widgets should only swap. */
    showAnimation :
    {
      check : "Boolean",
      init : true
    },


    /** Transition duration of each animation. */
    animationDuration :
    {
      check : "Integer",
      init : 350
    }
  },


 /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    __nextWidget : null,
    __currentWidget : null,
    __inAnimation : null,
    __animation : null,
    __reverse : null,
    __cardAnimation : null,


    // overridden
    _getCssClasses : function() {
      return ["layout-card","vbox"];
    },


    // overridden
    connectToChildWidget : function(widget)
    {
      this.base(arguments);
      if (widget) {
        widget.addCssClass("layout-card-item");
        widget.addCssClass("boxFlex1");
        widget.exclude();
      }
    },


    // overridden
    disconnectFromChildWidget : function(widget)
    {
      this.base(arguments);
      widget.removeCssClass("layout-card-item");
    },


    // overridden
    updateLayout : function(widget, action, properties)
    {
      if (action == "visible")
      {
        this._showWidget(widget, properties);
      }
      this.base(arguments, widget, action, properties);
    },


    /**
     * Setter for this.__cardAnimation.
     * @param value {qx.ui.mobile.layout.CardAnimation} the new CardAnimation object.
     */
    setCardAnimation : function(value) {
      this.__cardAnimation = value;
    },


    /**
     * Getter for this.__cardAnimation.
     * @return {qx.ui.mobile.layout.CardAnimation} the current CardAnimation object.
     */
    getCardAnimation : function() {
      return this.__cardAnimation;
    },


    /**
     * Shows the widget with the given properties.
     *
     * @param widget {qx.ui.mobile.core.Widget} The target widget
     * @param properties {Map} The layout properties to set. Key / value pairs.
     */
    _showWidget : function(widget, properties)
    {
      if (this.__nextWidget == widget) {
        return;
      }

      if (this.__inAnimation) {
        this.__stopAnimation();
      }

      this.__nextWidget = widget;
      if (this.__currentWidget && this.getShowAnimation() && qx.core.Environment.get("css.transform.3d")) {
        properties = properties || {};

        this.__animation = properties.animation || this.getDefaultAnimation();

        properties.reverse = properties.reverse === null ? false : properties.reverse;

        this.__reverse = properties.fromHistory || properties.reverse;

        this.__startAnimation(widget);
      } else {
        this._swapWidget();
      }
    },


    /**
     * Excludes the current widget and sets the next widget to the current widget.
     */
    _swapWidget : function() {
      if (this.__currentWidget) {
        this.__currentWidget.exclude();
      }
      this.__currentWidget = this.__nextWidget;
    },


    /**
     * Fix size, only if widget has mixin MResize set,
     * and nextWidget is set.
     *
     * @param widget {qx.ui.mobile.core.Widget} The target widget which should have a fixed size.
     */
    _fixWidgetSize : function(widget) {
      if(widget) {
        var hasResizeMixin = qx.Class.hasMixin(widget.constructor,qx.ui.mobile.core.MResize);
        if(hasResizeMixin) {
          // Size has to be fixed for animation.
          widget.fixSize();
        }
      }
    },


    /**
     * Releases recently fixed widget size (width/height). This is needed for allowing further
     * flexbox layouting.
     *
     * @param widget {qx.ui.mobile.core.Widget} The target widget which should have a flexible size.
     */
    _releaseWidgetSize : function(widget) {
      if(widget) {
        var hasResizeMixin = qx.Class.hasMixin(widget.constructor,qx.ui.mobile.core.MResize);
        if(hasResizeMixin) {
          // Size has to be released after animation.
          widget.releaseFixedSize();
        }
      }
    },


    /**
     * Starts the animation for the page transition.
     *
     * @param widget {qx.ui.mobile.core.Widget} The target widget
     */
    __startAnimation : function(widget)
    {
      // Fix size of current and next widget, then start animation.
      this.__inAnimation = true;

      this.fireDataEvent("animationStart", [this.__currentWidget, widget]);
      var fromElement = this.__currentWidget.getContainerElement();
      var toElement = widget.getContainerElement();

      var onAnimationEnd = qx.lang.Function.bind(this._onAnimationEnd, this);

      if(qx.core.Environment.get("event.mspointer")) {
        qx.bom.Event.addNativeListener(fromElement, "MSAnimationEnd", onAnimationEnd, false);
        qx.bom.Event.addNativeListener(toElement, "MSAnimationEnd", onAnimationEnd, false);
      }

      qx.event.Registration.addListener(fromElement, "animationEnd", this._onAnimationEnd, this);
      qx.event.Registration.addListener(toElement, "animationEnd", this._onAnimationEnd, this);

      var fromCssClasses = this.__getAnimationClasses("out");
      var toCssClasses = this.__getAnimationClasses("in");

      this._widget.addCssClass("animationParent");

      var toElementAnimation = this.__cardAnimation.getAnimation(this.__animation, "in", this.__reverse);
      var fromElementAnimation = this.__cardAnimation.getAnimation(this.__animation, "out", this.__reverse);

      qx.bom.element.Class.addClasses(toElement, toCssClasses);
      qx.bom.element.Class.addClasses(fromElement, fromCssClasses);

      qx.bom.element.Animation.animate(toElement, toElementAnimation);
      qx.bom.element.Animation.animate(fromElement, fromElementAnimation);
    },


    /**
     * Event handler. Called when the animation of the page transition ends.
     *
     * @param evt {qx.event.type.Event} The causing event
     */
    _onAnimationEnd : function(evt)
    {
      this.__stopAnimation();
      this.fireDataEvent("animationEnd", [this.__currentWidget, this.__nextWidget]);
    },


    /**
     * Stops the animation for the page transition.
     */
    __stopAnimation : function()
    {
      if (this.__inAnimation)
      {
        var fromElement = this.__currentWidget.getContainerElement();
        var toElement = this.__nextWidget.getContainerElement();

        qx.event.Registration.removeListener(fromElement, "animationEnd", this._onAnimationEnd, this);
        qx.event.Registration.removeListener(toElement, "animationEnd", this._onAnimationEnd, this);

        qx.bom.element.Class.removeClasses(fromElement, this.__getAnimationClasses("out"));
        qx.bom.element.Class.removeClasses(toElement, this.__getAnimationClasses("in"));

        this._swapWidget();
        this._widget.removeCssClass("animationParent");
        this.__inAnimation = false;
      }
    },


    /**
     * Returns the animation CSS classes for a given direction. The direction
     * can be <code>in</code> or <code>out</code>.
     *
     * @param direction {String} The direction of the animation. <code>in</code> or <code>out</code>.
     * @return {String[]} The CSS classes for the set animation.
     */
    __getAnimationClasses : function(direction)
    {
      var classes = ["animationChild", this.__animation, direction];
      if (this.__reverse) {
        classes.push("reverse");
      }
      return classes;
    }
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Tino Butz (tbtz)

   ======================================================================

   This class contains code based on the following work:

   * Unify Project

     Homepage:
       http://unify-project.org

     Copyright:
       2009-2010 Deutsche Telekom AG, Germany, http://telekom.com

     License:
       MIT: http://www.opensource.org/licenses/mit-license.php

************************************************************************ */

/**
 * EXPERIMENTAL - NOT READY FOR PRODUCTION
 *
 * This class provides support for HTML5 transition and animation events.
 * Currently only WebKit and Firefox are supported.
 */
qx.Class.define("qx.event.handler.Transition",
{
  extend : qx.core.Object,
  implement : qx.event.IEventHandler,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * Create a new instance
   *
   * @param manager {qx.event.Manager} Event manager for the window to use
   */
  construct : function(manager)
  {
    this.base(arguments);

    this.__registeredEvents = {};
    this.__onEventWrapper = qx.lang.Function.listener(this._onNative, this);
  },




  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /** @type {Integer} Priority of this handler */
    PRIORITY : qx.event.Registration.PRIORITY_NORMAL,

    /** @type {Map} Supported event types */
    SUPPORTED_TYPES :
    {
      transitionEnd : 1,
      animationEnd : 1,
      animationStart : 1,
      animationIteration : 1
    },

    /** @type {Integer} Which target check to use */
    TARGET_CHECK : qx.event.IEventHandler.TARGET_DOMNODE,

    /** @type {Integer} Whether the method "canHandleEvent" must be called */
    IGNORE_CAN_HANDLE : true,

    /** Mapping of supported event types to native event types */
    TYPE_TO_NATIVE : null,

    /** Mapping of native event types to supported event types */
    NATIVE_TO_TYPE : null
  },





  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members:
  {
    __onEventWrapper : null,
    __registeredEvents : null,


    /*
    ---------------------------------------------------------------------------
      EVENT HANDLER INTERFACE
    ---------------------------------------------------------------------------
    */

    // interface implementation
    canHandleEvent: function(target, type) {
      // Nothing needs to be done here
    },


    // interface implementation
    /**
     * This method is called each time an event listener, for one of the
     * supported events, is added using {@link qx.event.Manager#addListener}.
     *
     * @param target {var} The target to, which the event handler should
     *     be attached
     * @param type {String} event type
     * @param capture {Boolean} Whether to attach the event to the
     *         capturing phase or the bubbling phase of the event.
     * @signature function(target, type, capture)
     */
    registerEvent: qx.core.Environment.select("engine.name",
    {
      "webkit|gecko|mshtml" : function(target, type, capture)
      {
        var hash = qx.core.ObjectRegistry.toHashCode(target) + type;

        var nativeType = qx.event.handler.Transition.TYPE_TO_NATIVE[type];

        this.__registeredEvents[hash] =
        {
          target:target,
          type : nativeType
        };

        qx.bom.Event.addNativeListener(target, nativeType, this.__onEventWrapper);
      },

      "default" : function() {}
    }),


    // interface implementation
    /**
     * This method is called each time an event listener, for one of the
     * supported events, is removed by using {@link qx.event.Manager#removeListener}
     * and no other event listener is listening on this type.
     *
     * @param target {var} The target from, which the event handler should
     *     be removed
     * @param type {String} event type
     * @param capture {Boolean} Whether to attach the event to the
     *         capturing phase or the bubbling phase of the event.
     * @signature function(target, type, capture)
     */
    unregisterEvent: qx.core.Environment.select("engine.name",
    {
      "webkit|gecko|mshtml" : function(target, type, capture)
      {
        var events = this.__registeredEvents;

        if (!events) {
          return;
        }

        var hash = qx.core.ObjectRegistry.toHashCode(target) + type;

        if (events[hash]) {
          delete events[hash];
        }

        qx.bom.Event.removeNativeListener(target, qx.event.handler.Transition.TYPE_TO_NATIVE[type], this.__onEventWrapper);
      },

      "default" : function() {}
    }),



    /*
    ---------------------------------------------------------------------------
      EVENT-HANDLER
    ---------------------------------------------------------------------------
    */

    /**
     * Global handler for the transition event.
     *
     * @signature function(domEvent)
     * @param domEvent {Event} DOM event
     */
    _onNative : qx.event.GlobalError.observeMethod(function(nativeEvent) {
      qx.event.Registration.fireEvent(nativeEvent.target, qx.event.handler.Transition.NATIVE_TO_TYPE[nativeEvent.type], qx.event.type.Event);
    })
  },





  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function()
  {
    var event;
    var events = this.__registeredEvents;

    for (var id in events)
    {
      event = events[id];
      if (event.target) {
        qx.bom.Event.removeNativeListener(event.target, event.type, this.__onEventWrapper);
      }
    }

    this.__registeredEvents = this.__onEventWrapper = null;
  },





  /*
  *****************************************************************************
     DEFER
  *****************************************************************************
  */

  defer : function(statics) {
    var aniEnv = qx.core.Environment.get("css.animation") || {};
    var transEnv = qx.core.Environment.get("css.transition") || {};

    var n2t = qx.event.handler.Transition.NATIVE_TO_TYPE = {};
    var t2n = qx.event.handler.Transition.TYPE_TO_NATIVE = {
      transitionEnd : transEnv["end-event"] || null,
      animationStart : aniEnv["start-event"] || null,
      animationEnd : aniEnv["end-event"] || null,
      animationIteration : aniEnv["iteration-event"] || null
    };

    for (var type in t2n) {
      var nate = t2n[type];
      n2t[nate] = type;
    }

    qx.event.Registration.addHandler(statics);
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2012 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
 * Christopher Zuendorf (czuendorf)

 ************************************************************************ */

/**
 * Contains all animations which are needed for page transitions on {@link qx.ui.mobile.layout.Card}.
 * Provides a convenience method {@link qx.ui.mobile.layout.CardAnimation#getAnimation} which
 * makes it possibility to resolve the right animation for a pageTransition out of the cardAnimationMap.
 */
qx.Class.define("qx.ui.mobile.layout.CardAnimation",
{
  extend : qx.core.Object,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */
  construct : function()
  {
    this.base(arguments);

    this._cardAnimationsMap = {
      "slide": {
        "in": qx.util.Animation.SLIDE_LEFT_IN,
        "out": qx.util.Animation.SLIDE_LEFT_OUT,
        "reverse": {
          "in": qx.util.Animation.SLIDE_RIGHT_IN,
          "out": qx.util.Animation.SLIDE_RIGHT_OUT
        }
      },
      "fade": {
        "in": qx.util.Animation.FADE_IN,
        "out": qx.util.Animation.FADE_OUT,
        "reverse": {
          "in": qx.util.Animation.FADE_IN,
          "out": qx.util.Animation.FADE_OUT
        }
      },
      "pop": {
        "in": qx.util.Animation.POP_IN,
        "out": qx.util.Animation.POP_OUT,
        "reverse": {
          "in": qx.util.Animation.POP_IN,
          "out": qx.util.Animation.POP_OUT
        }
      },
      "slideup": {
        "in": qx.util.Animation.SLIDE_UP_IN,
        "out": qx.util.Animation.SLIDE_UP_OUT,
        "reverse": {
          "in": qx.util.Animation.SLIDE_DOWN_IN,
          "out": qx.util.Animation.SLIDE_DOWN_OUT
        }
      },
      "flip": {
        "in": qx.util.Animation.FLIP_LEFT_IN,
        "out": qx.util.Animation.FLIP_LEFT_OUT,
        "reverse": {
          "in": qx.util.Animation.FLIP_RIGHT_IN,
          "out": qx.util.Animation.FLIP_RIGHT_OUT
        }
      },
      "swap": {
        "in": qx.util.Animation.SWAP_LEFT_IN,
        "out": qx.util.Animation.SWAP_LEFT_OUT,
        "reverse": {
          "in": qx.util.Animation.SWAP_RIGHT_IN,
          "out": qx.util.Animation.SWAP_RIGHT_OUT
        }
      },
      "cube": {
        "in": qx.util.Animation.CUBE_LEFT_IN,
        "out": qx.util.Animation.CUBE_LEFT_OUT,
        "reverse": {
          "in": qx.util.Animation.CUBE_RIGHT_IN,
          "out": qx.util.Animation.CUBE_RIGHT_OUT
        }
      }
    };
  },


  members :
  {
    _cardAnimationsMap : null,


    /**
    * Returns a map with properties for {@link qx.bom.element.Animation} according to the given input parameters.
    * @param animationName {String} the animation key
    * @param direction {String} the animation direction ("in" | "out")
    * @param reverse {Boolean} flag which indicates whether it is a reverse animation.
    * @return {Map} animation property map, intended for the usage with {@link qx.bom.element.Animation}
    */
    getAnimation : function(animationName, direction, reverse)
    {
      if (qx.core.Environment.get("qx.debug")) {
        if (!reverse) {
          this.assertNotUndefined(this._cardAnimationsMap[animationName], "Animation '" + animationName + "' is not defined.");
          this.assertNotUndefined(this._cardAnimationsMap[animationName][direction], "Animation '" + animationName + " " + direction + "' is not defined.");
        } else {
          this.assertNotUndefined(this._cardAnimationsMap[animationName], "Animation Reverse'" + animationName + "' is not defined.");
          this.assertNotUndefined(this._cardAnimationsMap[animationName]["reverse"], "Animation Reverse'" + animationName + "' is not defined.");
          this.assertNotUndefined(this._cardAnimationsMap[animationName]["reverse"][direction], "Animation Reverse'" + animationName + " " + direction + "' is not defined.");
        }
      }

      var animation = this._cardAnimationsMap[animationName];
      var animationObject = {};

      if (!reverse) {
        animationObject = animation[direction];
      } else {
        animationObject = animation["reverse"][direction];
      }

      return animationObject;
    },


    /**
     * Getter for the cardAnimationsMap.
     * @return {Map} the cardAnimationsMap.
     */
    getMap : function() {
      return this._cardAnimationsMap;
    }
  },


  destruct : function()
  {
    this._disposeObjects("_cardAnimationsMap");
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2012 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
 * Christopher Zuendorf (czuendorf)

 ************************************************************************ */

/**
 * Contains property maps for the usage with qx.bom.element.Animation {@link qx.bom.element.Animation}.
 * These animations can be used for page transitions for example.
 */
qx.Bootstrap.define("qx.util.Animation",
{

  statics :
  {
    /** Target slides in from right. */
    SLIDE_LEFT_IN : {
      duration: 350,
      timing: "linear",
      origin: "bottom center",
      keyFrames : {
        0: {
          translate : ["100%"]
        },
        100: {
          translate : ["0%"]
        }
      }
    },
    /** Target slides out from right.*/
    SLIDE_LEFT_OUT : {
      duration: 350,
      timing: "linear",
      origin: "bottom center",
      keyFrames : {
        0: {
          translate : ["0px"]
        },
        100: {
          translate : ["-100%"]
        }
      }
    },
    /** Target slides in from left.*/
    SLIDE_RIGHT_IN : {
      duration: 350,
      timing: "linear",
      origin: "bottom center",
      keyFrames : {
        0: {
          translate : ["-100%"]
        },
        100: {
          translate : ["0%"]
        }
      }
    },
    /** Target slides out from left.*/
    SLIDE_RIGHT_OUT : {
      duration: 350,
      timing: "linear",
      origin: "bottom center",
      keyFrames : {
        0: {
          translate : ["0px"]
        },
        100: {
          translate : ["100%"]
        }
      }
    },
    /** Target fades in. */
    FADE_IN : {
      duration: 350,
      timing: "linear",
      origin: "bottom center",
      keyFrames : {
        0: {
          opacity : ["0"]
        },
        100: {
          opacity : ["1"]
        }
      }
    },
    /** Target fades out. */
    FADE_OUT : {
      duration: 350,
      timing: "linear",
      origin: "bottom center",
      keyFrames : {
        0: {
          opacity : ["1"]
        },
        100: {
          opacity : ["0"]
        }
      }
    },
    /** Target pops in from center. */
    POP_IN : {
      duration: 350,
      timing: "linear",
      origin: "center",
      keyFrames : {
        0: {
          scale : [".2",".2"]
        },
        100: {
          scale : ["1","1"]
        }
      }
    },
    /** Target pops out from center. */
    POP_OUT : {
      duration: 350,
      timing: "linear",
      origin: "center",
      keyFrames : {
        0: {
          scale : ["1","1"]
        },
        100: {
          scale : [".2",".2"]
        }
      }
    },

    /** Target shrinks its height. */
    SHRINK_HEIGHT : {
      duration: 400,
      timing: "linear",
      origin: "top center",
      keep: 100,
      keyFrames : {
        0: {
          scale : [ "1", "1" ],
          opacity: 1
        },
        100: {
          scale : [ "1", "0" ],
          opacity : 0
        }
      }
    },

    /** Target grows its height. */
    GROW_HEIGHT : {
      duration: 400,
      timing: "linear",
      origin: "top center",
      keep: 100,
      keyFrames : {
        0: {
          scale : [ "1", "0" ],
          opacity: 0
        },
        100: {
          scale : [ "1", "1" ],
          opacity : 1
        }
      }
    },

    /** Target shrinks its width. */
    SHRINK_WIDTH : {
      duration: 400,
      timing: "linear",
      origin: "left center",
      keep: 100,
      keyFrames : {
        0: {
          scale : [ "1", "1" ],
          opacity: 1
        },
        100: {
          scale : [ "0", "1" ],
          opacity : 0
        }
      }
    },

    /** Target grows its width. */
    GROW_WIDTH : {
      duration: 400,
      timing: "linear",
      origin: "left center",
      keep: 100,
      keyFrames : {
        0: {
          scale : [ "0", "1" ],
          opacity: 0
        },
        100: {
          scale : [ "1", "1" ],
          opacity : 1
        }
      }
    },

    /** Target shrinks in both width and height. */
    SHRINK : {
      duration: 400,
      timing: "linear",
      origin: "left top",
      keep: 100,
      keyFrames : {
        0: {
          scale : [ "1", "1" ],
          opacity: 1
        },
        100: {
          scale : [ "0", "0" ],
          opacity : 0
        }
      }
    },

    /** Target grows in both width and height. */
    GROW : {
      duration: 400,
      timing: "linear",
      origin: "left top",
      keep: 100,
      keyFrames : {
        0: {
          scale : [ "0", "0" ],
          opacity: 0
        },
        100: {
          scale : [ "1", "1" ],
          opacity : 1
        }
      }
    },

    /** Target slides in to top. */
    SLIDE_UP_IN : {
      duration: 350,
      timing: "linear",
      origin: "center",
      keyFrames : {
        0: {
          translate : ["0px","100%"]
        },
        100: {
          translate : ["0px","0px"]
        }
      }
    },
    /** Target slides out to top.*/
    SLIDE_UP_OUT : {
      duration: 350,
      timing: "linear",
      origin: "center",
      keyFrames : {
        0: {
          translate : ["0px","0px"]
        },
        100: {
          translate : ["0px","0px"]
        }
      }
    },
    /** Target slides out to bottom.*/
    SLIDE_DOWN_IN : {
      duration: 350,
      timing: "linear",
      origin: "center",
      keyFrames : {
        0: {
          translate : ["0px","0px"]
        },
        100: {
          translate : ["0px","0px"]
        }
      }
    },
    /** Target slides down to bottom.*/
    SLIDE_DOWN_OUT : {
      duration: 350,
      timing: "linear",
      origin: "center",
      keyFrames : {
        0: {
          translate : ["0px","0px"]
        },
        100: {
          translate : ["0px","100%"]
        }
      }
    },
    /** Target flips (turns) left from back side to front side. */
    FLIP_LEFT_IN : {
      duration: 350,
      timing: "linear",
      origin: "center",
      keyFrames : {
        0: {
          opacity : 0
        },
        49: {
          opacity : 0
        },
        50: {
          rotate : ["0deg","90deg"],
          scale : [".8","1"],
          opacity : 1
        },
        100: {
          rotate : ["0deg","0deg"],
          scale : ["1","1"],
          opacity : 1
        }
      }
    },
    /** Target flips (turns) left from front side to back side. */
    FLIP_LEFT_OUT : {
      duration: 350,
      timing: "linear",
      origin: "center center",
      keyFrames : {
        0: {
          rotate : ["0deg","0deg"],
          scale : ["1","1"]
        },
        100: {
          rotate : ["0deg","-180deg"],
          scale : [".8","1"]
        }
      }
    },
    /** Target flips (turns) right from back side to front side. */
    FLIP_RIGHT_IN : {
      duration: 350,
      timing: "linear",
      origin: "center center",
      keyFrames : {
        0: {
          opacity : 0
        },
        49: {
          opacity : 0
        },
        50: {
          rotate : ["0deg","-90deg"],
          scale : [".8","1"],
          opacity : 1
        },
        100: {
          rotate : ["0deg","0deg"],
          scale : ["1","1"],
          opacity : 1
        }
      }
    },
    /** Target flips (turns) right from front side to back side. */
    FLIP_RIGHT_OUT : {
      duration: 350,
      timing: "linear",
      origin: "center center",
      keyFrames : {
        0: {
          rotate : ["0deg","0deg"],
          scale : ["1","1"]
        },
        100: {
          rotate : ["0deg","180deg"],
          scale : [".8","1"]
        }
      }
    },
    /** Target moves in to left. */
    SWAP_LEFT_IN : {
      duration: 700,
      timing: "ease-out",
      origin: "center center",
      keyFrames : {
        0: {
          rotate : ["0deg","-70deg"],
          translate : ["0px","0px","-800px"],
          opacity : "0"
        },
        35: {
          rotate : ["0deg","-20deg"],
          translate : ["-180px","0px","-400px"],
          opacity : "1"
        },
        100: {
          rotate : ["0deg","0deg"],
          translate : ["0px","0px","0px"],
          opacity : "1"
        }
      }
    },
    /** Target moves out to left.  */
    SWAP_LEFT_OUT : {
      duration: 700,
      timing: "ease-out",
      origin: "center center",
      keyFrames : {
        0: {
          rotate : ["0deg","0deg"],
          translate : ["0px","0px","0px"],
          opacity : "1"
        },
        35: {
          rotate : ["0deg","20deg"],
          translate : ["-180px","0px","-400px"],
          opacity : ".5"
        },
        100: {
          rotate : ["0deg","70deg"],
          translate : ["0px","0px","-800px"],
          opacity : "0"
        }
      }
    },
    /** Target moves in to right. */
    SWAP_RIGHT_IN : {
      duration: 700,
      timing: "ease-out",
      origin: "center center",
      keyFrames : {
        0: {
          rotate : ["0deg","70deg"],
          translate : ["0px","0px","-800px"],
          opacity : "0"
        },
        35: {
          rotate : ["0deg","20deg"],
          translate : ["-180px","0px","-400px"],
          opacity : "1"
        },
        100: {
          rotate : ["0deg","0deg"],
          translate : ["0px","0px","0px"],
          opacity : "1"
        }
      }
    },
    /** Target moves out to right. */
    SWAP_RIGHT_OUT : {
      duration: 700,
      timing: "ease-out",
      origin: "center center",
      keyFrames : {
        0: {
          rotate : ["0deg","0deg"],
          translate : ["0px","0px","0px"],
          opacity : "1"
        },
        35: {
          rotate : ["0deg","-20deg"],
          translate : ["180px","0px","-400px"],
          opacity : ".5"
        },
        100: {
          rotate : ["0deg","-70deg"],
          translate : ["0px","0px","-800px"],
          opacity : "0"
        }
      }
    },
    /** Target moves in with cube animation from right to left.  */
    CUBE_LEFT_IN : {
      duration: 550,
      timing: "linear",
      origin: "100% 50%",
      keyFrames : {
        0: {
          rotate : ["0deg","90deg"],
          scale: ".5",
          translate: ["0","0","0px"],
          opacity : [".5"]
        },
        100: {
          rotate : ["0deg","0deg"],
          scale: "1",
          translate: ["0","0","0"],
          opacity : ["1"]
        }
      }
    },
    /** Target moves out with cube animation from right to left.  */
    CUBE_LEFT_OUT : {
      duration: 550,
      timing: "linear",
      origin: "0% 50%",
      keyFrames : {
        0: {
          rotate : ["0deg","0deg"],
          scale: "1",
          translate: ["0","0","0px"],
          opacity : ["1"]
        },
        100: {
          rotate : ["0deg","-90deg"],
          scale: ".5",
          translate: ["0","0","0"],
          opacity : [".5"]
        }
      }
    },
    /** Target moves in with cube animation from left to right.  */
    CUBE_RIGHT_IN : {
      duration: 550,
      timing: "linear",
      origin: "0% 50%",
      keyFrames : {
        0: {
          rotate : ["0deg","-90deg"],
          scale: ".5",
          translate: ["0","0","0px"],
          opacity : [".5"]
        },
        100: {
          rotate : ["0deg","0deg"],
          scale: "1",
          translate: ["0","0","0"],
          opacity : ["1"]
        }
      }
    },
    /** Target moves out with cube animation from left to right.  */
    CUBE_RIGHT_OUT : {
      duration: 550,
      timing: "linear",
      origin: "100% 50%",
      keyFrames : {
        0: {
          rotate : ["0deg","0deg"],
          scale: "1",
          translate: ["0","0","0px"],
          opacity : ["1"]
        },
        100: {
          rotate : ["0deg","90deg"],
          scale: ".5",
          translate: ["0","0","0"],
          opacity : [".5"]
        }
      }
    }
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Tino Butz (tbtz)

************************************************************************ */

/**
 * A navigation bar widget.
 *
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   var bar = new qx.ui.mobile.navigationbar.NavigationBar();
 *   var backButton = new qx.ui.mobile.navigationbar.BackButton();
 *   bar.add(backButton);
 *   var title = new qx.ui.mobile.navigationbar.Title();
 *   var.add(title, {flex:1});
 *
 *   this.getRoot.add(bar);
 * </pre>
 *
 * This example creates a navigation bar and adds a back button and a title to it.
 */
qx.Class.define("qx.ui.mobile.navigationbar.NavigationBar",
{
  extend : qx.ui.mobile.container.Composite,


 /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(layout)
  {
    this.base(arguments, layout);
    if (!layout) {
      this.setLayout(new qx.ui.mobile.layout.HBox().set({
        alignY : "middle"
      }));
    }

    this.addListener("touchstart", qx.bom.Event.preventDefault, this);
  },




 /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    // overridden
    defaultCssClass :
    {
      refine : true,
      init : "navigationbar"
    }
  },


  destruct : function()
  {
    this.removeListener("touchstart", qx.bom.Event.preventDefault, this);
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2012 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Martin Wittemann (wittemann)
     * Tino Butz (tbtz)

************************************************************************ */

/**
*
 * Basic application routing manager.
 *
 * Define routes to react on certain GET / POST / DELETE / PUT operations.
 *
 * * GET is triggered when the hash value of the url is changed. Can be called
 *   manually by calling the {@link #executeGet} method.
 * * POST / DELETE / PUT has to be triggered manually right now (will be changed later)
 *    by calling the {@link #executePost}, {@link #executeDelete}, {@link #executePut} method.
 *
 * This manager can also be used to provide browser history.
 *
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   var r = new qx.application.Routing();
 *
 *   // show the start page, when no hash is given or the hash is "#/"
 *   r.onGet("/", function(data) {
 *     startPage.show();
 *   }, this);
 *
 *   // whenever the url /address is called show the addressbook page.
 *   r.onGet("/address", function(data) {
 *     addressBookPage.show();
 *   }, this);
 *
 *   // address with the parameter "id"
 *   r.onGet("/address/{id}", function(data) {
 *     addressPage.show();
 *     model.loadAddress(data.params.id);
 *   }, this);
 *
 *   // Alternative you can use regExp for a route
 *   r.onGet(/address\/(.*)/, function(data) {
 *     addressPage.show();
 *     model.loadAddress(data.params.0);
 *   }, this);
 *
 *   // make sure that the data is always loaded
 *   r.onGet("/address.*", function(data) {
 *     if (!model.isLoaded()) {
 *       model.loadAddresses();
 *     }
 *   }, this);
 *
 *   // update the address
 *   r.onPost("/address/{id}", function(data) {
 *     model.updateAddress(data.params.id);
 *   }, this);
 *
 *   // delete the address and navigate back
 *   r.onDelete("/address/{id}", function(data) {
 *     model.deleteAddress(data.params.id);
 *     r.executeGet("/address", {reverse:true});
 *   }, this);
 * </pre>
 *
 * This example defines different routes to handle navigation events.
 */
qx.Bootstrap.define("qx.application.Routing", {

  construct : function()
  {
    this.__messaging = new qx.event.Messaging();

    this.__navigationHandler = qx.bom.History.getInstance();
    this.__navigationHandler.addListener("changeState", this.__onChangeHash, this);
  },


  statics : {
    DEFAULT_PATH : "/",

    __back : [],
    __forward : []
  },


  members :
  {
    __navigationHandler : null,
    __messaging : null,

    __currentGetPath : null,


    /**
     * Initialization method used to execute the get route for the currently set history path.
     * If no path is set, either the given argument named <code>defaultRoute</code>
     * or the {@link #DEFAULT_PATH} will be used for initialization.
     *
     * @param defaultRoute {String?} Optional default route for initialization.
     */
    init : function(defaultRoute)
    {
      if (qx.core.Environment.get("qx.debug")) {
        if (defaultRoute != null) {
          qx.core.Assert.assertString(defaultRoute, "Invalid argument 'defaultRoute'");
        }
      }

      var path = this.getState();
      path = this._getPathOrFallback(path, defaultRoute);
      this._executeGet(path, null, true);
    },


    /**
     * Checks if path is valid and registered in channel "get" and then just returns it.
     * If the path is not valid either the <code>defaultPath</code> (if given) or the
     * {@link #DEFAULT_PATH} will be returned.
     *
     * @param path {String} Path which gets checked.
     * @param defaultPath {String?} Optional default path.
     * @return {String} A valid path.
     */
    _getPathOrFallback : function(path, defaultPath) {
      if (path == "" || path == null || !this.__messaging.has("get", path)) {
        path = defaultPath || qx.application.Routing.DEFAULT_PATH;
      }
      return path;
    },


    /**
     * Adds a route handler for the "get" operation. The route gets called
     * when the {@link #executeGet} method found a match.
     *
     * @param route {String|RegExp} The route, used for checking if the executed path matches.
     * @param handler {Function} The handler to call, when the route matches with the executed path.
     * @param scope {Object} The scope of the handler.
     * @return {String} Event listener ID
     */
    onGet : function(route, handler, scope) {
      return this.__messaging.on("get", route, handler, scope);
    },


    /**
     * This is a shorthand for {@link #onGet}.
     *
     * @param route {String|RegExp} The route, used for checking if the executed path matches.
     * @param handler {Function} The handler to call, when the route matches with the executed path.
     * @param scope {Object} The scope of the handler.
     * @return {String} Event listener ID
     */
    on : function(route, handler, scope) {
      return this.onGet(route, handler, scope);
    },


    /**
     * Adds a route handler for the "post" operation. The route gets called
     * when the {@link #executePost} method found a match.
     *
     * @param route {String|RegExp} The route, used for checking if the executed path matches.
     * @param handler {Function} The handler to call, when the route matches with the executed path.
     * @param scope {Object} The scope of the handler.
     * @return {String} Event listener ID
     */
    onPost : function(route, handler, scope) {
      return this.__messaging.on("post", route, handler, scope);
    },


    /**
     * Adds a route handler for the "put" operation. The route gets called
     * when the {@link #executePut} method found a match.
     *
     * @param route {String|RegExp} The route, used for checking if the executed path matches
     * @param handler {Function} The handler to call, when the route matches with the executed path
     * @param scope {Object} The scope of the handler
     * @return {String} Event listener ID
     */
    onPut : function(route, handler, scope) {
      return this.__messaging.on("put", route, handler, scope);
    },


    /**
     * Adds a route handler for the "delete" operation. The route gets called
     * when the {@link #executeDelete} method found a match.
     *
     * @param route {String|RegExp} The route, used for checking if the executed path matches
     * @param handler {Function} The handler to call, when the route matches with the executed path
     * @param scope {Object} The scope of the handler
     * @return {String} Event listener ID
     */
    onDelete : function(route, handler, scope) {
      return this.__messaging.on("delete", route, handler, scope);
    },


    /**
     * Adds a route handler for the "any" operation. The "any" operation is called
     * before all other operations.
     *
     * @param route {String|RegExp} The route, used for checking if the executed path matches
     * @param handler {Function} The handler to call, when the route matches with the executed path
     * @param scope {Object} The scope of the handler
     * @return {String} Event listener ID
     */
    onAny : function(route, handler, scope) {
      return this.__messaging.onAny(route, handler, scope);
    },


    /**
     * Removes a registered route by the given id.
     *
     * @param id {String} The id of the registered route
     */
    remove : function(id) {
      this.__messaging.remove(id);
    },


    /**
     * Hash change event handler.
     *
     * @param evt {qx.event.type.Data} The changeHash event.
     */
    __onChangeHash : function(evt)
    {
      var path = evt.getData();
      path = this._getPathOrFallback(path);

      if (path != this.__currentGetPath) {
        this._executeGet(path, null, true);
      }
    },


    /**
     * Executes the get operation and informs all matching route handler.
     *
     * @param path {String} The path to execute
     * @param customData {var} The given custom data that should be propagated
     * @param fromEvent {var} Determines whether this method was called from history
     *
     */
    _executeGet : function(path, customData, fromEvent)
    {
      this.__currentGetPath = path;

      var history = this.__getFromHistory(path);
      if (history)
      {
        if (!customData)
        {
          customData = history.data.customData || {};
          customData.fromHistory = true;
          customData.action = history.action;
          customData.fromEvent = fromEvent;
        } else {
          this.__replaceCustomData(path, customData);
        }
      } else {
        this.__addToHistory(path, customData);
        qx.application.Routing.__forward = [];
      }

      this.__navigationHandler.setState(path);
      this.__messaging.emit("get", path, null, customData);
    },


    /**
     * Executes the get operation and informs all matching route handler.
     *
     * @param path {String} The path to execute
     * @param customData {var} The given custom data that should be propagated
     */
    executeGet : function(path, customData) {
      this._executeGet(path, customData);
    },


    /**
     * This is a shorthand for {@link #executeGet}.
     *
     * @param path {String} The path to execute
     * @param customData {var} The given custom data that should be propagated
     */
    execute : function(path, customData) {
      this.executeGet(path, customData);
    },


    /**
     * Executes the post operation and informs all matching route handler.
     *
     * @param path {String} The path to execute
     * @param params {Map} The given parameters that should be propagated
     * @param customData {var} The given custom data that should be propagated
     */
    executePost : function(path, params, customData) {
      this.__messaging.emit("post", path, params, customData);
    },


    /**
     * Executes the put operation and informs all matching route handler.
     *
     * @param path {String} The path to execute
     * @param params {Map} The given parameters that should be propagated
     * @param customData {var} The given custom data that should be propagated
     */
    executePut : function(path, params, customData) {
      this.__messaging.emit("put", path, params, customData);
    },


    /**
     * Executes the delete operation and informs all matching route handler.
     *
     * @param path {String} The path to execute
     * @param params {Map} The given parameters that should be propagated
     * @param customData {var} The given custom data that should be propagated
     */
    executeDelete : function(path, params, customData) {
      this.__messaging.emit("delete", path, params, customData);
    },


    /**
     * Returns state value (history hash) of the navigation handler.
     * @return {String} State of history navigation handler
     */
    getState : function() {
      return this.__navigationHandler.getState();
    },


    /**
     * Adds the custom data of a given path to the history.
     *
     * @param path {String} The path to store.
     * @param customData {var} The custom data to store
     */
    __addToHistory : function(path, customData)
    {
      qx.application.Routing.__back.unshift({
        path : path,
        customData : customData
      });
    },


    /**
     * Replaces the customData in the history objects with the recent custom data.
     * @param path {String} The path to replace.
     * @param customData {var} The custom data to store.
     */
    __replaceCustomData : function(path, customData) {
      var register = [qx.application.Routing.__back, qx.application.Routing.__forward];
      for (var i=0; i < register.length; i++) {
        for (var j=0; j < register[i].length; j++) {
          if (register[i][j].path == path) {
            register[i][j].customData = customData;
          }
        }
      }
    },


    /**
     * Returns a history entry for a certain path.
     *
     * @param path {String} The path of the entry
     * @return {Map|null} The retrieved entry. <code>null</code> when no entry was found.
     */
    __getFromHistory : function(path)
    {
      var back = qx.application.Routing.__back;
      var forward = qx.application.Routing.__forward;
      var found = false;

      var entry = null;
      var length = back.length;
      for (var i = 0; i < length; i++)
      {
        if (back[i].path == path)
        {
          entry = back[i];
          var toForward = back.splice(0,i);
          for (var a=0; a<toForward.length; a++){
            forward.unshift(toForward[a]);
          }
          found = true;
          break;
        }
      }
      if (found){
        return {
          data : entry,
          action : "back"
        }
      }

      var length = forward.length;
      for (var i = 0; i < length; i++)
      {
        if (forward[i].path == path)
        {
          entry = forward[i];
          var toBack = forward.splice(0,i+1);
          for (var a=0; a<toBack.length; a++){
            back.unshift(toBack[a]);
          }
          break;
        }
      }

      if (entry){
        return {
          data : entry,
          action : "forward"
        }
      }
      return entry;
    },


    /**
     * Decouples the Routing from the navigation handler.
     */
    dispose : function() {
      this.__navigationHandler.removeListener("changeState", this.__onChangeHash, this);
    }
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Tino Butz (tbtz)
     * Martin Wittemann (wittemann)

************************************************************************ */

/**
 * Define messages to react on certain channels.
 *
 * The channel names will be used in the {@link #on} method to define handlers which will
 * be called on certain channels and routes. The {@link #emit} method can be used
 * to execute a given route on a channel. {@link #onAny} defines a handler on any channel.
 *
 * *Example*
 *
 * Here is a little example of how to use the messaging.
 *
 * <pre class='javascript'>
 *   var m = new qx.event.Messaging();
 *
 *   m.on("get", "/address/{id}", function(data) {
 *     var id = data.params.id; // 1234
 *     // do something with the id...
 *   },this);
 *
 *   m.emit("get", "/address/1234");
 * </pre>
 */
qx.Bootstrap.define("qx.event.Messaging",
{
  construct : function()
  {
    this._listener = {},
    this.__listenerIdCount = 0;
    this.__channelToIdMapping = {};
  },


  members :
  {
    _listener : null,
    __listenerIdCount : null,
    __channelToIdMapping : null,


    /**
     * Adds a route handler for the given channel. The route is called
     * if the {@link #emit} method finds a match.
     *
     * @param channel {String} The channel of the message.
     * @param type {String|RegExp} The type, used for checking if the executed path matches.
     * @param handler {Function} The handler to call if the route matches the executed path.
     * @param scope {var ? null} The scope of the handler.
     * @return {String} The id of the route used to remove the route.
     */
    on : function(channel, type, handler, scope) {
      return this._addListener(channel, type, handler, scope);
    },



    /**
     * Adds a handler for the "any" channel. The "any" channel is called
     * before all other channels.
     *
     * @param type {String|RegExp} The route, used for checking if the executed path matches
     * @param handler {Function} The handler to call if the route matches the executed path
     * @param scope {var ? null} The scope of the handler.
     * @return {String} The id of the route used to remove the route.
     */
    onAny : function(type, handler, scope) {
      return this._addListener("any", type, handler, scope);
    },


    /**
     * Adds a listener for a certain channel.
     *
     * @param channel {String} The channel the route should be registered for
     * @param type {String|RegExp} The type, used for checking if the executed path matches
     * @param handler {Function} The handler to call if the route matches the executed path
     * @param scope {var ? null} The scope of the handler.
     * @return {String} The id of the route used to remove the route.
     */
    _addListener : function(channel, type, handler, scope) {
      var listeners = this._listener[channel] = this._listener[channel] || {};
      var id = this.__listenerIdCount++;
      var params = [];
      var param = null;

      // Convert the route to a regular expression.
      if (qx.lang.Type.isString(type))
      {
        var paramsRegexp = /\{([\w\d]+)\}/g;

        while ((param = paramsRegexp.exec(type)) !== null) {
          params.push(param[1]);
        }
        type = new RegExp("^" + type.replace(paramsRegexp, "([^\/]+)") + "$");
      }

      listeners[id] = {regExp:type, params:params, handler:handler, scope:scope};
      this.__channelToIdMapping[id] = channel;
      return id;
    },


    /**
     * Removes a registered listener by the given id.
     *
     * @param id {String} The id of the registered listener.
     */
    remove : function(id) {
      var channel = this.__channelToIdMapping[id];
      var listener = this._listener[channel];
      delete listener[id];
      delete this.__channelToIdMapping[id];
    },


    /**
     * Checks if a listener is registered for the given path in the given channel.
     *
     * @param channel {String} The channel of the message.
     * @param path {String} The path to check.
     * @return {Boolean} Whether a listener is registered.
     */
    has : function(channel, path) {
      var listeners = this._listener[channel];
      if (!listeners || qx.lang.Object.isEmpty(listeners)) {
        return false;
      }

      for (var id in listeners)
      {
        var listener = listeners[id];
        if (listener.regExp.test(path)) {
          return true;
        }
      }

      return false;
    },

    /**
     * Sends a message on the given channel and informs all matching route handlers.
     *
     * @param channel {String} The channel of the message.
     * @param path {String} The path to execute
     * @param params {Map} The given parameters that should be propagated
     * @param customData {var} The given custom data that should be propagated
     */
    emit : function(channel, path, params, customData) {
      this._emit(channel, path, params, customData);
    },


    /**
     * Executes a certain channel with a given path. Informs all
     * route handlers that match with the path.
     *
     * @param channel {String} The channel to execute.
     * @param path {String} The path to check
     * @param params {Map} The given parameters that should be propagated
     * @param customData {var} The given custom data that should be propagated
     */
    _emit : function(channel, path, params, customData)
    {
      var listenerMatchedAny = false;
      var listener = this._listener["any"];
      listenerMatchedAny = this._emitListeners(channel, path, listener, params, customData);

      var listenerMatched = false;
      listener = this._listener[channel];
      listenerMatched = this._emitListeners(channel, path, listener, params, customData);

      if (!listenerMatched && !listenerMatchedAny) {
        qx.Bootstrap.info("No listener found for " + path);
      }
    },


    /**
     * Executes all given listener for a certain channel. Checks all listeners if they match
     * with the given path and executes the stored handler of the matching route.
     *
     * @param channel {String} The channel to execute.
     * @param path {String} The path to check
     * @param listeners {Map[]} All routes to test and execute.
     * @param params {Map} The given parameters that should be propagated
     * @param customData {var} The given custom data that should be propagated
     *
     * @return {Boolean} Whether the route has been executed
     */
    _emitListeners : function(channel, path, listeners, params, customData)
    {
      if (!listeners || qx.lang.Object.isEmpty(listeners)) {
        return false;
      }
      var listenerMatched = false;
      for (var id in listeners)
      {
        var listener = listeners[id];
        listenerMatched |= this._emitRoute(channel, path, listener, params, customData);
      }
      return listenerMatched;
    },


    /**
     * Executes a certain listener. Checks if the listener matches the given path and
     * executes the stored handler of the route.
     *
     * @param channel {String} The channel to execute.
     * @param path {String} The path to check
     * @param listener {Map} The route data.
     * @param params {Map} The given parameters that should be propagated
     * @param customData {var} The given custom data that should be propagated
     *
     * @return {Boolean} Whether the route has been executed
     */
    _emitRoute : function(channel, path, listener, params, customData)
    {
      var match = listener.regExp.exec(path);
      if (match)
      {
        var params = params || {};
        var param = null;
        var value = null;
        match.shift(); // first match is the whole path
        for (var i=0; i < match.length; i++)
        {
          value = match[i];
          param = listener.params[i];
          if (param) {
            params[param] = value;
          } else {
            params[i] = value;
          }
        }
        listener.handler.call(listener.scope, {path:path, params:params, customData:customData});
      }

      return match != undefined;
    }
  }
});
