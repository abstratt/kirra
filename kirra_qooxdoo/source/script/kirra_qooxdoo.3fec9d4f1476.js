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
     * Gabriel Munteanu (gabios)

************************************************************************ */

/**
 * A toolbar widget.
 *
 */
qx.Class.define("qx.ui.mobile.toolbar.ToolBar",
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
      init : "toolbar"
    }
  },

  members :
  {

    /**
      * Flag to keep the show/hidden state of the toolbar
      */
    __hidden: false,


    /**
      * Adds a new child widget.
      *
      * @param child {qx.ui.mobile.core.Widget} the widget to add.
      * @param layoutProperties {Map?null} Optional layout data for widget.
      */
    add : function(child, layoutProperties)
    {
      if(!(child instanceof qx.ui.mobile.toolbar.Separator))
      {
        layoutProperties = layoutProperties ? layoutProperties : {};
        qx.lang.Object.mergeWith(layoutProperties, {flex: 1}, false);
      }
      this.base(arguments, child, layoutProperties);
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
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Gabriel Munteanu (gabios)

************************************************************************ */

/**
 * A separator widget used to separate widgets in a toolbar.
 *
 */
qx.Class.define("qx.ui.mobile.toolbar.Separator",
{
  extend : qx.ui.mobile.core.Widget,

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
     PROPERTIES
  *****************************************************************************
  */

    properties :
    {
      // overridden
      defaultCssClass :
      {
        refine : true,
        init : "toolbar-separator"
      }
    }
});
