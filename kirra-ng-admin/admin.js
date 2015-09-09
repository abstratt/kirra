
var uriMatches = window.location.search.match("[?&]?app-uri\=(.*)\&?");
var pathMatches = window.location.search.match("[?&]?app-path\=(.*)\&?");
if (!uriMatches && !pathMatches) {
     throw Error("You must specify an application URI or path (same server) using the app-uri or app-path query parameters, like '...?app-uri=http://myserver.com/myapp/rest/' or '...?app-path=/myapp/rest/'.");
}
var applicationUrl = uriMatches ? uriMatches[1] : (window.location.origin + pathMatches[1]);
if (!applicationUrl.endsWith('/')) applicationUrl = applicationUrl + '/';

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

var kirraModule;

var buildActionDirective = function(operation) {
    var directive = function($http, $state, notification) {
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
                $scope.entity = $scope.entity();
                $scope[operation.name + "Handler"] = function () {
                    console.log("Executing "+ operation.name + " on ");
                    console.log($scope.entry);
                    console.log($scope.entity);
                    console.log($http);
                    $http.post(applicationUrl + 'entities/' + operation.owner.fullName + '/instances/' + $scope.entry.values.id + "/actions/" + operation.name, {})
	                    .then(() => notification.log('Action performed: ' + operation.label, { addnCls: 'humane-flatty-success' }) )
	                    .catch(e => notification.log('A problem occurred: ' + e.data.message, { addnCls: 'humane-flatty-error' }) && console.error(e) )
	                    .finally(() => $state.go($state.current.name, $state.params, { reload: true }));
                };
            },            
	        template: "<a ng-hide=\"entry.values."+ getActionDisabledField(operation.name) + "\" class=\"btn btn-default btn-xs\" ng-class='btn-xs' ng-click=\"" + operation.name + "Handler()\">" + operation.label + "</a>"
        };        
    };
    directive.$inject = ['$http', '$state', 'notification'];
    kirraModule.directive('kirra' + capitalize(operation.name), directive);
};

var capitalize = function(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
};


var getActionDisabledField = function(operationName) {
    return "_" + operationName + "Disabled"
}; 

var buildUrlResolver = function(entity) {
    return function(entityName, viewType, identifierValue, identifierName) {
        var computedUrl = entity.extentUri + (identifierValue || '');
        console.log('URL for '+ entityName + '/' + identifierValue + ' is: ' + computedUrl); 
        return computedUrl;
    };
};

var buildActionDirectives = function(entity) {
    angular.forEach(entity.operations, function (operation) {
        if (operation.instanceOperation && operation.kind == 'Action') {
            buildActionDirective(operation);
        }
    });
};

var buildField = function(nga, property, readonly) {
    var appField = nga.field(property.name, toAppTypeName(property)).label(property.label);
    appField.editable(!readonly);
    if (property.required && !property.hasDefault) {
        appField.validation({required: true});
    }
    //appField.attributes({ placeholder: property.description });
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

var buildReferenceField = function(nga, relationship, readonly) {
    if (readonly) {
        return buildReferenceShorthandField(nga, relationship);
    }
    var appField = nga.field(relationship.name, 'reference').label(relationship.label);
    if (relationship.required && !relationship.hasDefault) {
        appField.validation({required: true});
    }
    var targetEntity = entities[relationship.typeRef.fullName];
    var targetProperty = targetEntity.properties[targetEntity.mnemonicProperty];
    appField
        .targetEntity(nga.entity(relationship.typeRef.fullName).url(buildUrlResolver(targetEntity)))
        .targetField(nga.field(targetProperty.name));
    return appField;
};


var buildReferenceShorthandField = function(nga, relationship) {
    var appShorthandField = nga.field("_" + relationship.name + "_shorthand", 'string')
    	.label(relationship.label)
    	.editable(false);
    return appShorthandField;
};

var buildEntity = function(nga, adminApp, appMenu, entity) {
    var appEntity = nga.entity(entity.fullName).url(buildUrlResolver(entity));
    var listFields = [];
    var creationFields = [];
    var editFields = [];
    var showFields = [];
    for (var propertyName in entity.properties) {
        var property = entity.properties[propertyName];
        if (property.userVisible) {
            if (property.typeRef.typeName !== 'Memo' || property.typeRef.kind !== 'Primitive') { 
	            var newListField = buildField(nga, property, true);
	            if (property.unique) {
				    newListField.isDetailLink(true);
				    newListField.detailLinkRoute('show');    
	            }
            }
            listFields.push(newListField);
            editFields.push(buildField(nga, property, !property.editable));
            showFields.push(buildField(nga, property, true));
            if (property.initializable) {
                creationFields.push(buildField(nga, property, false));
            }
        }
    }
    for (var relationshipName in entity.relationships) {
        var relationship = entity.relationships[relationshipName];
        if (!relationship.multiple && relationship.userVisible) {
            listFields.push(buildReferenceField(nga, relationship, true));
            editFields.push(buildReferenceField(nga, relationship, !relationship.editable));
            showFields.push(buildReferenceField(nga, relationship, true));
            if (relationship.initializable) {
                creationFields.push(buildReferenceField(nga, relationship, false));
            }
        }
    }

    var actions = [];
    angular.forEach(entity.operations, function (operation) {
        if (operation.instanceOperation && operation.kind == 'Action') {
			var directiveName = 'kirra-' + operation.name.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
		    actions.push('<' + directiveName + ' entry="entry" entity="entity" size="xs"></' + directiveName + '>');
		    var enablementField = nga.field(getActionDisabledField(operation.name), 'boolean');        
        }
    });
    
    listFields[0].isDetailLink(true);
    listFields[0].detailLinkRoute('show');    
    appEntity.listView()
        .title(entity.label)    
        .fields(listFields)
        .listActions(actions);

    if (entity.instantiable) {
	    appEntity.creationView()
	        .title('Create ' + entity.label)
	        .description(entity.description)
	        .fields(creationFields);
    }
        
    appEntity.editionView()
        .title('Edit ' + entity.label + ': {{ entry.values.' + entity.mnemonicProperty + '}}')
        .description(entity.description)
        .fields(editFields);
        
    appEntity.showView()
        .title(entity.label + ': {{ entry.values.' + entity.mnemonicProperty + '}}')
        .fields(showFields);
        
    adminApp.addEntity(appEntity);
    appMenu.addChild(nga.menu(appEntity).title(entity.label));
};

var buildEntities = function (nga, adminApp, entities) {
    var appMenu = nga.menu();
    
    // now go ahead and build out the UI
    for (var name in entities) {
        var entity = entities[name];
        if (entity.concrete && entity.topLevel) {    
            buildEntity(nga, adminApp, appMenu, entity);
        }
    }
    adminApp.menu(appMenu);
};

var registerInterceptors = function (RestangularProvider) {
    RestangularProvider.addFullRequestInterceptor(function(element, operation, what, url, headers, params) {
        var entity = entities[what];
        if(operation == 'post' || operation == 'put') {
            return { element: fromInternalToExternal(entity, element) };
        }
        if(operation == 'getList' && url.endsWith(what)) {
            url = url + '/instances/';
        } 	
        
        // remove parameters
        return { params: {}, url: url } ;
    });

    RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response) {
        var entity = entities[what];
        var mapped;
        if (operation == 'getList') {
            var instances = data.contents;
            mapped = [];
            for (var i = 0;i < instances.length;i++) {
		        mapped.push(fromExternalToInternal(entity, instances[i]));
		    }
        } else if (operation == 'get' || operation == 'put' || operation == 'post') {
            mapped = fromExternalToInternal(entity, data);
        } else if (operation == 'delete') {
            mapped = {};
        }
        return mapped;
    });
    
    RestangularProvider.setErrorInterceptor(function(response, deferred, responseHandler) {
        response.data = response.data.message;
	    return true;
	});
};

var fromInternalToExternal = function(entity, internal) {
    var mapped = { values: {}, links: {}};
    angular.forEach(entity.properties, function(property, name) {
        if (internal[name]) {
            mapped.values[name] = internal[name];
        }
    });
    angular.forEach(entity.relationships, function(relationship, name) {
        if (!relationship.multiple) {
            if (internal[name]) {
                mapped.links[name] = [{ 
                	objectId: internal[name],
                	scopeName: relationship.typeRef.typeName,
                	scopeNamespace: relationship.typeRef.entityNamespace 
            	}]; 
            }
        }
    });
    console.log("Mapped to external:");
    console.log(mapped);
    return mapped;
}

var fromExternalToInternal = function(entity, external) {
    var mapped = {};
    angular.forEach(entity.properties, function(property, name) {
        mapped[name] = external.values[name];
    });
    angular.forEach(entity.relationships, function(relationship, name) {
        if (!relationship.multiple) {
            var link = external.links[name];
            if (link) {
                mapped[name] = link[0].objectId;
                mapped["_"+ name + "_shorthand"] = link[0].shorthand;
            }
        }
    });
    mapped.id = external.objectId;
    angular.forEach(external.disabledActions, function(value, key) {
        mapped[getActionDisabledField(key)] = true;
    });
    console.log("Mapped to internal:");
    console.log(mapped);
    return mapped;
};

var repository = kirra.newRepository(applicationUrl);
var application;
var entities;

repository.loadApplication(function(loadedApp) {
    application = loadedApp;
    repository.loadEntities(function(loadedEntities) {
        entities = {};
        kirraModule = angular.module('kirraModule', ['ng-admin']);
	        	
	    kirraModule.config(['RestangularProvider', registerInterceptors]);
        
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
};