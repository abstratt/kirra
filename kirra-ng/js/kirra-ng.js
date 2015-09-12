var uriMatches = window.location.search.match("[?&]?app-uri\=(.*)\&?");
var pathMatches = window.location.search.match("[?&]?app-path\=(.*)\&?");
if (!uriMatches && !pathMatches) {
     throw Error("You must specify an application URI or path (same server) using the app-uri or app-path query parameters, like '...?app-uri=http://myserver.com/myapp/rest/' or '...?app-path=/myapp/rest/'.");
}
var applicationUrl = uriMatches ? uriMatches[1] : (window.location.origin + pathMatches[1]);
if (!applicationUrl.endsWith('/')) applicationUrl = applicationUrl + '/';

var repository = kirra.newRepository(applicationUrl);

var kirraNG = {};

var application;
var entitiesByName;
var entityNames;
var kirraModule;

kirraNG.capitalize = function(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
};

kirraNG.filter = function(arrayOrMap, filter) {
    var result = [];
    angular.forEach(arrayOrMap, function(it) {
        if (filter(it)) {
            result.push(it);
        }
    });
    return result;
};

kirraNG.generateHtmlName = function(camelCase) {
	return camelCase.replace(/([a-z])([A-Z])/g, '$1-$2').replace('.', '-').toLowerCase();
}

kirraNG.generateInstanceListControllerName = function(entity) {
    return entity.fullName + 'InstanceListCtrl';
};

kirraNG.generateEntityServiceName = function(entity) {
    return entity.fullName + 'Service';
};

kirraNG.toState = function(entityFullName, kind) {
    return entityFullName.replace('.', ':') + ":" + kind;
};

kirraNG.generateEntitySingleStateName = function(entity) {
    return entity.fullName.replace('.', ':') + ".single";
};

kirraNG.buildTableColumns = function(entity) {
    var tableColumns = [];
    angular.forEach(entity.properties, function(property) {
        if (property.userVisible && property.typeRef.typeName != 'Memo') {
        	tableColumns.push(property);
    	}
    });
    angular.forEach(entity.relationships, function(relationship) {
        if (relationship.userVisible && !relationship.multiple) {
        	tableColumns.push(relationship);
    	}
    });
    
    return tableColumns;
};

kirraNG.buildViewFields = function(entity) {
    var viewFields = [];
    angular.forEach(entity.properties, function(property) {
        if (property.userVisible) {
        	viewFields.push(property);
    	}
    });
    angular.forEach(entity.relationships, function(relationship) {
        if (relationship.userVisible && !relationship.multiple) {
        	viewFields.push(relationship);
    	}
    });
    return viewFields;
};

kirraNG.buildFieldValues = function(entity, instance) {
    // need to preserve order to allow retrieval by index
    var fieldValues = [];
    angular.forEach(entity.properties, function(property) {
        if (property.userVisible) {
        	fieldValues.push(instance.values[property.name]);
    	}
    });
    angular.forEach(entity.relationships, function(relationship) {
        if (relationship.userVisible && !relationship.multiple) {
            if (instance.links[relationship.name]) {
	            fieldValues.push({
	            	shorthand: instance.links[relationship.name][0].shorthand,
	            	objectId: instance.links[relationship.name][0].objectId
	        	});
        	} else {
        	    fieldValues.push(undefined);    
        	}
    	}
    });
    return fieldValues;
};

kirraNG.getEnabledActions = function(instance, instanceActions) {
    return kirraNG.filter(instanceActions, function(action) { 
        return instance.disabledActions[action.name] == undefined;
    });
};

kirraNG.buildRowData = function(entity, instance, instanceActions) {
    var enabledActions = kirraNG.getEnabledActions(instance, instanceActions);
    var data = {};
    angular.forEach(instance.values, function(value, name) {
        data[name] = value;
    });
    angular.forEach(instance.links, function(link, name) {
        data[name] = link.length > 0 ? {
            shorthand: link[0].shorthand,
            objectId: link[0].objectId
        } : {}
    });
    
    var row = { 
        data: data, 
        raw: instance, 
        enabledActions: enabledActions
    };
    return row;
};

kirraNG.buildTableData = function(entity, instances) {
	var rows = [];
	var instanceActions = kirraNG.getInstanceActions(entity);
	angular.forEach(instances, function(instance){
        rows.push(kirraNG.buildRowData(entity, instance, instanceActions));
    });
    return rows;
};

kirraNG.getInstanceActions = function(entity) {
    return kirraNG.filter(entity.operations, function(op) { return op.instanceOperation && op.kind == 'Action'; })
};

kirraNG.buildInstanceListController = function(entity) {
    var controller = function($scope, $state, instanceService) {
        $scope.entity = entity;
        $scope.entityName = entity.fullName;
        $scope.tableProperties = kirraNG.buildTableColumns(entity);
        $scope.actions = kirraNG.getInstanceActions(entity);
        instanceService.query(entity).then(function(instances) { 
        	$scope.instances = instances;
            $scope.rows = kirraNG.buildTableData(entity, instances);
    	});
    	$scope.performAction = function(row, action) {
    	    var objectId = row.raw.objectId;
    	    instanceService.performAction(entity, objectId, action.name).then(
    	        function() { return instanceService.get(entity, objectId); }
            ).then(
                function(instance) {
                    var newRow = kirraNG.buildRowData(entity, instance, kirraNG.getInstanceActions(entity));
                    row.data = newRow.data;
                    row.raw = newRow.raw;
                    row.enabledActions = newRow.enabledActions;
                }
            );
    	};
    };
    controller.$inject = ['$scope', '$state', 'instanceService'];
    return controller;
};

kirraNG.buildInstanceShowController = function(entity) {
    var multipleRelationships = kirraNG.filter(entity.relationships, function(rel) { return rel.multiple; });  

    var controller = function($scope, $stateParams, instanceService, $q) {
        var objectId = $stateParams.objectId;
        $scope.entity = entity;
        $scope.objectId = objectId;
        $scope.entityName = entity.fullName;
        $scope.viewFields = kirraNG.buildViewFields(entity);
    	$scope.performAction = function(action) {
    	    instanceService.performAction(entity, objectId, action.name).then(
    	        function() { return instanceService.get(entity, objectId); }
            ).then($scope.loadInstanceCallback).then($scope.loadInstanceRelatedCallback);
    	};
    	$scope.loadInstanceCallback = function(instance) { 
	    	$scope.raw = instance;
	    	$scope.enabledActions = kirraNG.getEnabledActions(instance, kirraNG.getInstanceActions(entity));
	    	$scope.fieldValues = kirraNG.buildFieldValues(entity, instance);
	    	$scope.relatedData = [];
	    	$scope.childrenData = [];
	    	return instance;
		};
    	$scope.loadInstanceRelatedCallback = function(relationship) {
    	    var relationshipTasks = [];
        	angular.forEach(multipleRelationships, function(relationship) {
        	    var edgeData = { 
        	        relationship: relationship, 
        	        relatedEntity: entitiesByName[relationship.typeRef.fullName], 
        	        rows: null 
    	        };
    	        var edgeList = relationship.style == 'CHILD' ? $scope.childrenData : $scope.relatedData;
    	        edgeList.push(edgeData);
        	    var next = instanceService.getRelated(entity, objectId, relationship.name).then(function(relatedInstances) {
        	        edgeData.rows = kirraNG.buildTableData(entity, relatedInstances);
        	    });
        	    relationshipTasks.push(next);
        	});
        	return $q.all(relationshipTasks);
        };
        instanceService.get(entity, objectId).then($scope.loadInstanceCallback).then($scope.loadInstanceRelatedCallback);
    };
    controller.$inject = ['$scope', '$stateParams', 'instanceService', '$q'];
    return controller;
};


kirraNG.buildInstanceService = function() {
    var serviceFactory = function($rootScope, $http) {
        var Instance = function (data) {
            angular.extend(this, data);
        };
        var loadOne = function (response) {
            return new Instance(response.data);
        };
        var loadMany = function (response) {
            var instances = [];
            angular.forEach(response.data.contents, function(data){
                instances.push(new Instance(data));
            });
            return instances;
        };
        Instance.performAction = function(entity, objectId, actionName) {
            return $http.post(entity.instanceActionUriTemplate.replace('(objectId)', objectId).replace('(actionName)', actionName), {});
        };
        Instance.query = function (entity) {
            var extentUri = entity.extentUri;
	        return $http.get(extentUri).then(loadMany);
	    };
	    Instance.get = function (entity, objectId) {
            var instanceUri = entity.instanceUriTemplate.replace('(objectId)', objectId);
	        return $http.get(instanceUri).then(loadOne);
	    };
	    Instance.getRelated = function (entity, objectId, relationshipName) {
            var relatedInstancesUri = entity.relatedInstancesUriTemplate.replace('(objectId)', objectId).replace('(relationshipName)', relationshipName);
	        return $http.get(relatedInstancesUri).then(loadMany);
	    };
	    return Instance;
    };
    return serviceFactory;
};

repository.loadApplication(function(loadedApp) {
    application = loadedApp;
    document.title = application.applicationName;
    repository.loadEntities(function(loadedEntities) {
        entitiesByName = {};
        entityNames = [];
        angular.forEach(loadedEntities, function(entity) {
            entitiesByName[entity.fullName] = entity;
            entityNames.push(entity.fullName);
        });
        kirraModule = angular.module('kirraModule', ['ui.bootstrap', 'ui.router']);
        
        kirraModule.controller('KirraRepositoryCtrl', function($scope) {
            $scope.applicationName = application.applicationName;
            $scope.entities = loadedEntities;
            $scope.kirraNG = kirraNG;
        });
        
        kirraModule.factory('instanceService', kirraNG.buildInstanceService());
        
        kirraModule.config(function($stateProvider, $urlRouterProvider) {
            var first = entityNames.find(function(name) { return entitiesByName[name].topLevel });
            $urlRouterProvider.otherwise("/" + first + "/");
            
            angular.forEach(entityNames, function(entityName) {
                var entity = entitiesByName[entityName];
		        $stateProvider.state(kirraNG.toState(entityName, 'list'), {
	                url: "/" + entityName + "/",
	                controller: kirraNG.buildInstanceListController(entity),
	                templateUrl: 'templates/instance-list.html'
	            });
	            $stateProvider.state(kirraNG.toState(entityName, 'show'), {
	                url: "/" + entityName + "/:objectId/show",
	                controller: kirraNG.buildInstanceShowController(entity),
	                templateUrl: 'templates/show-instance.html',
	                params: { objectId: { value: undefined } }
	            });                
            }); 
        });
        
        angular.element(document).ready(function() {
	      angular.bootstrap(document, ['kirraModule']);
	    });
    });
});	        	
