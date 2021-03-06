(function(){

if (!window.qx) window.qx = {};

qx.$$start = new Date();

if (!qx.$$environment) qx.$$environment = {};
var envinfo = {"qx.application":"kirra_qooxdoo.Application","qx.mobile.emulatetouch":true,"qx.revision":"","qx.theme":"custom","qx.version":"3.5"};
for (var k in envinfo) qx.$$environment[k] = envinfo[k];

if (!qx.$$libraries) qx.$$libraries = {};
var libinfo = {"__out__":{"sourceUri":"script"},"kirra_qooxdoo":{"resourceUri":"../source/resource","sourceUri":"../source/class"},"qx":{"resourceUri":"../../../qooxdoo/framework/source/resource","sourceUri":"../../../qooxdoo/framework/source/class","sourceViewUri":"https://github.com/qooxdoo/qooxdoo/blob/%{qxGitBranch}/framework/source/class/%{classFilePath}#L%{lineNumber}"}};
for (var k in libinfo) qx.$$libraries[k] = libinfo[k];

qx.$$resources = {};
qx.$$translations = {"C":null,"en":null};
qx.$$locales = {"C":null,"en":null};
qx.$$packageData = {};
qx.$$g = {}

qx.$$loader = {
  parts : {"boot":[0]},
  packages : {"0":{"uris":["__out__:kirra_qooxdoo.c8a398231601.js","kirra_qooxdoo:kirra_qooxdoo/Repository.js","__out__:kirra_qooxdoo.9cf39a938456.js","kirra_qooxdoo:kirra_qooxdoo/Application.js","__out__:kirra_qooxdoo.ada0b7ec3127.js","kirra_qooxdoo:kirra_qooxdoo/EntityNavigator.js","__out__:kirra_qooxdoo.7a6882f0a112.js","kirra_qooxdoo:kirra_qooxdoo/AbstractInstanceNavigator.js","kirra_qooxdoo:kirra_qooxdoo/DateFormats.js","__out__:kirra_qooxdoo.355be8b1da2b.js","kirra_qooxdoo:kirra_qooxdoo/InstanceNavigator.js","__out__:kirra_qooxdoo.3fec9d4f1476.js","kirra_qooxdoo:kirra_qooxdoo/InstanceForm.js","__out__:kirra_qooxdoo.d5f247fd5004.js","kirra_qooxdoo:kirra_qooxdoo/Widgets.js","__out__:kirra_qooxdoo.73747582b15b.js","kirra_qooxdoo:kirra_qooxdoo/RelatedInstanceNavigator.js","kirra_qooxdoo:kirra_qooxdoo/ActionForm.js","__out__:kirra_qooxdoo.6fa00af6bcdf.js"]}},
  urisBefore : [],
  cssBefore : ["./resource/kirra_qooxdoo/css/custom.css","./resource/kirra_qooxdoo/css/styles.css"],
  boot : "boot",
  closureParts : {},
  bootIsInline : false,
  addNoCacheParam : false,

  decodeUris : function(compressedUris)
  {
    var libs = qx.$$libraries;
    var uris = [];
    for (var i=0; i<compressedUris.length; i++)
    {
      var uri = compressedUris[i].split(":");
      var euri;
      if (uri.length==2 && uri[0] in libs) {
        var prefix = libs[uri[0]].sourceUri;
        euri = prefix + "/" + uri[1];
      } else {
        euri = compressedUris[i];
      }
      if (qx.$$loader.addNoCacheParam) {
        euri += "?nocache=" + Math.random();
      }
      
      uris.push(euri);
    }
    return uris;
  }
};

var readyStateValue = {"complete" : true};
if (document.documentMode && document.documentMode < 10 ||
    (typeof window.ActiveXObject !== "undefined" && !document.documentMode)) {
  readyStateValue["loaded"] = true;
}

function loadScript(uri, callback) {
  var elem = document.createElement("script");
  elem.charset = "utf-8";
  elem.src = uri;
  elem.onreadystatechange = elem.onload = function() {
    if (!this.readyState || readyStateValue[this.readyState]) {
      elem.onreadystatechange = elem.onload = null;
      if (typeof callback === "function") {
        callback();
      }
    }
  };

  if (isLoadParallel) {
    elem.async = null;
  }

  var head = document.getElementsByTagName("head")[0];
  head.appendChild(elem);
}

function loadCss(uri) {
  var elem = document.createElement("link");
  elem.rel = "stylesheet";
  elem.type = "text/css";
  elem.href = uri;
  elem.onload = onLoadCss();
  var head = document.getElementsByTagName("head")[0];
  head.appendChild(elem);
}

function onLoadCss() {
  cssFilesToLoad = cssFilesToLoad - 1;
  if(cssFilesToLoad == 0) {
    setTimeout(initScripts,0);
  }
}

var isWebkit = /AppleWebKit\/([^ ]+)/.test(navigator.userAgent);
var isLoadParallel = 'async' in document.createElement('script');

function loadScriptList(list, callback) {
  if (list.length == 0) {
    callback();
    return;
  }

  var item;

  if (isLoadParallel) {
    while (list.length) {
      item = list.shift();
      if (list.length) {
        loadScript(item);
      } else {
        loadScript(item, callback);
      }
    }
  } else {
    item = list.shift();
    loadScript(item,  function() {
      if (isWebkit) {
        // force async, else Safari fails with a "maximum recursion depth exceeded"
        window.setTimeout(function() {
          loadScriptList(list, callback);
        }, 0);
      } else {
        loadScriptList(list, callback);
      }
    });
  }
}

var fireContentLoadedEvent = function() {
  qx.$$domReady = true;
  document.removeEventListener('DOMContentLoaded', fireContentLoadedEvent, false);
};
if (document.addEventListener) {
  document.addEventListener('DOMContentLoaded', fireContentLoadedEvent, false);
}

qx.$$loader.importPackageData = function (dataMap, callback) {
  if (dataMap["resources"]){
    var resMap = dataMap["resources"];
    for (var k in resMap) qx.$$resources[k] = resMap[k];
  }
  if (dataMap["locales"]){
    var locMap = dataMap["locales"];
    var qxlocs = qx.$$locales;
    for (var lang in locMap){
      if (!qxlocs[lang]) qxlocs[lang] = locMap[lang];
      else
        for (var k in locMap[lang]) qxlocs[lang][k] = locMap[lang][k];
    }
  }
  if (dataMap["translations"]){
    var trMap   = dataMap["translations"];
    var qxtrans = qx.$$translations;
    for (var lang in trMap){
      if (!qxtrans[lang]) qxtrans[lang] = trMap[lang];
      else
        for (var k in trMap[lang]) qxtrans[lang][k] = trMap[lang][k];
    }
  }
  if (callback){
    callback(dataMap);
  }
}

qx.$$loader.signalStartup = function ()
{
  qx.$$loader.scriptLoaded = true;
  if (window.qx && qx.event && qx.event.handler && qx.event.handler.Application) {
    qx.event.handler.Application.onScriptLoaded();
    qx.$$loader.applicationHandlerReady = true;
  } else {
    qx.$$loader.applicationHandlerReady = false;
  }
}


// Load all stuff

var cssFilesToLoad = 0;

qx.$$loader.init = function() {
  var l=qx.$$loader;
  if (l.cssBefore.length>0) {

    cssFilesToLoad = l.cssBefore.length;

    for (var i=0, m=l.cssBefore.length; i<m; i++) {
      loadCss(l.cssBefore[i]);
    }
  } else {
    initScripts();
  }
}

// Init scripts...
function initScripts() {
  var l=qx.$$loader;
  if (l.urisBefore.length>0){
    loadScriptList(l.urisBefore, function(){
      l.initUris();
    });
  } else {
    l.initUris();
  }
}

// Load qooxdoo boot stuff
qx.$$loader.initUris = function(){
  var l=qx.$$loader;
  var bootPackageHash=l.parts[l.boot][0];
  if (l.bootIsInline){
    l.importPackageData(qx.$$packageData[bootPackageHash]);
    l.signalStartup();
  } else {
    loadScriptList(l.decodeUris(l.packages[l.parts[l.boot][0]].uris), function(){
      // Opera needs this extra time to parse the scripts
      window.setTimeout(function(){
        l.importPackageData(qx.$$packageData[bootPackageHash] || {});
        l.signalStartup();
      }, 0);
    });
  }
}
})();



qx.$$loader.init();

