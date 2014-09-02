var exports = module.exports = {};

exports.assert = function(condition, message) { 
    if (!condition)
        throw Error("Assert failed" + (typeof message !== "undefined" ? ": " + message : ""));
};

exports.merge = function (obj1, obj2) {
    for (var key in obj2) {
        if (obj1[key] === undefined) {
            obj1[key] = obj2[key];
        }
    }
    return obj1;
};



