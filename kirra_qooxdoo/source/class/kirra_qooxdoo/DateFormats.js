qx.Class.define("kirra_qooxdoo.DateFormats", {
    extend: qx.core.Object,
      statics : {
        _ymdFormatter : new qx.util.format.DateFormat("yyyy/MM/dd"),
        _isoFormatter : new qx.util.format.DateFormat(qx.util.format.DateFormat.ISO_MASKS.isoUtcDateTime),
        getYMDFormatter : function () {
            var me = this;
            me._ymdFormatter.__UTC = true;
            return { 
                format : function (date) {
                    return me._ymdFormatter.format(date);    
                },
                parse : function (dateString) {
                    var parsed = me._ymdFormatter.parse(dateString);
                    return parsed;
                }
            };
        },
        getISOFormatter : function () {
            var me = this;
            return { 
                format : function (date) {
                    return me._isoFormatter.format(date);
                },
                parse : function (dateString) {
                    var parsed = new Date(Date.parse(dateString));
                    return parsed;
                }
            };
        }
    }
});
