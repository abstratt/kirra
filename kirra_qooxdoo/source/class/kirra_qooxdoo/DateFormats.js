qx.Class.define("kirra_qooxdoo.DateFormats", {
    extend: qx.core.Object,
      statics : {
        _ymdFormatter : new qx.util.format.DateFormat("yyyy/MM/dd"),
        _isoFormatter : new qx.util.format.DateFormat("yyyy-MM-dd'T'hh:mmZ"),
        getYMDFormatter : function () {
            return this._ymdFormatter;
        },
        getISOFormatter : function () {
            return this._isoFormatter;
        }
    }
});
