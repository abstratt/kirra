qx.Class.define("kirra_qooxdoo.Widgets", {
    extend: qx.core.Object,

      statics : {

        createWidget: function (property) {
            var kind = property.typeRef && property.typeRef.kind
            var factoryMethodName = 'create' + kind + 'Widget';
            var factory = this[factoryMethodName];
            if (typeof (factory) !== 'function') {
                console.log("No factory found for: " + property.name + " : " + kind);
                return this.createStringWidget();
            }
            return factory.call(this, property);
        },

        createPrimitiveWidget: function (property) {
            var typeName = property.typeRef && property.typeRef.typeName
            var factoryMethodName = 'create' + typeName + 'Widget';
            var factory = this[factoryMethodName];
            if (typeof (factory) !== 'function') {
                console.log("No factory found for: " + property.name + " : " + typeName);
                return this.createStringWidget();
            }
            return factory.call(this, property);
        },

        createStringWidget: function (attribute) {
            return new qx.ui.mobile.form.TextField();
        },

        createIntegerWidget: function (attribute) {
            return new qx.ui.mobile.form.TextField();
        },

        createDoubleWidget: function (attribute) {
            return new qx.ui.mobile.form.TextField();
        },

        createDateWidget: function (attribute) {
            var me = this;
            var dateField = new qx.ui.mobile.form.TextField();
            dateField.setReadOnly(true);
            var days = [];
            for (var day = 1;day <= 31;day++) {
                days.push("" + day);
            } 
            var daySlot = new qx.data.Array(days);
                    var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            var monthSlot = new qx.data.Array(months);
            var years = [];
            for (var year = 1900 + new Date().getYear();year > 1900;year--) {
                years.push("" + year);
            }
            var yearSlot = new qx.data.Array(years);
            var picker = new qx.ui.mobile.dialog.Picker();
            picker.addSlot(daySlot);
            picker.addSlot(monthSlot);
            picker.addSlot(yearSlot);

            dateField.addListener("tap", function () {
                var parsed;
                try {
                    parsed = kirra_qooxdoo.DateFormats.getYMDFormatter().parse(dateField.getValue());
                } catch (e) {
                    parsed = new Date();
                }
                var slot0 = parsed.getDate()-1;
                var slot1 = parsed.getMonth();
                var slot2 = parsed.getYear();
                picker.setSelectedIndex(0, slot0);
                picker.setSelectedIndex(1, slot1);
                picker.setSelectedIndex(2, new Date().getYear() - slot2);
                picker.show();
            });
            picker.addListener("confirmSelection", function(e) {
                var data = e.getData();
                dateField.setValue(data[2].item+'/'+(months.indexOf(data[1].item)+1)+'/'+data[0].item);
            });
    
            return dateField;
        },
    
        createMemoWidget: function (attribute) {
            return new qx.ui.mobile.form.TextArea();
        },

        createBooleanWidget: function (attribute) {
            return new qx.ui.mobile.form.ToggleButton();
        },

        createStateMachineWidget: function (attribute) {
            return new qx.ui.mobile.form.TextField();
        },

        createEnumerationWidget: function (property) {
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
