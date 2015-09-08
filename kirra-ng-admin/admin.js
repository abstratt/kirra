
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

var buildReferenceField = function(nga, relationship) {
    var appField = nga.field(relationship.name, 'reference').label(relationship.label);
    var targetEntity = entities[relationship.typeRef.fullName];
    var targetProperty = targetEntity.properties[targetEntity.mnemonicProperty];
    appField
        .targetEntity(nga.entity(relationship.typeRef.fullName))
        .targetField(nga.field(targetProperty.name));
    return appField;
};


var buildReferenceShorthandField = function(nga, relationship) {
    var appShorthandField = nga.field("_" + relationship.name + "_shorthand", 'string').label(relationship.label);
    return appShorthandField;
};

var kirraModule;

var buildActionDirective = function(operation) {
    var directive = function($state) {
        return {
            restrict: 'E',
            scope: {
                entity: '&',
                entry: '&',
                size: '@',
                label: '@',
            },
            link: function ($scope) {
                console.log(operation.name + " is disabled: " + $scope.entry().values[getActionDisabledField(operation.name)]);
                $scope.entry = $scope.entry();
            },            
	        template: "<a ng-hide=\"entry.values."+ getActionDisabledField(operation.name) + "\" class=\"btn btn-default btn-xs\" ng-class='btn-xs'>" + operation.label + "</a>"
        };        
    };
    directive.$inject = ['$state'];
    kirraModule.directive('kirra' + capitalize(operation.name), directive);
};

var capitalize = function(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
};

var buildActionDirectives = function(entity) {
    angular.forEach(entity.operations, function (operation) {
        if (operation.instanceOperation && operation.kind == 'Action') {
            buildActionDirective(operation);
        }
    });
};

var buildEntity = function(nga, adminApp, appMenu, entity) {
    var appEntity = nga.entity(entity.fullName);
    appEntity.url(function(entityName, viewType, identifierValue, identifierName) {
        return entity.extentUri + (identifierValue || '');
    });
    var listFields = [];
    for (var propertyName in entity.properties) {
        var property = entity.properties[propertyName];
        if (property.userVisible) {
            listFields.push(buildField(nga, property));
        }
    }
    
    var appReferences = [];
    for (var relationshipName in entity.relationships) {
        var relationship = entity.relationships[relationshipName];
        if (!relationship.multiple) {
            //appFields.push(buildReferenceField(nga, relationship));
            listFields.push(buildReferenceShorthandField(nga, relationship));            
        }
    }

    var actions = ['edit', 'show'];
    angular.forEach(entity.operations, function (operation) {
        if (operation.instanceOperation && operation.kind == 'Action') {
			var directiveName = 'kirra-' + operation.name.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
		    actions.push('<' + directiveName + ' entry="entry" entity="entity" size="xs"></' + directiveName + '>');
		    var enablementField = nga.field(getActionDisabledField(operation.name), 'boolean');        
        }
    });
    
    appEntity.listView()
        .title(entity.label)    
        .fields(listFields)
        .listActions(actions);
        
    appEntity.editionView()
        .title('Edit ' + entity.label)    
        .fields(listFields);
        
    appEntity.showView()
        .title('Showing ' + entity.label)    
        .fields(listFields);
        
    adminApp.addEntity(appEntity);
    appMenu.addChild(nga.menu(appEntity).title(entity.label));
};

var getActionDisabledField = function(operationName) {
    return "_" + operationName + "Disabled"
}; 

var buildEntities = function (nga, adminApp, entities) {
    var appMenu = nga.menu();
    for (var name in entities) {
        if (entities[name].concrete && entities[name].topLevel) {    
            buildEntity(nga, adminApp, appMenu, entities[name]);
        }
    }
    adminApp.menu(appMenu);
};

var requestInterceptor = function (RestangularProvider) {
    RestangularProvider.addFullRequestInterceptor(function(element, operation, what, url, headers, params) {	
        if(operation == 'post' || operation == 'put') {
            delete element.id;
            for (var propertyName in element) {
                if (propertyName.startsWith('_')) {
                    delete element[propertyName];
                }
            }
            element = { values: element };
            return { element: element };
        }
        // remove parameters
        return { params: {} } ;
    });
};

var responseInterceptor = function (RestangularProvider) {
    RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response) {
        var mapped;
        if (operation == 'getList') {
            var instances = data.contents;
            mapped = [];
            for (var i = 0;i < instances.length;i++) {
		        mapped.push(mapInstance(instances[i]));
		    }
        } else if (operation == 'get' || operation == 'put') {
            mapped = mapInstance(data);
        } else if (operation == 'delete') {
            mapped = {};
        }
        return mapped;
    });
};

var mapInstance = function(original) {
    var mapped = angular.copy(original.values);
    mapped.id = original.objectId;
    angular.forEach(original.links, function(value, key) {
        mapped["_"+ key + "_shorthand"] = value ? value[0].shorthand : null;
    });
    angular.forEach(original.disabledActions, function(value, key) {
        mapped[getActionDisabledField(key)] = true;
    });
    console.log("Mapped:");
    console.log(mapped);
    return mapped;
}

var repository = kirra.newRepository(applicationUrl);
var application;
var entities;

repository.loadApplication(function(loadedApp) {
    application = loadedApp;
    repository.loadEntities(function(loadedEntities) {
        entities = {};
        kirraModule = angular.module('kirraModule', ['ng-admin']);
        angular.forEach(loadedEntities, function (it) {
            entities[it.fullName] = it;
            // directives must be declared before bootstrapping 
            buildActionDirectives(it);
        }); 
        configureApp();
        angular.element(document).ready(function() {
	      angular.bootstrap(document, ['kirraModule']);
	    });
    });
});
        
var configureApp = function() {
	kirraModule.config(['NgAdminConfigurationProvider', function (nga) {
	    var adminApp = nga.application(application.applicationName).baseApiUrl(applicationUrl + 'entities/');
	    document.title = application.applicationName;
	    buildEntities(nga, adminApp, entities);
	    nga.configure(adminApp);
	}]);
	
	kirraModule.config(['RestangularProvider', requestInterceptor]);
	kirraModule.config(['RestangularProvider', responseInterceptor]);
};
        





