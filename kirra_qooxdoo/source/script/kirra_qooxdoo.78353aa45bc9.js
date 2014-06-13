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
 * The purpose of this class is to contain all checks for PhoneGap/Cordova.
 *
 * This class is used by {@link qx.core.Environment} and should not be used
 * directly. Please check its class comment for details how to use it.
 *
 * @internal
 */
qx.Bootstrap.define("qx.bom.client.PhoneGap",
{
  statics :
  {
    /**
     * Checks if PhoneGap/Cordova is available.
     * @return {Boolean} <code>true</code>, if it could be used.
     * @internal
     */
    getPhoneGap : function() {
      return ("cordova" in window || "Cordova" in window || "PhoneGap" in window);
    },


    /**
     * Checks if notifications can be displayed.
     * @return {Boolean} <code>true</code>, if it could be used.
     * @internal
     */
    getNotification : function() {
      return "notification" in navigator;
    }
  },

  defer : function(statics) {
    qx.core.Environment.add("phonegap", statics.getPhoneGap);
    qx.core.Environment.add("phonegap.notification", statics.getNotification);
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
     * Tino Butz (tbtz)
     * Christopher Zuendorf (czuendorf)

************************************************************************ */

/**
 * This mixin resizes the container element to the height of the parent element.
 * Use this when the height can not be set by CSS.
 *
 */
qx.Mixin.define("qx.ui.mobile.core.MResize",
{
  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** Whether the resize should fire the "domupdated" event. Set this to "true"
     *  whenever other elements should react on this size change (e.g. when the size
     *  change does not infect the size of the application, but other widgets should
     *  react).
     */
    fireDomUpdatedOnResize : {
      check : "Boolean",
      init : false
    }
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    __lastHeight : null,
    __lastWidth : null,


    /**
     * Removes fixed size from container.
     */
    releaseFixedSize : function() {
      var parent = this.getLayoutParent();

      if (parent && parent.getContainerElement()) {
        var element = this.getContainerElement();
        qx.bom.element.Style.set(element, "height", "auto");
        qx.bom.element.Style.set(element, "width", "auto");
      }
    },


    /**
     * Resizes the container element to the height of the parent element.
     */
    fixSize : function()
    {
      var parent = this.getLayoutParent();

      if (parent && parent.getContainerElement()) {
        var height = parent.getContainerElement().offsetHeight;
        var width = parent.getContainerElement().offsetWidth;

        // Only fix size, when value are above zero.
        if(height === 0 || width === 0) {
          return;
        }

        if (!this.getFireDomUpdatedOnResize()) {
          this._setHeight(height);
          this._setWidth(width);
        } else if (this.__lastHeight != height && this.__lastWidth != width) {
          this._setHeight(height);
          this._setWidth(width);
          this.__lastWidth = width;
          this.__lastHeight = height;
          this._domUpdated();
        }
      }
    },


    /**
     * Sets the height of the container element.
     *
     * @param height {Integer} The height to set
     */
    _setHeight : function(height)
    {
      var element = this.getContainerElement();
      if (qx.core.Environment.get("qx.mobile.nativescroll"))
      {
        qx.bom.element.Style.set(element, "minHeight", height + "px");
      } else {
        qx.bom.element.Style.set(element, "height", height + "px");
      }
    },



    /**
     * Sets the width of the container element.
     *
     * @param width {Integer} The width to set
     */
    _setWidth : function(width)
    {
      var element = this.getContainerElement();
      if (qx.core.Environment.get("qx.mobile.nativescroll"))
      {
        qx.bom.element.Style.set(element, "minWidth", width + "px");
      } else {
        qx.bom.element.Style.set(element, "width", width + "px");
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
 * A page is a widget which provides a screen with which users
 * can interact in order to do something. Most times a page provides a single task
 * or a group of related tasks.
 *
 * A qooxdoo mobile application is usually composed of one or more loosely bound
 * pages. Typically there is one page that presents the "main" view.
 *
 * Pages can have one or more child widgets from the {@link qx.ui.mobile}
 * namespace. Normally a page provides a {@link qx.ui.mobile.navigationbar.NavigationBar}
 * for the navigation between pages.
 *
 * To navigate between two pages, just call the {@link #show} method of the page
 * that should be shown. Depending on the used page manager a page transition will be animated.
 * There are several animations available. Have
 * a look at the {@link qx.ui.mobile.page.Manager} manager or {@link qx.ui.mobile.layout.Card} card layout for more information.
 *
 * A page has predefined lifecycle methods that get called by the used page manager
 * when a page gets shown. Each time another page is requested to be shown the currently shown page
 * is stopped. The other page, will be, if shown for the first time, initialized and started
 * afterwards. For all called lifecycle methods an event is fired.
 *
 * Call of the {@link #show} method triggers the following lifecycle methods:
 *
 * * <strong>initialize</strong>: Initializes the page to show
 * * <strong>start</strong>: Gets called when the page to show is started
 * * <strong>stop</strong>:  Stops the current page
 *
 * IMPORTANT: Define all child widgets of a page when the {@link #initialize} lifecycle
 * method is called, either by listening to the {@link #initialize} event or overriding
 * the {@link #_initialize} method. This is because a page can be instanced during
 * application startup and would then decrease performance when the widgets would be
 * added during constructor call. The <code>initialize</code> event and the
 * {@link #_initialize} lifecycle method are only called when the page is shown
 * for the first time.
 *
 */
qx.Class.define("qx.ui.mobile.page.Page",
{
  extend : qx.ui.mobile.container.Composite,
  include : qx.ui.mobile.core.MResize,

 /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(layout)
  {
    this.base(arguments, layout || new qx.ui.mobile.layout.VBox());
  },



 /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics : {
    _currentPage : null,

    /**
     * Event handler. Called when the device is ready.
     */
    _onDeviceReady : function() {
      qx.bom.Event.addNativeListener(document, "backbutton", qx.ui.mobile.page.Page._onBackButton);
      qx.bom.Event.addNativeListener(document, "menubutton", qx.ui.mobile.page.Page._onMenuButton);
    },


    /**
     * Event handler. Called when the back button of the device was pressed.
     */
    _onBackButton : function()
    {
      if (qx.core.Environment.get("phonegap") && qx.core.Environment.get("os.name") == "android")
      {
        var exit = true;
        if (qx.ui.mobile.page.Page._currentPage) {
          exit = qx.ui.mobile.page.Page._currentPage.back(true);
        }
        if (exit) {
          navigator.app.exitApp();
        }
      }
    },


    /**
     * Event handler. Called when the menu button of the device was pressed.
     */
    _onMenuButton : function()
    {
      if (qx.core.Environment.get("phonegap") && qx.core.Environment.get("os.name") == "android")
      {
        if (qx.ui.mobile.page.Page._currentPage) {
          qx.ui.mobile.page.Page._currentPage.menu();
        }
      }
    }
  },


 /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /** Fired when the lifecycle method {@link #initialize} is called */
    "initialize" : "qx.event.type.Event",

    /** Fired when the lifecycle method {@link #start} is called */
    "start" : "qx.event.type.Event",

    /** Fired when the lifecycle method {@link #stop} is called */
    "stop" : "qx.event.type.Event",

    /** Fired when the lifecycle method {@link #pause} is called */
    "pause" : "qx.event.type.Event",

    /** Fired when the lifecycle method {@link #resume} is called */
    "resume" : "qx.event.type.Event",

    /** Fired when the method {@link #back} is called. Data indicating
     *  whether the action was triggered by a key event or not.
     */
    "back" : "qx.event.type.Data",

    /** Fired when the method {@link #menu} is called */
    "menu" : "qx.event.type.Event",

    /** Fired when the method {@link #wait} is called */
    "wait" : "qx.event.type.Event"
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
      init : "page"
    }
  },


 /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    __initialized : false,

    // overridden
    show : function(properties)
    {
      qx.ui.mobile.page.Page._currentPage = this;
      this.initialize();
      this.start();
      this.base(arguments, properties);
    },


    // overridden
    exclude : function(properties)
    {
      this.stop();
      this.base(arguments, properties);
    },


    /**
     * Fires the <code>back</code> event. Call this method if you want to request
     * a back action. For Android PhoneGap applications this method gets called
     * by the used page manager when the back button was pressed. Return <code>true</code>
     * to exit the application.
     *
     * @param triggeredByKeyEvent {Boolean} Whether the back action was triggered by a key event.
     * @return {Boolean} Whether the exit should be exit or not. Return <code>true</code
     *     to exit the application. Only needed for Android PhoneGap applications.
     */
    back : function(triggeredByKeyEvent)
    {
      this.fireDataEvent("back", triggeredByKeyEvent);
      var value = this._back(triggeredByKeyEvent);
      return value || false;
    },


    /**
     * Override this method if you want to perform a certain action when back
     * is called.
     *
     * @param triggeredByKeyEvent {Boolean} Whether the back action was triggered by a key event.
     * @return {Boolean} Whether the exit should be exit or not. Return <code>true</code
     *     to exit the application. Only needed for Android PhoneGap applications.
     * @see #back
     * @abstract
     */
    _back : function(triggeredByKeyEvent)
    {

    },


    /**
     * Only used by Android PhoneGap applications. Called by the used page manager
     * when the menu button was pressed. Fires the <code>menu</code> event.
     */
    menu : function() {
      this.fireEvent("menu");
    },


    /*
    ---------------------------------------------------------------------------
      Lifecycle Methods
    ---------------------------------------------------------------------------
    */

    /**
     * Lifecycle method. Called by the page manager when the page is shown.
     * Fires the <code>initialize</code> event. You should create and add all your
     * child widgets of the view,  either by listening to the {@link #initialize} event or overriding
     * the {@link #_initialize} method. This is because a page can be instanced during
     * application startup and would then decrease performance when the widgets would be
     * added during constructor call. The {@link #_initialize} lifecycle method and the
     * <code>initialize</code> are only called once when the page is shown for the first time.
     */
    initialize : function()
    {
      if (!this.isInitialized())
      {
        this._initialize();
        this.__initialized = true;
        this.fireEvent("initialize");
      }
    },


    /**
     * Override this method if you would like to perform a certain action when initialize
     * is called.
     *
     * @see #initialize
     */
    _initialize : function()
    {

    },


    /**
     * Returns the status of the initialization of the page.
     *
     * @return {Boolean} Whether the page is already initialized or not
     */
    isInitialized : function()
    {
      return this.__initialized;
    },


    /**
     * Lifecycle method. Called by the page manager after the {@link #initialize}
     * method when the page is shown. Fires the <code>start</code> event. You should
     * register all your event listener when this event occurs, so that no page
     * updates are down when page is not shown.
     */
    start : function() {
      this._start();
      this.fireEvent("start");
    },


    /**
     * Override this method if you would like to perform a certain action when start
     * is called.
     *
     * @see #start
     */
    _start : function()
    {

    },


    /**
     * Lifecycle method. Called by the page manager when another page is shown.
     * Fires the <code>stop</code> event. You should unregister all your event
     * listener when this event occurs, so that no page updates are down when page is not shown.
     */
    stop : function()
    {
      this._stop();
      this.fireEvent("stop");
    },


    /**
     * Override this method if you would like to perform a certain action when stop
     * is called.
     *
     * @see #stop
     */
    _stop : function()
    {

    },


    /**
     * Lifecycle method. Not used right now. Should be called when the current page
     * is interrupted, e.g. by a dialog, so that page view updates can be interrupted.
     * Fires the <code>pause</code> event.
     */
    pause : function() {
      this._pause();
      this.fireEvent("pause");
    },


    /**
     * Override this method if you would like to perform a certain action when pause
     * is called.
     *
     * @see #pause
     */
    _pause : function()
    {

    },


    /**
     * Lifecycle method. Not used right now. Should be called when the current page
     * is resuming from a interruption, e.g. when a dialog is closed, so that page
     * can resume updating the view.
     * Fires the <code>resume</code> event.
     */
    resume : function() {
      this._resume();
      this.fireEvent("resume");
    },


    /**
     * Override this method if you would like to perform a certain action when resume
     * is called.
     *
     * @see #resume
     */
    _resume : function()
    {

    },


    /**
     * Lifecycle method. Not used right now. Should be called when the current page
     * waits for data request etc.
     * Fires the <code>wait</code> event.
     */
    wait : function() {
      this._wait();
      this.fireEvent("wait");
    },


    /**
     * Override this method if you would like to perform a certain action when wait
     * is called.
     *
     * @see #wait
     */
    _wait : function()
    {

    }
  },



 /*
  *****************************************************************************
      DEFER
  *****************************************************************************
  */

  defer : function(statics) {
    if (qx.core.Environment.get("phonegap") && qx.core.Environment.get("os.name") == "android")
    {
      qx.bom.Event.addNativeListener(document, "deviceready", statics._onDeviceReady);
    }
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
     * Tino Butz (tbtz)

************************************************************************ */

/**
 * All widgets that are added to the navigation container should implement this interface.
 */
qx.Interface.define("qx.ui.mobile.container.INavigation",
{
  members :
  {
    /**
     * Returns the title widget that is merged into the navigation bar.
     *
     * @return {qx.ui.mobile.navigationbar.Title} The title of the navigation bar
     */
    getTitleWidget : function() {},


    /**
     * Returns the left container that is merged into the navigation bar.
     *
     * @return {qx.ui.mobile.container.Composite} The left container of the navigation bar
     */
    getLeftContainer : function() {},


    /**
     * Returns the right container that is merged into the navigation bar.
     *
     * @return {qx.ui.mobile.container.Composite} The right container of the navigation bar
     */
    getRightContainer : function() {}
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
 * Specialized page. This page includes already a {@link qx.ui.mobile.navigationbar.NavigationBar}
 * and and a {@link qx.ui.mobile.container.Scroll} container.
 * The NavigationPage can only be used with a page manager {@link qx.ui.mobile.page.Manager}.

 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *
 *  var manager = new qx.ui.mobile.page.Manager();
 *  var page = new qx.ui.mobile.page.NavigationPage();
 *  page.setTitle("Page Title");
 *  page.setShowBackButton(true);
 *  page.setBackButtonText("Back")
 *  page.addListener("initialize", function()
 *  {
 *    var button = new qx.ui.mobile.form.Button("Next Page");
 *    page.getContent().add(button);
 *  },this);
 *
 *  page.addListener("back", function()
 *  {
 *    otherPage.show({animation:"cube", reverse:true});
 *  },this);
 *
 *  manager.addDetail(page);
 *  page.show();
 * </pre>
 *
 * This example creates a NavigationPage with a title and a back button. In the
 * <code>initialize</code> lifecycle method a button is added.
 */
qx.Class.define("qx.ui.mobile.page.NavigationPage",
{
  extend : qx.ui.mobile.page.Page,
  implement : qx.ui.mobile.container.INavigation,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param wrapContentByGroup {Boolean} Defines whether a group box should wrap the content. This can be used for defining a page margin.
   * @param layout {qx.ui.mobile.layout.Abstract} The layout of this page.
   */
  construct : function(wrapContentByGroup, layout)
  {
    this.base(arguments);

    if(wrapContentByGroup != null) {
      this._wrapContentByGroup = wrapContentByGroup;
    }
  },

  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /** Fired when the user tapped on the navigation button */
    action : "qx.event.type.Event"
  },


  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** The title of the page */
    title :
    {
      check : "String",
      init : "",
      event : "changeTitle",
      apply : "_applyTitle"
    },


    /** The back button text */
    backButtonText :
    {
      check : "String",
      init : "",
      apply : "_applyBackButtonText"
    },


    /** The action button text */
    buttonText :
    {
      check : "String",
      init : "",
      apply : "_applyActionButtonText"
    },


    /** The action button icon */
    buttonIcon :
    {
      check : "String",
      init : null,
      nullable : true,
      apply : "_applyActionButtonIcon"
    },


    /**
     * Whether to show the back button.
     */
    showBackButton:
    {
      check : "Boolean",
      init : false,
      apply : "_applyShowBackButton"
    },


    /**
     * Indicates whether the back button should be shown on tablet.
     */
    showBackButtonOnTablet:
    {
      check : "Boolean",
      init : false
    },


    /**
     * Whether to show the action button.
     */
    showButton:
    {
      check : "Boolean",
      init : false,
      apply : "_applyShowButton"
    },


    /**
     * Toggles visibility of NavigationBar in
     * wrapping container {@link qx.ui.mobile.container.Navigation}
     */
    navigationBarHidden:
    {
      check : "Boolean",
      init : false
    },


    /**
     * Sets the transition duration (in seconds) for the effect when hiding/showing
     * the NavigationBar through boolean property navigationBarHidden.
     */
    navigationBarToggleDuration:
    {
      check : "Number",
      init : 0.8
    },


    /**
     * The CSS class to add to the content per default.
     */
    contentCssClass :
    {
      check : "String",
      init : "content",
      nullable : true,
      apply : "_applyContentCssClass"
    }
  },


 /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _isTablet : false,
    _wrapContentByGroup : true,
    __backButton : null,
    __actionButton : null,
    __content : null,
    __scrollContainer : null,
    __title : null,
    __leftContainer : null,
    __rightContainer : null,


    // interface implementation
    getTitleWidget : function() {
      if (!this.__title) {
        this.__title = this._createTitleWidget();
      }
      return this.__title;
    },


    /**
     * Creates the navigation bar title.
     *
     * @return {qx.ui.mobile.navigationbar.Title} The created title widget
     */
    _createTitleWidget : function()
    {
      return new qx.ui.mobile.navigationbar.Title(this.getTitle());
    },


    // property apply
    _applyTitle : function(value, old) {
      if (this.__title) {
        this.__title.setValue(value);
      }
    },


    // interface implementation
    getLeftContainer : function() {
      if (!this.__leftContainer) {
        this.__leftContainer = this._createLeftContainer();
      }
      return this.__leftContainer;
    },


    // interface implementation
    getRightContainer : function() {
      if (!this.__rightContainer) {
        this.__rightContainer = this._createRightContainer();
      }
      return this.__rightContainer;
    },


    /**
     * Creates the left container for the navigation bar.
     *
     * @return {qx.ui.mobile.container.Composite} Creates the left container for the navigation bar.
     */
    _createLeftContainer : function() {
      var layout =new qx.ui.mobile.layout.HBox();
      var container = new qx.ui.mobile.container.Composite(layout);
      container.addCssClass("left-container");
      this.__backButton = this._createBackButton();
      this.__backButton.addListener("tap", this._onBackButtonTap, this);
      this._showBackButton();
      container.add(this.__backButton);
      return container;
    },


    /**
     * Creates the right container for the navigation bar.
     *
     * @return {qx.ui.mobile.container.Composite} Creates the right container for the navigation bar.
     */
    _createRightContainer : function() {
      var layout = new qx.ui.mobile.layout.HBox();
      var container = new qx.ui.mobile.container.Composite(layout);
      container.addCssClass("right-container");
      this.__actionButton = this._createButton();
      this.__actionButton.addListener("tap", this._onButtonTap, this);
      this._showButton();
      container.add(this.__actionButton);
      return container;
    },


    /**
      * Creates the navigation bar back button.
      * Creates the scroll container.
      *
      * @return {qx.ui.mobile.navigationbar.BackButton} The created back button widget
      */
    _createBackButton : function() {
      return new qx.ui.mobile.navigationbar.BackButton(this.getBackButtonText());
    },



    /**
      * Creates the navigation bar button.
      * Creates the content container.
      *
      * @return {qx.ui.mobile.navigationbar.Button} The created button widget
      */
    _createButton : function() {
     return new qx.ui.mobile.navigationbar.Button(this.getButtonText(), this.getButtonIcon());
    },


    /**
    * Scrolls the wrapper contents to the x/y coordinates in a given
    * period.
    *
    * @param x {Integer} X coordinate to scroll to.
    * @param y {Integer} Y coordinate to scroll to.
    * @param time {Integer} Time slice in which scrolling should
    *              be done.
    *
    */
    scrollTo : function(x, y, time)
    {
      this.__scrollContainer.scrollTo(x, y, time);
    },


    /**
    * Scrolls the wrapper contents to the widgets coordinates in a given
    * period.
    *
    * @param widget {qx.ui.mobile.core.Widget} the widget, the scroll container should scroll to.
    * @param time {Integer} Time slice in which scrolling should
    *              be done.
    *
    */
    scrollToWidget : function(widget, time)
    {
      if(widget) {
        this.__scrollContainer.scrollToElement(widget.getId(), time);
      }
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
     * Returns the back button widget.
     *
     * @return {qx.ui.mobile.navigationbar.BackButton} The back button widget
     */
    _getBackButton : function()
    {
      return this.__backButton;
    },


    /**
     * Returns the action button widget.
     *
     * @return {qx.ui.mobile.navigationbar.Button} The action button widget
     */
    _getButton : function()
    {
      return this.__actionButton;
    },


    /**
     * Sets the isTablet flag.
     * @param isTablet {Boolean} value of the isTablet flag.
     */
    setIsTablet : function (isTablet) {
      this._isTablet = isTablet
    },


    /**
     * Returns the isTablet flag.
     * @return {Boolean} the isTablet flag of this page.
     */
    isTablet : function() {
      return this._isTablet;
    },


    /**
     * Returns the scroll container.
     *
     * @return {qx.ui.mobile.container.Scroll} The scroll container
     */
    _getScrollContainer : function()
    {
      return this.__scrollContainer;
    },


    /**
     * Adds a widget, below the NavigationBar.
     *
     * @param widget {qx.ui.mobile.core.Widget} The widget to add, after NavigationBar.
     */
    addAfterNavigationBar : function(widget) {
      if(widget && this.__scrollContainer) {
        this.addBefore(widget, this.__scrollContainer);
      }
    },


    // property apply
    _applyBackButtonText : function(value, old)
    {
      if (this.__backButton) {
        this.__backButton.setValue(value);
      }
    },


    // property apply
    _applyActionButtonText : function(value, old)
    {
      if (this.__actionButton) {
        this.__actionButton.setValue(value);
      }
    },


    // property apply
    _applyActionButtonIcon : function(value, old)
    {
      if (this.__actionButton) {
        this.__actionButton.setIcon(value);
      }
    },


    // property apply
    _applyShowBackButton : function(value, old)
    {
      this._showBackButton();
    },


    // property apply
    _applyShowButton : function(value, old)
    {
      this._showButton();
    },


    // property apply
    _applyContentCssClass : function(value, old)
    {
      if (this.__content) {
        this.__content.setDefaultCssClass(value);
      }
    },


    /**
     * Helper method to show the back button.
     */
    _showBackButton : function()
    {
      if (this.__backButton)
      {
        if (this.getShowBackButton()) {
          this.__backButton.show();
        } else {
          this.__backButton.exclude();
        }
      }
    },


    /**
     * Helper method to show the button.
     */
    _showButton : function()
    {
      if (this.__actionButton)
      {
        if (this.getShowButton()) {
          this.__actionButton.show();
        } else {
          this.__actionButton.exclude();
        }
      }
    },


    // overridden
    _initialize : function()
    {
      this.base(arguments);

      this.__scrollContainer = this._createScrollContainer();
      this.__content = this._createContent();

      if (this.__content) {
        this.__scrollContainer.add(this.__content, {flex :1});
      }
      if (this.__scrollContainer) {
        this.add(this.__scrollContainer, {flex:1});
      }
    },


    /**
     * Creates the scroll container.
     *
     * @return {qx.ui.mobile.container.Scroll} The created scroll container
     */
    _createScrollContainer : function()
    {
      return new qx.ui.mobile.container.Scroll();
    },


    /**
     * Creates the content container.
     *
     * @return {qx.ui.mobile.container.Composite} The created content container
     */
    _createContent : function()
    {
      var content = new qx.ui.mobile.container.Composite();
      content.setDefaultCssClass(this.getContentCssClass());

      if(this._wrapContentByGroup == true) {
        content.addCssClass("group");
      }

      return content;
    },


    /**
     * Event handler. Called when the tap event occurs on the back button.
     *
     * @param evt {qx.event.type.Tap} The tap event
     */
    _onBackButtonTap : function(evt)
    {
      this.back();
    },


    /**
     * Event handler. Called when the tap event occurs on the button.
     *
     * @param evt {qx.event.type.Tap} The tap event
     */
    _onButtonTap : function(evt)
    {
      this.fireEvent("action");
    }
  },


  destruct : function()
  {
    this._disposeObjects("__leftContainer", "__rightContainer", "__backButton",
      "__actionButton", "__title");
    this.__leftContainer = this.__rightContainer = this.__backButton = this.__actionButton = null;
    this.__title = this.__content = this.__scrollContainer = null;
    this._isTablet = null;
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
 * The label widget displays a text or HTML content.
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   var label = new qx.ui.mobile.basic.Label("Hello World");
 *
 *   this.getRoot().add(label);
 * </pre>
 *
 * This example create a widget to display the label.
 *
 */
qx.Class.define("qx.ui.mobile.basic.Label",
{
  extend : qx.ui.mobile.core.Widget,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param value {String?null} Text or HTML content to display
   */
  construct : function(value)
  {
    this.base(arguments);
    if (value) {
      this.setValue(value);
    }
    this.initWrap();

    if (qx.core.Environment.get("qx.dynlocale")) {
      qx.locale.Manager.getInstance().addListener("changeLocale", this._onChangeLocale, this);
    }
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
      init : "label"
    },


    /**
     * Text or HTML content to display
     */
    value :
    {
      nullable : true,
      init : null,
      check : "String",
      apply : "_applyValue",
      event : "changeValue"
    },


    // overridden
    anonymous :
    {
      refine : true,
      init : true
    },


    /**
     * Controls whether text wrap is activated or not.
     */
    wrap :
    {
      check : "Boolean",
      init : true,
      apply : "_applyWrap"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // property apply
    _applyValue : function(value, old)
    {
      this._setHtml(value);
    },


    // property apply
    _applyWrap : function(value, old)
    {
      if (value) {
        this.removeCssClass("no-wrap");
      } else {
        this.addCssClass("no-wrap");
      }
    },

    /**
     * Locale change event handler
     *
     * @signature function(e)
     * @param e {Event} the change event
     */
    _onChangeLocale : qx.core.Environment.select("qx.dynlocale",
    {
      "true" : function(e)
      {
        var content = this.getValue();
        if (content && content.translate) {
          this.setValue(content.translate());
        }
      },

      "false" : null
    })
  },


  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function()
  {
    if (qx.core.Environment.get("qx.dynlocale")) {
      qx.locale.Manager.getInstance().removeListener("changeLocale", this._onChangeLocale, this);
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
 * A navigation bar title widget.
 */
qx.Class.define("qx.ui.mobile.navigationbar.Title",
{
  extend : qx.ui.mobile.basic.Label,

  properties :
  {
    wrap :
    {
      refine : true,
      init : false
    },


    // overridden
    defaultCssClass :
    {
      refine : true,
      init : "title"
    }
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // overridden
    _getTagName : function()
    {
      return "h1";
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
 * A horizontal box layout.
 *
 * The horizontal box layout lays out widgets in a horizontal row, from left
 * to right.
 *
 * *Item Properties*
 *
 * <ul>
 * <li><strong>flex</strong> <em>(Integer)</em>: The flex property determines how the container
 *   distributes remaining empty space among its children. If items are made
 *   flexible, they can grow or shrink accordingly. Their relative flex values
 *   determine how the items are being resized, i.e. the larger the flex ratio
 *   of two items, the larger the resizing of the first item compared to the
 *   second.
 * </li>
 * </ul>
 *
 * *Example*
 *
 * Here is a little example of how to use the HBox layout.
 *
 * <pre class="javascript">
 * var layout = new qx.ui.mobile.layout.HBox().set({alignX:"center"});
 *
 * var container = new qx.ui.mobile.container.Composite(layout);
 *
 * container.add(new qx.ui.mobile.basic.Label("1"));
 * container.add(new qx.ui.mobile.basic.Label("2"), {flex:1});
 * container.add(new qx.ui.mobile.basic.Label("3"));
 * </pre>
 */
qx.Class.define("qx.ui.mobile.layout.HBox",
{
  extend : qx.ui.mobile.layout.AbstractBox,


 /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // overridden
    _getCssClasses : function(){
      return ["hbox"];
    }
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
     * Gabriel Munteanu (gabios)
     * Christopher Zuendorf (czuendorf)

************************************************************************ */

/**
 * A multi-purpose widget, which combines a label with an icon.
 *
 * The intended purpose of qx.ui.mobile.basic.Atom is to easily align the common icon-text
 * combination in different ways.
 *
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   var atom = new qx.ui.mobile.basic.Atom("Icon Right", "icon/32/actions/go-next.png");
 *   this.getRoot().add(atom);
 * </pre>
 *
 * This example creates an atom with the label "Icon Right" and an icon.
 */
qx.Class.define("qx.ui.mobile.basic.Atom",
{
  extend : qx.ui.mobile.core.Widget,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param label {String} Label to use
   * @param icon {String?null} Icon to use
   */
  construct : function(label, icon)
  {
    this.base(arguments);
    this.__createChildren(label, icon);
    this.__updateGap(this.getIconPosition(),4);
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
      init : "atom"
    },

    /** The label/caption/text of the qx.ui.mobile.basic.Atom instance */
    label :
    {
      apply : "_applyLabel",
      nullable : true,
      check : "String",
      event : "changeLabel"
    },

    /** Any URI String supported by qx.ui.mobile.basic.Image to display an icon */
    icon :
    {
      check : "String",
      apply : "_applyIcon",
      nullable : true,
      event : "changeIcon"
    },


    /**
     * The space between the icon and the label
     */
    gap :
    {
      check : "Integer",
      nullable : false,
      apply : "_applyGap",
      init : 4
    },


    /**
     * Configure the visibility of the sub elements/widgets.
     * Possible values: both, text, icon
     */
    show :
    {
      init : "both",
      check : [ "both", "label", "icon" ],
      inheritable : true,
      apply : "_applyShow"
    },


    /**
     * The position of the icon in relation to the text.
     * Only useful/needed if text and icon is configured and 'show' is configured as 'both' (default)
     */
    iconPosition :
    {
      init   : "left",
      check : [ "top", "right", "bottom", "left" ],
      apply : "_applyIconPosition"
    }
  },

  members :
  {
    __label : null,
    __icon : null,
    __childrenContainer : null,


        // property apply
    _applyIconPosition : function(value, old) {
        var targetLayout;
        var verticalLayout = ["top", "bottom"].indexOf(value) != -1;

        if(verticalLayout) {
           targetLayout = new qx.ui.mobile.layout.VBox();
        } else {
           targetLayout = new qx.ui.mobile.layout.HBox();
        }

        var isReverse = ["right", "bottom"].indexOf(value) != -1;
        targetLayout.setReversed(isReverse);

        this.__childrenContainer.setLayout(targetLayout);

        this.__updateGap(old, null);
        this.__updateGap(value,this.getGap());

        this._domUpdated();
    },


    // property apply
    _applyShow : function(value, old)
    {
        if(this.__label) {
            if(value === 'both' || value === 'label') {
                this.__label.show();
            } else if(value === 'icon') {
                this.__label.exclude();
            }
        }
        if(this.__icon) {
            if(value === 'both' || value === 'icon') {
                this.__icon.show();
            } else if(value === 'label') {
                this.__icon.exclude();
            }
        }
    },

    // property apply
    _applyGap : function(value, old)
    {
      this.__updateGap(this.getIconPosition(),value);
    },


    /**
     * Updates the gap between icon and label text.
     * @param iconPosition {String} position of the icon: "left", "bottom", "right", "top".
     * @param value {Integer} size of the gap.
     */
    __updateGap : function (iconPosition, value) {

      if(this.__icon)
      {
        // Then set new margin gap.
        var newMarginPosition = this.__getOpposedPosition(iconPosition);
        var newPropKey = 'margin'+qx.lang.String.firstUp(newMarginPosition);

        if(value) {
          this.__icon._setStyle(newPropKey, value + 'px');
        } else {
          this.__icon._setStyle(newPropKey, null);
        }

      }
    },

    /**
     * Returns the opposed position for a given position.
     * @param position {String} "left", "right", "bottom", "right" position.
     * @return {String} opposed position.
     */
    __getOpposedPosition : function(position)
    {
      var opposedPosition = 'left';
      switch(position)
      {
        case 'top':
          opposedPosition = 'bottom';
          break;
        case 'bottom':
          opposedPosition = 'top';
          break;
        case 'left':
          opposedPosition = 'right';
          break;
      }
      return opposedPosition;
    },


    // property apply
    _applyLabel : function(value, old)
    {
      if(this.__label)
      {
        this.__label.setValue(value);
      }
      else
      {
        this.__label = this._createLabelWidget(value);
      }
    },


    // property apply
    _applyIcon : function(value, old)
    {
      if(this.__icon)
      {
        this.__icon.setSource(value);
      }
      else
      {
        this.__icon = this._createIconWidget(value);
      }
    },


    /**
     * Returns the icon widget.
     *
     * @return {qx.ui.mobile.basic.Image} The icon widget.
     */
    getIconWidget: function() {
      return this.__icon;
    },


    /**
     * Returns the label widget.
     *
     * @return {qx.ui.mobile.basic.Label} The label widget.
     */
    getLabelWidget : function() {
      return this.__label;
    },


    /**
     * Creates the icon widget.
     *
     * @param iconUrl {String} The icon url.
     * @return {qx.ui.mobile.basic.Image} The created icon widget.
     */
    _createIconWidget : function(iconUrl)
    {
      var iconWidget = new qx.ui.mobile.basic.Image(iconUrl);
      iconWidget.setAnonymous(true);

      return iconWidget;
    },


    /**
     * Creates the label widget.
     *
     * @param label {String} The text that should be displayed.
     * @return {qx.ui.mobile.basic.Label} The created label widget.
     */
    _createLabelWidget : function(label)
    {
      var labelWidget = new qx.ui.mobile.basic.Label(label);
      labelWidget.setAnonymous(true);
      labelWidget.setWrap(false);

      return labelWidget;
    },


    /**
     * This function is responsible for creating and adding 2 children controls to the Button widget.
     * A label and an icon.
     * @param label {String} the text of the button
     * @param icon {String} A path to an image resource
     *
     */
    __createChildren : function(label, icon) {

      this.__label = this._createLabelWidget(label);
      if(label)
      {
        this.setLabel(label);
      }

      this.__icon = this._createIconWidget(icon);
      if(icon)
      {
        this.setIcon(icon);
      } else {
        this.__icon.exclude();
      }

      var layout;
      var verticalLayout = [ "top", "bottom" ].indexOf(this.getIconPosition()) != -1;
      // If Atom has no Label, only Icon is shown, and should vertically centered.
      var hasNoLabel = !this.__label;

      if(verticalLayout || hasNoLabel){
        layout = new qx.ui.mobile.layout.VBox();
      } else {
        layout = new qx.ui.mobile.layout.HBox();
      }

      this.__childrenContainer = new qx.ui.mobile.container.Composite(layout);
      this.__childrenContainer.addCssClass("box-centered");
      this.__childrenContainer.setAnonymous(true);

      if(this.__icon) {
        this.__childrenContainer.add(this.__icon, {flex : 0});
      }

      if(this.__label) {
        this.__label.addCssClass("box-centered");
        this.__childrenContainer.add(this.__label, {flex : 0});
      }

      // Show/Hide Label/Icon
      if(this.getShow() === 'icon' && this.__label) {
        this.__label.exclude();
      }
      if(this.getShow() === 'label' && this.__icon) {
        this.__icon.exclude();
      }

      this._add(this.__childrenContainer);
    }
  },


  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
      this._disposeObjects("__label", "__icon", "__childrenContainer");
  }

});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2013 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Tino Butz (tbtz)

************************************************************************ */

/**
 * The image widget displays an image file.
 *
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   var image = new qx.ui.mobile.basic.Image("path/to/icon.png");
 *
 *   this.getRoot().add(image);
 * </pre>
 *
 * This example create a widget to display the image
 * <code>path/to/icon.png</code>.
 *
 */
qx.Class.define("qx.ui.mobile.basic.Image",
{
  extend : qx.ui.mobile.core.Widget,



  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param source {String?null} The URL of the image to display.
   */
  construct : function(source)
  {
    this.base(arguments);

    if (qx.ui.mobile.basic.Image.ROOT === null) {
      qx.ui.mobile.basic.Image.ROOT = qx.core.Init.getApplication().getRoot();
    }

    if (source) {
      this.setSource(source);
    } else {
      this.initSource();
    }

    qx.ui.mobile.basic.Image.ROOT.addListener("changeAppScale", this._onChangeAppScale, this);
  },



  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /**
     * Fired if the image source can not be loaded.
     */
    loadingFailed : "qx.event.type.Event",


    /**
     * Fired if the image has been loaded.
     */
    loaded : "qx.event.type.Event"
  },


  statics :
  {
    /** @type {Array} Possible pixel ratios of the current device operating system */
    PIXEL_RATIOS : null,

    /** @type {qx.ui.mobile.core.Root} the mobile application root */
    ROOT : null
  },


  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /**
     * The URL of the image to display.
     */
    source :
    {
      check : "String",
      nullable : true,
      init : null,
      apply : "_applySource"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // overridden
    _getTagName : function() {
      return "img";
    },


    // property apply
    _applySource : function(value, old)
    {
      var source = value;
      if (source && source.indexOf('data:') != 0) {
        var resourceManager = qx.util.ResourceManager.getInstance();

        this._setStyle("width", resourceManager.getImageWidth(source) / 16 + "rem");
        this._setStyle("height", resourceManager.getImageHeight(source) / 16 + "rem");

        var foundHighResolutionSource = this._findHighResolutionSource(source);

        source = resourceManager.toUri(source);
        var ImageLoader = qx.io.ImageLoader;
        if (!ImageLoader.isFailed(source) && !ImageLoader.isLoaded(source)) {
          ImageLoader.load(source, this.__loaderCallback, this);
        }

        // If a no high-resolution version of the source was found, apply the source.
        if (foundHighResolutionSource == false) {
          this._setSource(source);
        }
      } else {
        this._setSource(source);
      }
    },


    /**
    * Event handler for "changeAppScale" on application root.
    * Reloads the image source.
    */
    _onChangeAppScale : function() {
      this._applySource(this.getSource());
    },


    /**
    * Detects whether there is a high-resolution image available.
    * A high-resolution image is assumed to have the same file name as
    * the parameter source, but with a pixelRatio identifier before the file
    * extension, like "@2x".
    * Medium Resolution: "example.png", high-resolution: "example@2x.png"
    * If an image with a higher resolution is available, the method
    * {@link #_createHighResolutionOverlay} is called.
    *
    * @param source {String} source of the medium resolution image.
    * @return {Boolean} If a high-resolution image source was found or not.
    */
    _findHighResolutionSource: function(source) {
      var pixelRatioCandidates = qx.ui.mobile.basic.Image.PIXEL_RATIOS;

      // Calculate the optimal ratio, based on the rem scale factor of the application and the device pixel ratio.
      var factor = qx.ui.mobile.basic.Image.ROOT.getAppScale();
      if (factor <= 1) {
        return false;
      }

      var i = pixelRatioCandidates.length;
      while (i > 0 && factor > pixelRatioCandidates[--i]) {}

      var imgSrc;

      // Search for best img with a higher resolution.
      for (var k = i; k >= 0; k--) {
        imgSrc = this._getHighResolutionSource(source, pixelRatioCandidates[k]);
        if (imgSrc) {
          this._createHighResolutionOverlay(imgSrc);
          return true;
        }
      }

      // Search for best img with a lower resolution.
      for (var k = i + 1; k < pixelRatioCandidates.length; k++) {
        imgSrc = this._getHighResolutionSource(source, pixelRatioCandidates[k]);
        if (imgSrc) {
          this._createHighResolutionOverlay(imgSrc);
          return true;
        }
      }

      return false;
    },

    /**
    * Returns the source name for the high-resolution image based on the passed
    * parameters.
    * @param source {String} the source of the medium resolution image.
    * @param pixelRatio {Number} the pixel ratio of the high-resolution image.
    * @return {String} the high-resolution source name or null if no source could be found.
    */
    _getHighResolutionSource : function(source, pixelRatio) {
      var fileExtIndex = source.lastIndexOf('.');
      if (fileExtIndex > -1) {
        var pixelRatioIdentifier = "@" + pixelRatio + "x";
        var candidate = source.slice(0, fileExtIndex) + pixelRatioIdentifier + source.slice(fileExtIndex);

        if(qx.util.ResourceManager.getInstance().has(candidate)) {
          return candidate;
        }
      }
      return null;
    },


    /**
    * Creates an overlay for this image which shows the image defined by the parameter 'highResSource',
    * but has the same size and position as the source image.
    * The original image widget is hidden by this method.
    *
    * @param highResSource {String} Image source of the high-resolution image.
    */
    _createHighResolutionOverlay : function(highResSource) {
      // Replace the source through transparent pixel for making the high-resolution background image visible.
      this._setSource("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7");
      this._setStyle("backgroundImage","url("+qx.util.ResourceManager.getInstance().toUri(highResSource)+")");
      this._setStyle("backgroundSize","100%");
      this._setStyle("backgroundRepeat","no-repeat");
      this._setStyle("backgroundPosition","50% 50%");
    },


    /**
     * Event handler fired after the preloader has finished loading the icon
     *
     * @param source {String} Image source which was loaded
     * @param imageInfo {Map} Dimensions of the loaded image
     */
    __loaderCallback : function(source, imageInfo)
    {
      // Ignore the callback on already disposed images
      if (this.$$disposed === true) {
        return;
      }

      // Output a warning if the image could not loaded and quit
      if (imageInfo.failed)
      {
        this.warn("Image could not be loaded: " + source);
        this.fireEvent("loadingFailed");
      }
      else if (imageInfo.aborted)
      {
        // ignore the rest because it is aborted
        return;
      }
      else
      {
        this.fireEvent("loaded");
      }
      this._domUpdated();
    },


    /**
     * Sets the source attribute of the image tag.
     *
     * @param source {String} Image source which was loaded
     */
    _setSource : function(source)
    {
      this._setAttribute("src", source);
    },


    /**
     * Sets the attribute draggable to the given value "isDraggable".
     * @param isDraggable {Boolean} target value.
     */
    setDraggable : function(isDraggable) {
      if(isDraggable){
        this._setAttribute("draggable", "true");
      } else {
        this._setAttribute("draggable", "false");
      }
    }
  },


  defer : function(statics) {
    statics.PIXEL_RATIOS = ["3", "2", "1.5"];
  },


  destruct : function() {
    qx.ui.mobile.basic.Image.ROOT.removeListener("changeAppScale", this._onChangeAppScale, this);
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

   Author:
     * Daniel Wagner (danielwagner)

************************************************************************ */

/**
 * Provides read/write access to library-specific information such as
 * source/resource URIs.
 */
qx.Class.define("qx.util.LibraryManager", {

  extend : qx.core.Object,

  type : "singleton",

  statics :
  {
    /** @type {Map} The libraries used by this application */
    __libs : qx.$$libraries || {}
  },

  members :
  {
    /**
     * Checks whether the library with the given namespace is known to the
     * application.
     * @param namespace {String} The library's namespace
     * @return {Boolean} <code>true</code> if the given library is known
     */
    has : function(namespace)
    {
      return !!this.self(arguments).__libs[namespace];
    },


    /**
     * Returns the value of an attribute of the given library
     * @param namespace {String} The library's namespace
     * @param key {String} Name of the attribute
     * @return {var|null} The attribute's value or <code>null</code> if it's not defined
     */
    get : function(namespace, key)
    {
      return this.self(arguments).__libs[namespace][key] ?
        this.self(arguments).__libs[namespace][key] : null;
    },


    /**
     * Sets an attribute on the given library.
     *
     * @param namespace {String} The library's namespace
     * @param key {String} Name of the attribute
     * @param value {var} Value of the attribute
     */
    set : function(namespace, key, value)
    {
      this.self(arguments).__libs[namespace][key] = value;
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
     * Fabian Jakobs (fjakobs)

************************************************************************ */

/**
 * Contains information about images (size, format, clipping, ...) and
 * other resources like CSS files, local data, ...
 */
qx.Class.define("qx.util.ResourceManager",
{
  extend  : qx.core.Object,
  type    : "singleton",

  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);
  },

  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /** @type {Map} the shared image registry */
    __registry : qx.$$resources || {},

    /** @type {Map} prefix per library used in HTTPS mode for IE */
    __urlPrefix : {}
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {

    /**
     * Whether the registry has information about the given resource.
     *
     * @param id {String} The resource to get the information for
     * @return {Boolean} <code>true</code> when the resource is known.
     */
    has : function(id) {
      return !!this.self(arguments).__registry[id];
    },


    /**
     * Get information about an resource.
     *
     * @param id {String} The resource to get the information for
     * @return {Array} Registered data or <code>null</code>
     */
    getData : function(id) {
      return this.self(arguments).__registry[id] || null;
    },


    /**
     * Returns the width of the given resource ID,
     * when it is not a known image <code>0</code> is
     * returned.
     *
     * @param id {String} Resource identifier
     * @return {Integer} The image width, maybe <code>null</code> when the width is unknown
     */
    getImageWidth : function(id)
    {
      var entry = this.self(arguments).__registry[id];
      return entry ? entry[0] : null;
    },


    /**
     * Returns the height of the given resource ID,
     * when it is not a known image <code>0</code> is
     * returned.
     *
     * @param id {String} Resource identifier
     * @return {Integer} The image height, maybe <code>null</code> when the height is unknown
     */
    getImageHeight : function(id)
    {
      var entry = this.self(arguments).__registry[id];
      return entry ? entry[1] : null;
    },


    /**
     * Returns the format of the given resource ID,
     * when it is not a known image <code>null</code>
     * is returned.
     *
     * @param id {String} Resource identifier
     * @return {String} File format of the image
     */
    getImageFormat : function(id)
    {
      var entry = this.self(arguments).__registry[id];
      return entry ? entry[2] : null;
    },

    /**
     * Returns the format of the combined image (png, gif, ...), if the given
     * resource identifier is an image contained in one, or the empty string
     * otherwise.
     *
     * @param id {String} Resource identifier
     * @return {String} The type of the combined image containing id
     */
    getCombinedFormat : function(id)
    {
      var clippedtype = "";
      var entry = this.self(arguments).__registry[id];
      var isclipped = entry && entry.length > 4 && typeof(entry[4]) == "string"
        && this.constructor.__registry[entry[4]];
      if (isclipped){
        var combId  = entry[4];
        var combImg = this.constructor.__registry[combId];
        clippedtype = combImg[2];
      }
      return clippedtype;
    },


    /**
     * Converts the given resource ID to a full qualified URI
     *
     * @param id {String} Resource ID
     * @return {String} Resulting URI
     */
    toUri : function(id)
    {
      if (id == null) {
        return id;
      }

      var entry = this.self(arguments).__registry[id];
      if (!entry) {
        return id;
      }

      if (typeof entry === "string") {
        var lib = entry;
      }
      else
      {
        var lib = entry[3];

        // no lib reference
        // may mean that the image has been registered dynamically
        if (!lib) {
          return id;
        }
      }

      var urlPrefix = "";
      if ((qx.core.Environment.get("engine.name") == "mshtml") &&
          qx.core.Environment.get("io.ssl")) {
        urlPrefix = this.self(arguments).__urlPrefix[lib];
      }

      return urlPrefix + qx.util.LibraryManager.getInstance().get(lib, "resourceUri") + "/" + id;
    },

    /**
     * Construct a data: URI for an image resource.
     *
     * Constructs a data: URI for a given resource id, if this resource is
     * contained in a base64 combined image. If this is not the case (e.g.
     * because the combined image has not been loaded yet), returns the direct
     * URI to the image file itself.
     *
     * @param resid {String} resource id of the image
     * @return {String} "data:" or "http:" URI
     */
    toDataUri : function (resid)
    {
      var resentry = this.constructor.__registry[resid];
      var combined = this.constructor.__registry[resentry[4]];
      var uri;
      if (combined) {
        var resstruct = combined[4][resid];
        uri = "data:image/" + resstruct["type"] + ";" + resstruct["encoding"] +
              "," + resstruct["data"];
      }
      else {
        uri = this.toUri(resid);
      }
      return uri;
    }
  },


  defer : function(statics)
  {
    if ((qx.core.Environment.get("engine.name") == "mshtml"))
    {
      // To avoid a "mixed content" warning in IE when the application is
      // delivered via HTTPS a prefix has to be added. This will transform the
      // relative URL to an absolute one in IE.
      // Though this warning is only displayed in conjunction with images which
      // are referenced as a CSS "background-image", every resource path is
      // changed when the application is served with HTTPS.
      if (qx.core.Environment.get("io.ssl"))
      {
        for (var lib in qx.$$libraries)
        {
          var resourceUri;
          if (qx.util.LibraryManager.getInstance().get(lib, "resourceUri")) {
            resourceUri = qx.util.LibraryManager.getInstance().get(lib, "resourceUri");
          }
          else
          {
            // default for libraries without a resourceUri set
            statics.__urlPrefix[lib] = "";
            continue;
          }

          // It is valid to to begin a URL with "//" so this case has to
          // be considered. If the to resolved URL begins with "//" the
          // manager prefixes it with "https:" to avoid any problems for IE
          if (resourceUri.match(/^\/\//) != null) {
            statics.__urlPrefix[lib] = window.location.protocol;
          }
          // If the resourceUri begins with a single slash, include the current
          // hostname
          else if (resourceUri.match(/^\//) != null) {
            statics.__urlPrefix[lib] = window.location.protocol + "//" + window.location.host;
          }
          // If the resolved URL begins with "./" the final URL has to be
          // put together using the document.URL property.
          // IMPORTANT: this is only applicable for the source version
          else if (resourceUri.match(/^\.\//) != null)
          {
            var url = document.URL;
            statics.__urlPrefix[lib] = url.substring(0, url.lastIndexOf("/") + 1);
          } else if (resourceUri.match(/^http/) != null) {
            // Let absolute URLs pass through
            statics.__urlPrefix[lib] = "";
          }
          else
          {
            // check for parameters with URLs as value
            var index = window.location.href.indexOf("?");
            var href;
            if (index == -1) {
              href = window.location.href;
            } else {
              href = window.location.href.substring(0, index);
            }

            statics.__urlPrefix[lib] = href.substring(0, href.lastIndexOf("/") + 1);
          }
        }
      }
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
 * The ImageLoader can preload and manage loaded image resources. It easily
 * handles multiple requests and supports callbacks for successful and failed
 * requests.
 *
 * After loading of an image the dimension of the image is stored as long
 * as the application is running. This is quite useful for in-memory layouting.
 *
 * Use {@link #load} to preload your own images.
 */
qx.Bootstrap.define("qx.io.ImageLoader",
{
  statics :
  {
    /** @type {Map} Internal data structure to cache image sizes */
    __data : {},


    /** @type {Map} Default image size */
    __defaultSize :
    {
      width : null,
      height : null
    },

    /** @type {RegExp} Known image types */
    __knownImageTypesRegExp : /\.(png|gif|jpg|jpeg|bmp)\b/i,

    /** @type {RegExp} Image types of a data URL */
    __dataUrlRegExp : /^data:image\/(png|gif|jpg|jpeg|bmp)\b/i,

    /**
     * Whether the given image has previously been loaded using the
     * {@link #load} method.
     *
     * @param source {String} Image source to query
     * @return {Boolean} <code>true</code> when the image is loaded
     */
    isLoaded : function(source)
    {
      var entry = this.__data[source];
      return !!(entry && entry.loaded);
    },


    /**
     * Whether the given image has previously been requested using the
     * {@link #load} method but failed.
     *
     * @param source {String} Image source to query
     * @return {Boolean} <code>true</code> when the image loading failed
     */
    isFailed : function(source)
    {
      var entry = this.__data[source];
      return !!(entry && entry.failed);
    },


    /**
     * Whether the given image is currently loading.
     *
     * @param source {String} Image source to query
     * @return {Boolean} <code>true</code> when the image is loading in the moment.
     */
    isLoading : function(source)
    {
      var entry = this.__data[source];
      return !!(entry && entry.loading);
    },


    /**
     * Returns the format of a previously loaded image
     *
     * @param source {String} Image source to query
     * @return {String ? null} The format of the image or <code>null</code>
     */
    getFormat : function(source)
    {
      var entry = this.__data[source];

      if (! entry || ! entry.format)
      {
        var result = this.__dataUrlRegExp.exec(source);
        if (result != null)
        {
          // If width and height aren't defined, provide some defaults
          var width =
            (entry && qx.lang.Type.isNumber(entry.width)
             ? entry.width
             : this.__defaultSize.width);

          var height =
            (entry && qx.lang.Type.isNumber(entry.height)
             ? entry.height
             : this.__defaultSize.height);

          entry =
            {
              loaded : true,
              format : result[1],
              width  : width,
              height : height
            };
        }
      }
      return entry ? entry.format : null;
    },


    /**
     * Returns the size of a previously loaded image
     *
     * @param source {String} Image source to query
     * @return {Map} The dimension of the image (<code>width</code> and
     *    <code>height</code> as key). If the image is not yet loaded, the
     *    dimensions are given as <code>null</code> for width and height.
     */
    getSize : function(source) {
      var entry = this.__data[source];
      return entry ? { width: entry.width, height: entry.height } : this.__defaultSize;
    },


    /**
     * Returns the image width
     *
     * @param source {String} Image source to query
     * @return {Integer} The width or <code>null</code> when the image is not loaded
     */
    getWidth : function(source)
    {
      var entry = this.__data[source];
      return entry ? entry.width : null;
    },


    /**
     * Returns the image height
     *
     * @param source {String} Image source to query
     * @return {Integer} The height or <code>null</code> when the image is not loaded
     */
    getHeight : function(source)
    {
      var entry = this.__data[source];
      return entry ? entry.height : null;
    },


    /**
     * Loads the given image. Supports a callback which is
     * executed when the image is loaded.
     *
     * This method works asychronous.
     *
     * @param source {String} Image source to load
     * @param callback {Function} Callback function to execute
     *   The first parameter of the callback is the given source url, the
     *   second parameter is the data entry which contains additional
     *   information about the image.
     * @param context {Object} Context in which the given callback should be executed
     */
    load : function(source, callback, context)
    {
      // Shorthand
      var entry = this.__data[source];

      if (!entry) {
        entry = this.__data[source] = {};
      }

      // Normalize context
      if (callback && !context) {
        context = window;
      }

      // Already known image source
      if (entry.loaded || entry.loading || entry.failed)
      {
        if (callback)
        {
          if (entry.loading) {
            entry.callbacks.push(callback, context);
          } else {
            callback.call(context, source, entry);
          }
        }
      }
      else
      {
        // Updating entry
        entry.loading = true;
        entry.callbacks = [];

        if (callback) {
          entry.callbacks.push(callback, context);
        }

        // Create image element
        var el = new Image();

        // Create common callback routine
        var boundCallback = qx.lang.Function.listener(this.__onload, this, el, source);

        // Assign callback to element
        el.onload = boundCallback;
        el.onerror = boundCallback;

        // Start loading of image
        el.src = source;

        // save the element for aborting
        entry.element = el;
      }
    },


    /**
     * Abort the loading for the given url.
     *
     * @param source {String} URL of the image to abort its loading.
     */
    abort : function (source)
    {
      var entry = this.__data[source];

      if (entry && !entry.loaded)
      {
        entry.aborted = true;

        var callbacks = entry.callbacks;
        var element = entry.element;

        // Cleanup listeners
        element.onload = element.onerror = null;

        // Cleanup entry
        delete entry.callbacks;
        delete entry.element;
        delete entry.loading;

        for (var i=0, l=callbacks.length; i<l; i+=2) {
          callbacks[i].call(callbacks[i+1], source, entry);
        }
      }

      this.__data[source] = null;
    },


    /**
     * Internal event listener for all load/error events.
     *
     * @signature function(event, element, source)
     *
     * @param event {Event} Native event object
     * @param element {Element} DOM element which represents the image
     * @param source {String} The image source loaded
     */
    __onload : qx.event.GlobalError.observeMethod(function(event, element, source)
    {
      // Shorthand
      var entry = this.__data[source];

      var isImageAvailable = function(imgElem) {
        return (imgElem && imgElem.height !== 0);
      };

      // [BUG #7497]: IE11 doesn't properly emit an error event
      // when loading fails so augment success check
      if (event.type === "load" && isImageAvailable(element))
      {
        // Store dimensions
        entry.loaded = true;
        entry.width = this.__getWidth(element);
        entry.height = this.__getHeight(element);

        // try to determine the image format
        var result = this.__knownImageTypesRegExp.exec(source);
        if (result != null)
        {
          entry.format = result[1];
        }
      }
      else
      {
        entry.failed = true;
      }

      // Cleanup listeners
      element.onload = element.onerror = null;

      // Cache callbacks
      var callbacks = entry.callbacks;

      // Cleanup entry
      delete entry.loading;
      delete entry.callbacks;
      delete entry.element;

      // Execute callbacks
      for (var i=0, l=callbacks.length; i<l; i+=2) {
        callbacks[i].call(callbacks[i+1], source, entry);
      }
    }),


    /**
     * Returns the natural width of the given image element.
     *
     * @param element {Element} DOM element which represents the image
     * @return {Integer} Image width
     */
    __getWidth : function(element)
    {
      return qx.core.Environment.get("html.image.naturaldimensions") ?
        element.naturalWidth : element.width;
    },


    /**
     * Returns the natural height of the given image element.
     *
     * @param element {Element} DOM element which represents the image
     * @return {Integer} Image height
     */
    __getHeight : function(element)
    {
      return qx.core.Environment.get("html.image.naturaldimensions") ?
        element.naturalHeight : element.height;
    },

    /**
     * Dispose stored images.
     */
    dispose : function()
    {
      this.__data = {};
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
 * A Button widget.
 *
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   var button = new qx.ui.mobile.form.Button("Hello World");
 *
 *   button.addListener("tap", function(e) {
 *     alert("Button was clicked");
 *   }, this);
 *
 *   this.getRoot.add(button);
 * </pre>
 *
 * This example creates a button with the label "Hello World" and attaches an
 * event listener to the {@link qx.ui.mobile.core.Widget#tap} event.
 */
qx.Class.define("qx.ui.mobile.form.Button",
{
  extend : qx.ui.mobile.basic.Atom,

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
      init : "button"
    },

    // overridden
    activatable :
    {
      refine :true,
      init : true
    }
  },

  members :
  {
    /**
     * Sets the value.
     *
     * @param value {String} The value to set
     */
    setValue : function(value) {
      this.setLabel(value);
    },


    /**
     * Returns the set value.
     *
     * @return {String} The set value
     */
    getValue : function() {
      return this.getLabel();
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
 * A navigation bar button widget.
 */
qx.Class.define("qx.ui.mobile.navigationbar.Button",
{
  extend : qx.ui.mobile.form.Button,


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
      init : "navigationbar-button"
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
 * A navigation bar back button widget.
 */
qx.Class.define("qx.ui.mobile.navigationbar.BackButton",
{
  extend : qx.ui.mobile.navigationbar.Button,


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
      init : "navigationbar-backbutton"
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


/* ************************************************************************


************************************************************************ */

/**
 * Mixin for the {@link Scroll} container. Used when the variant
 * <code>qx.mobile.nativescroll</code> is set to "off". Uses the iScroll script to simulate
 * the CSS position:fixed style. Position fixed is not available in iOS and
 * Android < 2.2.
 *
 * @ignore(iScroll)
 * @asset(qx/mobile/js/iscroll*.js)
 */
qx.Mixin.define("qx.ui.mobile.container.MIScroll",
{

  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.__initScroll();
    this.__registerEventListeners();
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    __scroll : null,

    /**
     * Mixin method. Creates the scroll element.
     *
     * @return {Element} The scroll element
     */
    _createScrollElement : function()
    {
      var scroll = qx.dom.Element.create("div");
      qx.bom.element.Class.add(scroll,"iscroll");
      return scroll;
    },


    /**
     * Mixin method. Returns the scroll content element..
     *
     * @return {Element} The scroll content element
     */
    _getScrollContentElement : function()
    {
      return this.getContainerElement().childNodes[0];
    },


   /**
    * Scrolls the wrapper contents to the x/y coordinates in a given period.
    *
    * @param x {Integer} X coordinate to scroll to.
    * @param y {Integer} Y coordinate to scroll to.
    * @param time {Integer} Time slice in which scrolling should
    *              be done.
    */
    _scrollTo : function(x, y, time)
    {
      if (this.__scroll) {
        this.__scroll.scrollTo(x, y, time);
      }
    },


    /**
    * Scrolls the wrapper contents to the widgets coordinates in a given
    * period.
    *
    * @param elementId {String} the elementId, the scroll container should scroll to.
    * @param time {Integer?0} Time slice in which scrolling should be done (in seconds).
    *
    */
    _scrollToElement : function(elementId, time)
    {
      if(typeof time === "undefined") {
        time = 0;
      }
      if (this.__scroll) {
        this.__scroll.scrollToElement("#"+elementId, time);
      }
    },


    /**
     * Loads and inits the iScroll instance.
     *
     * @ignore(iScroll)
     */
    __initScroll : function()
    {
      if (!window.iScroll)
      {
        if (qx.core.Environment.get("qx.debug"))
        {
          var resource = "qx/mobile/js/iscroll.js";
        } else {
          var resource = "qx/mobile/js/iscroll.min.js";
        }
        var path = qx.util.ResourceManager.getInstance().toUri(resource);
        if (qx.core.Environment.get("qx.debug"))
        {
          path += "?" + new Date().getTime();
        }
        var loader = new qx.bom.request.Script();
        loader.on("load", this.__onScrollLoaded, this);
        loader.open("GET", path);
        loader.send();
      } else {
        this._setScroll(this.__createScrollInstance());
      }
    },


    /**
     * Creates the iScroll instance.
     *
     * @return {Object} The iScroll instance
     * @ignore(iScroll)
     */
    __createScrollInstance : function()
    {
      var defaultScrollProperties = this._getDefaultScrollProperties();
      var customScrollProperties = {};

      if(this._scrollProperties != null) {
        customScrollProperties = this._scrollProperties;
      }

      var iScrollProperties = qx.lang.Object.mergeWith(defaultScrollProperties, customScrollProperties, true);

      return new iScroll(this.getContainerElement(), iScrollProperties);
    },


    /**
     * Returns a map with default iScroll properties for the iScroll instance.
     * @return {Object} Map with default iScroll properties
     */
    _getDefaultScrollProperties : function() {
      var container = this;

      return {
        hideScrollbar: true,
        fadeScrollbar: true,
        hScrollbar : false,
        scrollbarClass: "scrollbar",
        useTransform: true,
        onScrollEnd : function() {
          // Alert interested parties that we scrolled to end of page.
          if (qx.core.Environment.get("qx.mobile.nativescroll") == false)
          {
            if(this.y == this.maxScrollY) {
              container.fireEvent("pageEnd");
            }
          }
        },
        onScrollMove : function() {
          // Alert interested parties that we scrolled to end of page.
          if (qx.core.Environment.get("qx.mobile.nativescroll") == false)
          {
            if(this.y == this.maxScrollY) {
              container.fireEvent("pageEnd");
            }
          }
        },
        onBeforeScrollStart : function(e) {
          // QOOXDOO ENHANCEMENT: Do not prevent default for form elements
          /* When updating iScroll, please check out that doubleTapTimer is not active (commented out)
           * in code. DoubleTapTimer creates a fake click event. Android 4.1. and newer
           * is able to fire native events, which  create side effect with the fake event of iScroll. */
          var target = e.target;
          while (target.nodeType != 1) {
            target = target.parentNode;
          }

          if (target.tagName != 'SELECT' && target.tagName != 'INPUT' && target.tagName != 'TEXTAREA' && target.tagName != 'LABEL') {
            // Remove focus from input elements, so that the keyboard and the mouse cursor is hidden
            var elements = [];
            var inputElements = qx.lang.Array.cast(document.getElementsByTagName("input"), Array);
            var textAreaElements = qx.lang.Array.cast(document.getElementsByTagName("textarea"), Array);
            elements = elements.concat(inputElements);
            elements = elements.concat(textAreaElements);

            for (var i=0, length = elements.length; i < length; i++) {
              elements[i].blur();
            }

            e.preventDefault();
          }

          // we also want to alert interested parties that we are starting scrolling
          if (qx.core.Environment.get("qx.mobile.nativescroll") == false)
          {
            var iScrollStartEvent = new qx.event.message.Message('iscrollstart');
            qx.event.message.Bus.getInstance().dispatch(iScrollStartEvent);
          }
        }
      }
    },


    /**
     * Registers all needed event listener.
     */
    __registerEventListeners : function()
    {
      qx.event.Registration.addListener(window, "orientationchange", this._refresh, this);
      qx.event.Registration.addListener(window, "resize", this._refresh, this);
      this.addListener("domupdated", this._refresh, this);
    },


    /**
     * Unregisters all needed event listener.
     */
    __unregisterEventListeners : function()
    {
      qx.event.Registration.removeListener(window, "orientationchange", this._refresh, this);
      qx.event.Registration.removeListener(window, "resize", this._refresh, this);
      this.removeListener("domupdated", this._refresh, this);
    },


    /**
     * Load callback. Called when the iScroll script is loaded.
     *
     * @param request {qx.bom.request.Script} The Script request object
     */
    __onScrollLoaded : function(request)
    {
      if (request.status < 400)
      {
        this._setScroll(this.__createScrollInstance());
      } else {
        if (qx.core.Environment.get("qx.debug"))
        {
          this.error("Could not load iScroll");
        }
      }
    },


    /**
     * Setter for the scroll instance.
     *
     * @param scroll {Object} iScroll instance.
     */
    _setScroll : function(scroll)
    {
      this.__scroll = scroll;
    },


    /**
     * Delegation method for iScroll. Disabled the iScroll objects.
     * Prevents any further scrolling of this container.
     */
    disable : function() {
      if(this.__scroll) {
        this.__scroll.disable();
      }
    },


    /**
     * Delegation method for iScroll. Enables the iScroll object.
     */
    enable : function() {
      if(this.__scroll) {
        this.__scroll.enable();
      }
    },


    /**
     * Calls the refresh function of iScroll. Needed to recalculate the
     * scrolling container.
     */
    _refresh : function()
    {
      if (this.__scroll) {
        this.__scroll.refresh();
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
    this.__unregisterEventListeners();

    // Cleanup iScroll
    if (this.__scroll) {
      this.__scroll.destroy();
    }
    this.__scroll;
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
     * Tristan Koch (tristankoch)

************************************************************************ */

/**
 * Script loader with interface similar to
 * <a href="http://www.w3.org/TR/XMLHttpRequest/">XmlHttpRequest</a>.
 *
 * The script loader can be used to load scripts from arbitrary sources.
 * <span class="desktop">
 * For JSONP requests, consider the {@link qx.bom.request.Jsonp} transport
 * that derives from the script loader.
 * </span>
 *
 * <div class="desktop">
 * Example:
 *
 * <pre class="javascript">
 *  var req = new qx.bom.request.Script();
 *  req.onload = function() {
 *    // Script is loaded and parsed and
 *    // globals set are available
 *  }
 *
 *  req.open("GET", url);
 *  req.send();
 * </pre>
 * </div>
 *
 * @ignore(qx.core, qx.core.Environment.*)
 * @require(qx.bom.request.Script#_success)
 * @require(qx.bom.request.Script#abort)
 * @require(qx.bom.request.Script#dispose)
 * @require(qx.bom.request.Script#isDisposed)
 * @require(qx.bom.request.Script#getAllResponseHeaders)
 * @require(qx.bom.request.Script#getResponseHeader)
 * @require(qx.bom.request.Script#setDetermineSuccess)
 * @require(qx.bom.request.Script#setRequestHeader)
 *
 * @group (IO)
 */

qx.Bootstrap.define("qx.bom.request.Script",
{

  construct : function()
  {
    this.__initXhrProperties();

    this.__onNativeLoadBound = qx.Bootstrap.bind(this._onNativeLoad, this);
    this.__onNativeErrorBound = qx.Bootstrap.bind(this._onNativeError, this);
    this.__onTimeoutBound = qx.Bootstrap.bind(this._onTimeout, this);

    this.__headElement = document.head || document.getElementsByTagName( "head" )[0] ||
                         document.documentElement;

    this._emitter = new qx.event.Emitter();

    // BUGFIX: Browsers not supporting error handler
    // Set default timeout to capture network errors
    //
    // Note: The script is parsed and executed, before a "load" is fired.
    this.timeout = this.__supportsErrorHandler() ? 0 : 15000;
  },


  events : {
    /** Fired at ready state changes. */
    "readystatechange" : "qx.bom.request.Script",

    /** Fired on error. */
    "error" : "qx.bom.request.Script",

    /** Fired at loadend. */
    "loadend" : "qx.bom.request.Script",

    /** Fired on timeouts. */
    "timeout" : "qx.bom.request.Script",

    /** Fired when the request is aborted. */
    "abort" : "qx.bom.request.Script",

    /** Fired on successful retrieval. */
    "load" : "qx.bom.request.Script"
  },


  members :
  {

    /**
     * @type {Number} Ready state.
     *
     * States can be:
     * UNSENT:           0,
     * OPENED:           1,
     * LOADING:          2,
     * LOADING:          3,
     * DONE:             4
     *
     * Contrary to {@link qx.bom.request.Xhr#readyState}, the script transport
     * does not receive response headers. For compatibility, another LOADING
     * state is implemented that replaces the HEADERS_RECEIVED state.
     */
    readyState: null,

    /**
     * @type {Number} The status code.
     *
     * Note: The script transport cannot determine the HTTP status code.
     */
    status: null,

    /**
     * @type {String} The status text.
     *
     * The script transport does not receive response headers. For compatibility,
     * the statusText property is set to the status casted to string.
     */
    statusText: null,

    /**
     * @type {Number} Timeout limit in milliseconds.
     *
     * 0 (default) means no timeout.
     */
    timeout: null,

    /**
     * @type {Function} Function that is executed once the script was loaded.
     */
    __determineSuccess: null,


    /**
     * Add an event listener for the given event name.
     *
     * @param name {String} The name of the event to listen to.
     * @param listener {Function} The function to execute when the event is fired
     * @param ctx {var?} The context of the listener.
     * @return {qx.bom.request.Script} Self for chaining.
     */
    on: function(name, listener, ctx) {
      this._emitter.on(name, listener, ctx);
      return this;
    },


    /**
     * Initializes (prepares) request.
     *
     * @param method {String}
     *   The HTTP method to use.
     *   This parameter exists for compatibility reasons. The script transport
     *   does not support methods other than GET.
     * @param url {String}
     *   The URL to which to send the request.
     */
    open: function(method, url) {
      if (this.__disposed) {
        return;
      }

      // Reset XHR properties that may have been set by previous request
      this.__initXhrProperties();

      this.__abort = null;
      this.__url = url;

      if (this.__environmentGet("qx.debug.io")) {
        qx.Bootstrap.debug(qx.bom.request.Script, "Open native request with " +
          "url: " + url);
      }

      this._readyStateChange(1);
    },

    /**
     * Appends a query parameter to URL.
     *
     * This method exists for compatibility reasons. The script transport
     * does not support request headers. However, many services parse query
     * parameters like request headers.
     *
     * Note: The request must be initialized before using this method.
     *
     * @param key {String}
     *  The name of the header whose value is to be set.
     * @param value {String}
     *  The value to set as the body of the header.
     * @return {qx.bom.request.Script} Self for chaining.
     */
    setRequestHeader: function(key, value) {
      if (this.__disposed) {
        return null;
      }

      var param = {};

      if (this.readyState !== 1) {
        throw new Error("Invalid state");
      }

      param[key] = value;
      this.__url = qx.util.Uri.appendParamsToUrl(this.__url, param);
      return this;
    },

    /**
     * Sends request.
     * @return {qx.bom.request.Script} Self for chaining.
     */
    send: function() {
      if (this.__disposed) {
        return null;
      }

      var script = this.__createScriptElement(),
          head = this.__headElement,
          that = this;

      if (this.timeout > 0) {
        this.__timeoutId = window.setTimeout(this.__onTimeoutBound, this.timeout);
      }

      if (this.__environmentGet("qx.debug.io")) {
        qx.Bootstrap.debug(qx.bom.request.Script, "Send native request");
      }

      // Attach script to DOM
      head.insertBefore(script, head.firstChild);

      // The resource is loaded once the script is in DOM.
      // Assume HEADERS_RECEIVED and LOADING and dispatch async.
      window.setTimeout(function() {
        that._readyStateChange(2);
        that._readyStateChange(3);
      });
      return this;
    },

    /**
     * Aborts request.
     * @return {qx.bom.request.Script} Self for chaining.
     */
    abort: function() {
      if (this.__disposed) {
        return null;
      }

      this.__abort = true;
      this.__disposeScriptElement();
      this._emit("abort");
      return this;
    },


    /**
     * Helper to emit events and call the callback methods.
     * @param event {String} The name of the event.
     */
    _emit: function(event) {
      this["on" + event]();
      this._emitter.emit(event, this);
    },


    /**
     * Event handler for an event that fires at every state change.
     *
     * Replace with custom method to get informed about the communication progress.
     */
    onreadystatechange: function() {},

    /**
     * Event handler for XHR event "load" that is fired on successful retrieval.
     *
     * Note: This handler is called even when an invalid script is returned.
     *
     * Warning: Internet Explorer < 9 receives a false "load" for invalid URLs.
     * This "load" is fired about 2 seconds after sending the request. To
     * distinguish from a real "load", consider defining a custom check
     * function using {@link #setDetermineSuccess} and query the status
     * property. However, the script loaded needs to have a known impact on
     * the global namespace. If this does not work for you, you may be able
     * to set a timeout lower than 2 seconds, depending on script size,
     * complexity and execution time.
     *
     * Replace with custom method to listen to the "load" event.
     */
    onload: function() {},

    /**
     * Event handler for XHR event "loadend" that is fired on retrieval.
     *
     * Note: This handler is called even when a network error (or similar)
     * occurred.
     *
     * Replace with custom method to listen to the "loadend" event.
     */
    onloadend: function() {},

    /**
     * Event handler for XHR event "error" that is fired on a network error.
     *
     * Note: Some browsers do not support the "error" event.
     *
     * Replace with custom method to listen to the "error" event.
     */
    onerror: function() {},

    /**
    * Event handler for XHR event "abort" that is fired when request
    * is aborted.
    *
    * Replace with custom method to listen to the "abort" event.
    */
    onabort: function() {},

    /**
    * Event handler for XHR event "timeout" that is fired when timeout
    * interval has passed.
    *
    * Replace with custom method to listen to the "timeout" event.
    */
    ontimeout: function() {},

    /**
     * Get a single response header from response.
     *
     * Note: This method exists for compatibility reasons. The script
     * transport does not receive response headers.
     *
     * @param key {String}
     *  Key of the header to get the value from.
     * @return {String|null} Warning message or <code>null</code> if the request
     * is disposed
     */
    getResponseHeader: function(key) {
      if (this.__disposed) {
        return null;
      }

      if (this.__environmentGet("qx.debug")) {
        qx.Bootstrap.debug("Response header cannot be determined for " +
          "requests made with script transport.");
      }
      return "unknown";
    },

    /**
     * Get all response headers from response.
     *
     * Note: This method exists for compatibility reasons. The script
     * transport does not receive response headers.
     * @return {String|null} Warning message or <code>null</code> if the request
     * is disposed
     */
    getAllResponseHeaders: function() {
      if (this.__disposed) {
        return null;
      }

      if (this.__environmentGet("qx.debug")) {
        qx.Bootstrap.debug("Response headers cannot be determined for" +
          "requests made with script transport.");
      }

      return "Unknown response headers";
    },

    /**
     * Determine if loaded script has expected impact on global namespace.
     *
     * The function is called once the script was loaded and must return a
     * boolean indicating if the response is to be considered successful.
     *
     * @param check {Function} Function executed once the script was loaded.
     *
     */
    setDetermineSuccess: function(check) {
      this.__determineSuccess = check;
    },

    /**
     * Dispose object.
     */
    dispose: function() {
      var script = this.__scriptElement;

      if (!this.__disposed) {

        // Prevent memory leaks
        if (script) {
          script.onload = script.onreadystatechange = null;
          this.__disposeScriptElement();
        }

        if (this.__timeoutId) {
          window.clearTimeout(this.__timeoutId);
        }

        this.__disposed = true;
      }
    },


    /**
     * Check if the request has already beed disposed.
     * @return {Boolean} <code>true</code>, if the request has been disposed.
     */
    isDisposed : function() {
      return !!this.__disposed;
    },


    /*
    ---------------------------------------------------------------------------
      PROTECTED
    ---------------------------------------------------------------------------
    */

    /**
     * Get URL of request.
     *
     * @return {String} URL of request.
     */
    _getUrl: function() {
      return this.__url;
    },

    /**
     * Get script element used for request.
     *
     * @return {Element} Script element.
     */
    _getScriptElement: function() {
      return this.__scriptElement;
    },

    /**
     * Handle timeout.
     */
    _onTimeout: function() {
      this.__failure();

      if (!this.__supportsErrorHandler()) {
        this._emit("error");
      }

      this._emit("timeout");

      if (!this.__supportsErrorHandler()) {
        this._emit("loadend");
      }
    },

    /**
     * Handle native load.
     */
    _onNativeLoad: function() {
      var script = this.__scriptElement,
          determineSuccess = this.__determineSuccess,
          that = this;

      // Aborted request must not fire load
      if (this.__abort) {
        return;
      }

      // BUGFIX: IE < 9
      // When handling "readystatechange" event, skip if readyState
      // does not signal loaded script
      if (this.__environmentGet("engine.name") === "mshtml" &&
          this.__environmentGet("browser.documentmode") < 9) {
        if (!(/loaded|complete/).test(script.readyState)) {
          return;
        } else {
          if (this.__environmentGet("qx.debug.io")) {
            qx.Bootstrap.debug(qx.bom.request.Script, "Received native readyState: loaded");
          }
        }
      }

      if (this.__environmentGet("qx.debug.io")) {
        qx.Bootstrap.debug(qx.bom.request.Script, "Received native load");
      }

      // Determine status by calling user-provided check function
      if (determineSuccess) {

        // Status set before has higher precedence
        if (!this.status) {
          this.status = determineSuccess() ? 200 : 500;
        }

      }

      if (this.status === 500) {
        if (this.__environmentGet("qx.debug.io")) {
          qx.Bootstrap.debug(qx.bom.request.Script, "Detected error");
        }
      }

      if (this.__timeoutId) {
        window.clearTimeout(this.__timeoutId);
      }

      window.setTimeout(function() {
        that._success();
        that._readyStateChange(4);
        that._emit("load");
        that._emit("loadend");
      });
    },

    /**
     * Handle native error.
     */
    _onNativeError: function() {
      this.__failure();
      this._emit("error");
      this._emit("loadend");
    },

    /*
    ---------------------------------------------------------------------------
      PRIVATE
    ---------------------------------------------------------------------------
    */

    /**
     * @type {Element} Script element
     */
    __scriptElement: null,

    /**
     * @type {Element} Head element
     */
    __headElement: null,

    /**
     * @type {String} URL
     */
    __url: "",

    /**
     * @type {Function} Bound _onNativeLoad handler.
     */
    __onNativeLoadBound: null,

    /**
     * @type {Function} Bound _onNativeError handler.
     */
    __onNativeErrorBound: null,

    /**
     * @type {Function} Bound _onTimeout handler.
     */
    __onTimeoutBound: null,

    /**
     * @type {Number} Timeout timer iD.
     */
    __timeoutId: null,

    /**
     * @type {Boolean} Whether request was aborted.
     */
    __abort: null,

    /**
     * @type {Boolean} Whether request was disposed.
     */
    __disposed: null,

    /*
    ---------------------------------------------------------------------------
      HELPER
    ---------------------------------------------------------------------------
    */

    /**
     * Initialize properties.
     */
    __initXhrProperties: function() {
      this.readyState = 0;
      this.status = 0;
      this.statusText = "";
    },

    /**
     * Change readyState.
     *
     * @param readyState {Number} The desired readyState
     */
    _readyStateChange: function(readyState) {
      this.readyState = readyState;
      this._emit("readystatechange");
    },

    /**
     * Handle success.
     */
    _success: function() {
      this.__disposeScriptElement();
      this.readyState = 4;

      // By default, load is considered successful
      if (!this.status) {
        this.status = 200;
      }

      this.statusText = "" + this.status;
    },

    /**
     * Handle failure.
     */
    __failure: function() {
      this.__disposeScriptElement();
      this.readyState = 4;
      this.status = 0;
      this.statusText = null;
    },

    /**
     * Looks up whether browser supports error handler.
     *
     * @return {Boolean} Whether browser supports error handler.
     */
    __supportsErrorHandler: function() {
      var isLegacyIe = this.__environmentGet("engine.name") === "mshtml" &&
        this.__environmentGet("browser.documentmode") < 9;

      var isOpera = this.__environmentGet("engine.name") === "opera";

      return !(isLegacyIe || isOpera);
    },

    /**
     * Create and configure script element.
     *
     * @return {Element} Configured script element.
     */
    __createScriptElement: function() {
      var script = this.__scriptElement = document.createElement("script");

      script.src = this.__url;
      script.onerror = this.__onNativeErrorBound;
      script.onload = this.__onNativeLoadBound;

      // BUGFIX: IE < 9
      // Legacy IEs do not fire the "load" event for script elements.
      // Instead, they support the "readystatechange" event
      if (this.__environmentGet("engine.name") === "mshtml" &&
          this.__environmentGet("browser.documentmode") < 9) {
        script.onreadystatechange = this.__onNativeLoadBound;
      }

      return script;
    },

    /**
     * Remove script element from DOM.
     */
    __disposeScriptElement: function() {
      var script = this.__scriptElement;

      if (script && script.parentNode) {
        this.__headElement.removeChild(script);
      }
    },

    /**
     * Proxy Environment.get to guard against env not being present yet.
     *
     * @param key {String} Environment key.
     * @return {var} Value of the queried environment key
     * @lint environmentNonLiteralKey(key)
     */
    __environmentGet: function(key) {
      if (qx && qx.core && qx.core.Environment) {
        return qx.core.Environment.get(key);
      } else {
        if (key === "engine.name") {
          return qx.bom.client.Engine.getName();
        }

        if (key === "browser.documentmode") {
          return qx.bom.client.Browser.getDocumentMode();
        }

        if (key == "qx.debug.io") {
          return false;
        }

        throw new Error("Unknown environment key at this phase");
      }
    }
  },

  defer: function() {
    if (qx && qx.core && qx.core.Environment) {
      qx.core.Environment.add("qx.debug.io", false);
    }
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2007 Christian Boulanger

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Christian Boulanger

************************************************************************ */

/**
 * A message to be dispatched on the message bus.
 */
qx.Class.define("qx.event.message.Message",
{
  extend : qx.core.Object,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param name {String} The name of the message
   * @param data {var} Any type of data to attach
   */
  construct : function(name, data)
  {
    this.base(arguments);

    if (name != null) {
      this.setName(name);
    }

    if (data != null) {
      this.setData(data);
    }
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /**
     * Event name of the message. Based on this name the message is dispatched
     * to the event listeners.
     */
    name :
    {
      check       : "String"
    },

    /**
     * Any data the sender wants to pass with the event.
     */
    data :
    {
      init        : null,
      nullable    : true
    },

    /**
     * A reference to the sending object.
     */
    sender :
    {
      check       : "Object"
    }
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2007 Christian Boulanger

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Christian Boulanger

************************************************************************ */

/**
 * A simple message bus singleton.
 * The message bus registers subscriptions and notifies subscribers when
 * a matching message is dispatched
 */
qx.Class.define("qx.event.message.Bus",
{
  type : "singleton",

  extend : qx.core.Object,

  statics :
  {

    /**
     * gets the hash map of message subscriptions
     *
     * @return {Map} with registered subscriptions. The key is the
     *    <code>message</code> and the value is a map with <code>{subscriber: {Function},
     *    context: {Object|null}}</code>.
     */
    getSubscriptions : function() {
      return this.getInstance().getSubscriptions();
    },


    /**
     * subscribes to a message
     *
     * @param message {String} name of message, can be truncated by *
     * @param subscriber {Function} subscribing callback function
     * @param context {Object} The execution context of the callback (i.e. "this")
     * @return {Boolean} Success
     */
    subscribe : function(message, subscriber, context)
    {
      return this.getInstance().subscribe(message, subscriber, context);

    },

    /**
     * checks if subscription is already present
     * if you supply the callback function, match only the exact message monitor
     * otherwise match all monitors that have the given message
     *
     * @param message {String} Name of message, can be truncated by *
     * @param subscriber {Function} Callback Function
     * @param context {Object} execution context
     * @return {Boolean} Whether monitor is present or not
     */
    checkSubscription : function(message, subscriber, context)
    {
      return this.getInstance().checkSubscription(message, subscriber, context);
    },

    /**
     * unsubscribe a listening method
     * if you supply the callback function and execution context,
     * remove only this exact subscription
     * otherwise remove all subscriptions
     *
     * @param message {String} Name of message, can be truncated by *
     * @param subscriber {Function} Callback Function
     * @param context {Object} execution context
     * @return {Boolean} Whether monitor was removed or not
     */
    unsubscribe : function(message, subscriber, context)
    {
      return this.getInstance().unsubscribe(message, subscriber, context)
    },

    /**
     * dispatch message and call subscribed functions
     *
     * @param msg {qx.event.message.Message} message which is being dispatched
     * @return {Boolean} <code>true</code> if the message was dispatched,
     *    <code>false</code> otherwise.
     */
    dispatch : function(msg)
    {
      return this.getInstance().dispatch.apply(this.getInstance(), arguments);
    },

    /**
     * Dispatches a new message by supplying the name of the
     * message and its data.
     *
     * @param name {String} name of the message
     * @param data {var} Any type of data to attach
     *
     * @return {Boolean} <code>true</code> if the message was dispatched,
     *    <code>false</code> otherwise.
     */
    dispatchByName : function(name, data)
    {
      return this.getInstance().dispatchByName.apply(this.getInstance(), arguments);
    }
  },

  /**
   * constructor
   */
  construct : function()
  {
    /*
     * message subscriptions database
     */
    this.__subscriptions = {};
  },

  members :
  {
    __subscriptions : null,


    /**
     * gets the hash map of message subscriptions
     *
     * @return {Map} with registered subscriptions. The key is the
     *    <code>message</code> and the value is a map with <code>{subscriber: {Function},
     *    context: {Object|null}}</code>.
     */
    getSubscriptions : function() {
      return this.__subscriptions;
    },


    /**
     * subscribes to a message
     *
     * @param message {String} name of message, can be truncated by *
     * @param subscriber {Function} subscribing callback function
     * @param context {Object} The execution context of the callback (i.e. "this")
     * @return {Boolean} Success
     */
    subscribe : function(message, subscriber, context)
    {
      if (!message || typeof subscriber != "function")
      {
        this.error("Invalid parameters! "+ [message, subscriber, context]);

        return false;
      }

      var sub = this.getSubscriptions();

      if (this.checkSubscription(message))
      {
        if (this.checkSubscription(message, subscriber, context))
        {
          this.warn("Object method already subscribed to " + message);
          return false;
        }

        // add a subscription
        sub[message].push(
        {
          subscriber : subscriber,
          context    : context || null
        });

        return true;
      }
      else
      {
        // create a subscription
        sub[message] = [ {
          subscriber : subscriber,
          context    : context || null
        } ];

        return true;
      }
    },


    /**
     * checks if subscription is already present
     * if you supply the callback function, match only the exact message monitor
     * otherwise match all monitors that have the given message
     *
     * @param message {String} Name of message, can be truncated by *
     * @param subscriber {Function} Callback Function
     * @param context {Object} execution context
     * @return {Boolean} Whether monitor is present or not
     */
    checkSubscription : function(message, subscriber, context)
    {
      var sub = this.getSubscriptions();

      if (!sub[message] || sub[message].length === 0) {
        return false;
      }

      if (subscriber)
      {
        for (var i=0; i<sub[message].length; i++)
        {
          if (sub[message][i].subscriber === subscriber && sub[message][i].context === (context || null)) {
            return true;
          }
        }

        return false;
      }

      return true;
    },


    /**
     * unsubscribe a listening method
     * if you supply the callback function and execution context,
     * remove only this exact subscription
     * otherwise remove all subscriptions
     *
     * @param message {String} Name of message, can be truncated by *
     * @param subscriber {Function} Callback Function
     * @param context {Object} execution context
     * @return {Boolean} Whether monitor was removed or not
     */
    unsubscribe : function(message, subscriber, context)
    {
       var sub = this.getSubscriptions();
       var subscrList = sub[message];
       if (subscrList) {
         if (!subscriber) {
           sub[message] = null;
           delete sub[message];
           return true;
         } else {
           if (! context) {
             context = null;
           }
           var i = subscrList.length;
           var subscription;
           do {
             subscription = subscrList[--i];
             if (subscription.subscriber === subscriber && subscription.context === context) {
               subscrList.splice(i, 1);
               if (subscrList.length === 0) {
                 sub[message] = null;
                 delete sub[message];
               }
               return true;
             }
           } while (i);
         }
       }
       return false;
    },

    /**
     * dispatch message and call subscribed functions
     *
     * @param msg {qx.event.message.Message} message which is being dispatched
     * @return {Boolean} <code>true</code> if the message was dispatched,
     *    <code>false</code> otherwise.
     */
    dispatch : function(msg)
    {
      var sub = this.getSubscriptions();
      var msgName = msg.getName();
      var dispatched = false;

      for (var key in sub)
      {
        var pos = key.indexOf("*");

        if (pos > -1)
        {
          // use of wildcard
          if (pos === 0 || key.substr(0, pos) === msgName.substr(0, pos))
          {
            this.__callSubscribers(sub[key], msg);
            dispatched = true;
          }
        }
        else
        {
          // exact match
          if (key === msgName)
          {
            this.__callSubscribers(sub[msgName], msg);
            dispatched = true;
          }
        }
      }

      return dispatched;
    },

    /**
     * Dispatches a new message by supplying the name of the
     * message and its data.
     *
     * @param name {String} name of the message
     * @param data {var} Any type of data to attach
     *
     * @return {Boolean} <code>true</code> if the message was dispatched,
     *    <code>false</code> otherwise.
     */
    dispatchByName : function(name, data)
    {
      var message = new qx.event.message.Message(name, data);
      return this.dispatch(message);
    },


    /**
     * Call subscribers with passed message.
     *
     * @param subscribers {Map} subscribers to call
     * @param msg {qx.event.message.Message} message for subscribers
     */
    __callSubscribers : function(subscribers, msg)
    {
      for (var i=0; i<subscribers.length; i++)
      {
        var subscriber = subscribers[i].subscriber;
        var context = subscribers[i].context;

        // call message monitor subscriber
        if (context && context.isDisposed)
        {
          if (context.isDisposed())
          {
            subscribers.splice(i, 1);
            i--;
          }
          else
          {
            subscriber.call(context, msg);
          }
        }
        else
        {
          subscriber.call(context, msg);
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
 * Mixin for the {@link Scroll} container. Used when the variant
 * <code>qx.mobile.nativescroll</code> is set to "on".
 */
qx.Mixin.define("qx.ui.mobile.container.MNativeScroll",
{


  construct : function()
  {
    this.addCssClass("native");
    if(qx.core.Environment.get("os.name") == "ios") {
      this.addListener("touchstart", this._onTouchStart, this);
    }
  },


  members :
  {
    /**
     * Handler for "touchstart" event.
     * Prevents "rubber-banding" effect of page.
     * @param evt {qx.event.type.Touch} The touch event.
     */
    _onTouchStart : function(evt) {
      var parentContentElementHeight = this.getLayoutParent().getContentElement().offsetHeight;
      var contentElementHeight = this.getContentElement().scrollHeight;

      // If scroll container is scrollable
      if (contentElementHeight > parentContentElementHeight) {
        var scrollTop = this.getContentElement().scrollTop;
        var maxScrollTop = contentElementHeight - parentContentElementHeight;
        if (scrollTop === 0) {
          this.getContentElement().scrollTop = 1;
        } else if (scrollTop == maxScrollTop) {
          this.getContentElement().scrollTop = maxScrollTop - 1;
        }
      } else {
        if (!(evt.getTarget() instanceof qx.ui.mobile.form.Input)) {
          evt.preventDefault();
        }
      }
    },


    /**
     * Mixin method. Creates the scroll element.
     *
     * @return {Element} The scroll element
     */
    _createScrollElement : function()
    {
      return null;
    },


    /**
     * Mixin method. Returns the scroll content element.
     *
     * @return {Element} The scroll content element
     */
    _getScrollContentElement : function()
    {
      return null;
    },


   /**
    * Scrolls the wrapper contents to the x/y coordinates in a given period.
    *
    * @param x {Integer} X coordinate to scroll to.
    * @param y {Integer} Y coordinate to scroll to.
    * @param time {Integer} is always <code>0</code> for this mixin.
    */
    _scrollTo : function(x, y, time) {
      this.getContentElement().scrollLeft = x;
      this.getContentElement().scrollTop = y;
    },


    /**
    * Scrolls the wrapper contents to the widgets coordinates in a given
    * period.
    *
    * @param elementId {String} the elementId, the scroll container should scroll to.
    * @param time {Integer} Time slice in which scrolling should
    *              be done (in seconds).
    */
    _scrollToElement : function(elementId, time)
    {
      var targetElement = document.getElementById(elementId);
      var offsetParent = qx.bom.element.Location.getOffsetParent(targetElement);
      var location = qx.bom.element.Location.getRelative(offsetParent, targetElement, "scroll", "scroll");

      this._scrollTo(Math.abs(location.left), Math.abs(location.top), time);
    }
  }
});
/**
 * The mixin contains all functionality to provide methods
 * for form elements to manipulate their state. [usually "valid" and "invalid"]
 *
 */
qx.Mixin.define("qx.ui.mobile.form.MState",
{

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  /**
   * The states of the element
   */
  __states: null,

  /**
   * Adds a state to the element
   * @param state {String} the state to be added
   *
   */
    addState : function(state) {
      if(this.__states === null) {
        this.__states = {};
      }
      this.__states[state] = true;
      this.addCssClass(state);
    },

    /**
     * Checkes whether the element has the state passed as argument
     * @param state {String} the state to be checked
     * @return {Boolean} true if the element has the state, false if it doesn't.
     *
     */
    hasState : function(state) {
      return this.__states!==null && this.__states[state] ;
    },

    /**
     * Removes a state from the element
     * @param state {String} the state to be removed
     *
     */
    removeState : function(state) {
      if(this.hasState(state)) {
        delete this.__states[state];
        this.removeCssClass(state);
      }
    },

    /**
     * Replaces a state of the element with a new state.
     * If the element doesn't have the state to be removed, then th new state will
     * just be added.
     * @param oldState {String} the state to be replaced
     * @param newState {String} the state to get injected in the oldState's place
     *
     */
    replaceState : function(oldState, newState) {
      if(this.hasState(oldState))
      {
        delete this.__states[oldState];
        this.__states[newState] = true;
        this.removeCssClass(oldState);
        this.addCssClass(newState);
      }
      else
      {
        this.addState(newState);
      }
    }

  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2009 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Martin Wittemann (martinwittemann)

************************************************************************ */

/**
 * Mixin handling the valid and required properties for the form widgets.
 */
qx.Mixin.define("qx.ui.form.MForm",
{

  construct : function()
  {
    if (qx.core.Environment.get("qx.dynlocale")) {
      qx.locale.Manager.getInstance().addListener("changeLocale", this.__onChangeLocale, this);
    }
  },


  properties : {

    /**
     * Flag signaling if a widget is valid. If a widget is invalid, an invalid
     * state will be set.
     */
    valid : {
      check : "Boolean",
      init : true,
      apply : "_applyValid",
      event : "changeValid"
    },


    /**
     * Flag signaling if a widget is required.
     */
    required : {
      check : "Boolean",
      init : false,
      event : "changeRequired"
    },


    /**
     * Message which is shown in an invalid tooltip.
     */
    invalidMessage : {
      check : "String",
      init: "",
      event : "changeInvalidMessage"
    },


    /**
     * Message which is shown in an invalid tooltip if the {@link #required} is
     * set to true.
     */
    requiredInvalidMessage : {
      check : "String",
      nullable : true,
      event : "changeInvalidMessage"
    }
  },


  members : {
    // apply method
    _applyValid: function(value, old) {
      value ? this.removeState("invalid") : this.addState("invalid");
    },


    /**
     * Locale change event handler
     *
     * @signature function(e)
     * @param e {Event} the change event
     */
    __onChangeLocale : qx.core.Environment.select("qx.dynlocale",
    {
      "true" : function(e)
      {
        // invalid message
        var invalidMessage = this.getInvalidMessage();
        if (invalidMessage && invalidMessage.translate) {
          this.setInvalidMessage(invalidMessage.translate());
        }
        // required invalid message
        var requiredInvalidMessage = this.getRequiredInvalidMessage();
        if (requiredInvalidMessage && requiredInvalidMessage.translate) {
          this.setRequiredInvalidMessage(requiredInvalidMessage.translate());
        }
      },

      "false" : null
    })
  },


  destruct : function()
  {
    if (qx.core.Environment.get("qx.dynlocale")) {
      qx.locale.Manager.getInstance().removeListener("changeLocale", this.__onChangeLocale, this);
    }
  }
});
/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2009 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Martin Wittemann (martinwittemann)

************************************************************************ */
/**
 * Can be included for implementing {@link qx.ui.form.IModel}. It only contains
 * a nullable property named 'model' with a 'changeModel' event.
 */
qx.Mixin.define("qx.ui.form.MModelProperty",
{
  properties :
  {
    /**
     * Model property for storing additional information for the including
     * object. It can act as value property on form items for example.
     *
     * Be careful using that property as this is used for the
     * {@link qx.ui.form.MModelSelection} it has some restrictions:
     *
     * * Don't use equal models in one widget using the
     *     {@link qx.ui.form.MModelSelection}.
     *
     * * Avoid setting only some model properties if the widgets are added to
     *     a {@link qx.ui.form.MModelSelection} widge.
     *
     * Both restrictions result of the fact, that the set models are deputies
     * for their widget.
     */
    model :
    {
      nullable : true,
      event : "changeModel",
      apply : "_applyModel",
      dereference : true
    }
  },


  members :
  {
    // apply method
    _applyModel : function(value, old) {
      // Empty implementation
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
     * Martin Wittemann (martinwittemann)

************************************************************************ */

/**
 * Form interface for all form widgets. It includes the API for enabled,
 * required and valid states.
 */
qx.Interface.define("qx.ui.form.IForm",
{
  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /** Fired when the enabled state was modified */
    "changeEnabled" : "qx.event.type.Data",

    /** Fired when the valid state was modified */
    "changeValid" : "qx.event.type.Data",

    /** Fired when the invalidMessage was modified */
    "changeInvalidMessage" : "qx.event.type.Data",

    /** Fired when the required was modified */
    "changeRequired" : "qx.event.type.Data"
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /*
    ---------------------------------------------------------------------------
      ENABLED PROPERTY
    ---------------------------------------------------------------------------
    */

    /**
     * Set the enabled state of the widget.
     *
     * @param enabled {Boolean} The enabled state.
     */
    setEnabled : function(enabled) {
      return arguments.length == 1;
    },


    /**
     * Return the current set enabled state.
     *
     * @return {Boolean} If the widget is enabled.
     */
    getEnabled : function() {},


    /*
    ---------------------------------------------------------------------------
      REQUIRED PROPERTY
    ---------------------------------------------------------------------------
    */

    /**
     * Sets the required state of a widget.
     *
     * @param required {Boolean} A flag signaling if the widget is required.
     */
    setRequired : function(required) {
      return arguments.length == 1;
    },


    /**
     * Return the current required state of the widget.
     *
     * @return {Boolean} True, if the widget is required.
     */
    getRequired : function() {},


    /*
    ---------------------------------------------------------------------------
      VALID PROPERTY
    ---------------------------------------------------------------------------
    */

    /**
     * Sets the valid state of the widget.
     *
     * @param valid {Boolean} The valid state of the widget.
     */
    setValid : function(valid) {
      return arguments.length == 1;
    },


    /**
     * Returns the valid state of the widget.
     *
     * @return {Boolean} If the state of the widget is valid.
     */
    getValid : function() {},


    /*
    ---------------------------------------------------------------------------
      INVALID MESSAGE PROPERTY
    ---------------------------------------------------------------------------
    */

    /**
     * Sets the invalid message of the widget.
     *
     * @param message {String} The invalid message.
     */
    setInvalidMessage : function(message) {
      return arguments.length == 1;
    },


    /**
     * Returns the invalid message of the widget.
     *
     * @return {String} The current set message.
     */
    getInvalidMessage : function() {},



    /*
    ---------------------------------------------------------------------------
      REQUIRED INVALID MESSAGE PROPERTY
    ---------------------------------------------------------------------------
    */

    /**
     * Sets the invalid message if required of the widget.
     *
     * @param message {String} The invalid message.
     */
    setRequiredInvalidMessage : function(message) {
      return arguments.length == 1;
    },


    /**
     * Returns the invalid message if required of the widget.
     *
     * @return {String} The current set message.
     */
    getRequiredInvalidMessage : function() {}

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
     * Martin Wittemann (martinwittemann)

************************************************************************ */

/**
 * Each object which wants to store data representative for the real item
 * should implement this interface.
 */
qx.Interface.define("qx.ui.form.IModel",
{

  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /** Fired when the model data changes */
    "changeModel" : "qx.event.type.Data"
  },



  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * Set the representative data for the item.
     *
     * @param value {var} The data.
     */
    setModel : function(value) {},


    /**
     * Returns the representative data for the item
     *
     * @return {var} The data.
     */
    getModel : function() {},


    /**
     * Sets the representative data to null.
     */
    resetModel : function() {}
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
 * Abstract class for all input fields.
 */
qx.Class.define("qx.ui.mobile.form.Input",
{
  extend : qx.ui.mobile.core.Widget,
  include : [
    qx.ui.form.MForm,
    qx.ui.form.MModelProperty,
    qx.ui.mobile.form.MState
  ],
  implement : [
    qx.ui.form.IForm,
    qx.ui.form.IModel
  ],
  type : "abstract",


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);
    this._setAttribute("type", this._getType());

    // BUG #7756
    if(qx.core.Environment.get("os.name") == "ios") {
      this.addListener("blur", this._onBlur, this);
      this.addListener("focus", this._onFocus, this);
    }
  },


  statics:
  {
    SCROLL_TIMER_ID : null
  },


  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // overridden
    _getTagName : function()
    {
      return "input";
    },


    /**
     * Returns the type of the input field. Override this method in the
     * specialized input class.
     */
    _getType : function()
    {
      if (qx.core.Environment.get("qx.debug")) {
        throw new Error("Abstract method call");
      }
    },


    /**
     * Handles the focus event on this input.
     */
    _onFocus: function() {
      clearTimeout(qx.ui.mobile.form.Input.SCROLL_TIMER_ID);
    },


    /**
     * Handles the blur event on this input.
     */
    _onBlur: function() {
      qx.ui.mobile.form.Input.SCROLL_TIMER_ID = setTimeout(function() {
        window.scrollTo(0, 0);
      }, 150);
    }
  },


  destruct : function() {
    // BUG #7756
    if (qx.core.Environment.get("os.name") == "ios") {
      this.removeListener("focus", this._onFocus, this);
      this.removeListener("blur", this._onBlur, this);
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
 * Container, which allows, depending on the set variant <code>qx.mobile.nativescroll</code>,
 * vertical and horizontal scrolling if the contents is larger than the container.
 *
 * Note that this class can only have one child widget. This container has a
 * fixed layout, which cannot be changed.
 *
 * *Example*
 *
 * Here is a little example of how to use the widget.
 *
 * <pre class='javascript'>
 *   // create the scroll widget
 *   var scroll = new qx.ui.mobile.container.Scroll()
 *
 *   // add a children
 *   scroll.add(new qx.ui.mobile.basic.Label("Name: "));
 *
 *   this.getRoot().add(scroll);
 * </pre>
 *
 * This example creates a scroll container and adds a label to it.
 */
qx.Class.define("qx.ui.mobile.container.Scroll",
{
  extend : qx.ui.mobile.container.Composite,


  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
  * @param scrollProperties {Object} A map with scroll properties which are passed to the scrolling container (may contain iScroll properties).
  */
  construct : function(scrollProperties)
  {
    this.base(arguments);

    if(scrollProperties) {
      this._scrollProperties = scrollProperties;
    }
  },


  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events :
  {
    /** Fired when the user scrolls to the end of scroll area. */
    pageEnd : "qx.event.type.Event"
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
      init : "scroll"
    }
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _scrollProperties : null,


    // overridden
    _createContainerElement : function()
    {
      var element = this.base(arguments);
      var scrollElement = this._createScrollElement();
      if (scrollElement) {
        element.appendChild(scrollElement);
      }

      return element;
    },


    // overridden
    _getContentElement : function()
    {
      var contentElement = this.base(arguments);

      var scrollContentElement = this._getScrollContentElement();

      return scrollContentElement || contentElement;
    },


    /**
     * Calls the refresh function the used scrolling method. Needed to recalculate the
     * scrolling container.
     */
    refresh : function() {
      if (qx.core.Environment.get("qx.mobile.nativescroll") == false)
      {
        this._refresh();
      }
    },


    /**
     * Scrolls the wrapper contents to the x/y coordinates in a given time.
     *
     * @param x {Integer} X coordinate to scroll to.
     * @param y {Integer} Y coordinate to scroll to.
     * @param time {Integer} Time slice in which scrolling should
     *              be done.
     */
     scrollTo : function(x, y, time)
     {
       this._scrollTo(x, y, time);
     },


    /**
      * Scrolls the wrapper contents to the widgets coordinates in a given
      * period.
      *
      * @param elementId {String} the elementId, the scroll container should scroll to.
      * @param time {Integer?0} Time slice in which scrolling should
      *              be done (in seconds).
      *
      */
     scrollToElement : function(elementId, time)
     {
       this._scrollToElement(elementId, time);
     }
  },

  /*
  *****************************************************************************
     DEFER
  *****************************************************************************
  */

  defer : function(statics)
  {
    if (qx.core.Environment.get("qx.mobile.nativescroll") == false)
    {
      qx.Class.include(statics, qx.ui.mobile.container.MIScroll);
    } else {
      qx.Class.include(statics, qx.ui.mobile.container.MNativeScroll);
    }
  }
});
