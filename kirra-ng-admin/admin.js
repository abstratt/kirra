
var uriMatches = window.location.search.match("[?&]?app-uri\=(.*)\&?");
var pathMatches = window.location.search.match("[?&]?app-path\=(.*)\&?");
if (!uriMatches && !pathMatches) {
     throw Error("You must specify an application URI or path (same server) using the app-uri or app-path query parameters, like '...?app-uri=http://myserver.com/myapp/rest/' or '...?app-path=/myapp/rest/'.");
}
var applicationUrl = uriMatches ? uriMatches[1] : (window.location.origin + pathMatches[1]);

var toAppTypeName = function(property) {
    if (property.typeRef.kind === 'Primitive') {
	    switch (property.typeRef.typeName) {
	        case 'String' : return 'string';
	        case 'Memo' : return 'text';
	        case 'Integer' : return 'number';
	        case 'Double' : return 'number';
	        case 'Date' : return 'date';
	        case 'Boolean' : return 'boolean';
	    }
	    console.log(property.name + " has unsupported type " + property.typeRef.typeName); 
    	return 'string';
	} else if (property.typeRef.kind === 'Enumeration') {
	    return 'choice';
	} else {
        console.log(property.name + " has unsupported type kind " + property.typeRef.kind);
        return null;	
	}
};

var buildField = function(nga, property) {
    var appField = nga.field(property.name, toAppTypeName(property)).label(property.label);
    if (property.typeRef.kind === 'Primitive') {
	    switch (property.typeRef.typeName) {
	        case 'Integer' : appField.format('0'); break;
	        case 'Double' : appField.format('0,0.00'); break;
	    }
    } else if (property.typeRef.kind === 'Enumeration') {
        var choices = [];
        angular.forEach(property.enumerationLiterals, function (it) {
            choices.push({ value: it, label: it});
        });
        appField.choices(choices);
    }
    return appField;
};


var buildEntity = function(nga, adminApp, appMenu, entity) {
    var appEntity = nga.entity(entity.fullName);
    appEntity.url(applicationUrl + 'entities/' + entity.fullName + '/instances/');
    var appFields = [];
    for (var propertyName in entity.properties) {
        var property = entity.properties[propertyName];
        appFields.push(buildField(nga, property));
    }
    appEntity.listView()
        .title(entity.label)    
        .fields(appFields);
    adminApp.addEntity(appEntity);
    appMenu.addChild(nga.menu(appEntity).title(entity.label));
};

var buildEntities = function (nga, adminApp, entities) {
    var appMenu = nga.menu();
    for (var i = 0;i < entities.length;i++) {
        buildEntity(nga, adminApp, appMenu, entities[i]);
    }
    adminApp.menu(appMenu);
};

var requestInterceptor = function (RestangularProvider) {
    RestangularProvider.addFullRequestInterceptor(function(element, operation, what, url, headers, params) {	
        console.log("REQUEST");
        console.log(element);
        console.log(operation);
        console.log(what);
        console.log(url);
        console.log(headers);
        console.log(params);
    
        return { params: {}, headers: headers };
    });
};

var responseInterceptor = function (RestangularProvider) {
    RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response) {
        console.log("RESPONSE");
        console.log(data);
        console.log(operation);
        console.log(what);
        console.log(url);
        var mapped;
        if (operation == "getList") {
            var instances = data.contents;
            mapped = [];
            for (var i = 0;i < instances.length;i++) {
		        mapped.push(mapInstance(instances[i]));
		    }
        } else {
            mapped = mapInstance(data);
        }
        return mapped;
    });
};

var mapInstance = function(original) {
    var mapped = angular.copy(original.values);
    mapped.id = original.objectId;
    return mapped;
}

var repository = kirra.newRepository(applicationUrl);
var application;
var entities;
repository.loadApplication(function(loaded) {
    application = loaded;
    repository.loadEntities(function(loaded) {
        entities = loaded;
        angular.element(document).ready(function() {
	      angular.bootstrap(document, ['myApp']);
	    });
    });
});

var myApp = angular.module('myApp', ['ng-admin']);        
myApp.config(['NgAdminConfigurationProvider', function (nga) {
    var loadedApp = nga.application(application.applicationName).baseApiUrl(applicationUrl + 'entities/');
    buildEntities(nga, loadedApp, entities);
    nga.configure(loadedApp);
}]);

myApp.config(['RestangularProvider', requestInterceptor]);
myApp.config(['RestangularProvider', responseInterceptor]);





