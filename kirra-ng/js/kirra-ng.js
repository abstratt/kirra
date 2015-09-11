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

kirraNG.generateHtmlName = function(camelCase) {
	return camelCase.replace(/([a-z])([A-Z])/g, '$1-$2').replace('.', '-').toLowerCase();
}

kirraNG.generateInstanceListControllerName = function(entity) {
    return entity.fullName + 'InstanceListCtrl';
};

kirraNG.generateEntityServiceName = function(entity) {
    return entity.fullName + 'Service';
};

kirraNG.generateEntityStateName = function(entity) {
    return entity.fullName.replace('.', ':');
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
    var fieldValues = {};
    angular.forEach(entity.properties, function(property) {
        if (property.userVisible && instance.values[property.name] !== undefined) {
        	fieldValues[property.name] = instance.values[property.name];
    	}
    });
    angular.forEach(entity.relationships, function(relationship) {
        if (relationship.userVisible && !relationship.multiple && instance.links[relationship.name]) {
            fieldValues[relationship.name] = {
            	shorthand: instance.links[relationship.name][0].shorthand,
            	objectId: instance.links[relationship.name][0].objectId
        	};
    	}
    });
    return fieldValues;
};

kirraNG.buildInstanceListController = function() {
    var controller = function($scope, $stateParams, instanceService) {
        var entity = entitiesByName[$stateParams.entityName];
        $scope.entity = entity;
        $scope.entityName = $stateParams.entityName;
        $scope.tableProperties = kirraNG.buildTableColumns(entity);
        instanceService.query(entity).then(function(instances) { 
        	$scope.instances = instances;
        	var rows = [];
        	angular.forEach(instances, function(instance){
        	    var row = { data: {}, raw: instance };
        	    angular.forEach(instance.values, function(value, name) {
        	        row.data[name] = value;
        	    });
        	    angular.forEach(instance.links, function(value, name) {
        	        if (value.length > 0) {
        	            row.data[name] = value[0].shorthand;
        	        }
        	    });
                rows.push(row);
            });
            $scope.rows = rows;
    	});
    };
    controller.$inject = ['$scope', '$stateParams', 'instanceService'];
    return controller;
};

kirraNG.buildInstanceShowCtrlController = function() {
    var controller = function($scope, $stateParams, instanceService) {
        var entity = entitiesByName[$stateParams.entityName];
        var objectId = $stateParams.objectId;
        $scope.entity = entity;
        $scope.entityName = $stateParams.entityName;
        $scope.viewFields = kirraNG.buildViewFields(entity);
        instanceService.get(entity, objectId).then(function(instance) { 
        	$scope.raw = instance;
        	$scope.fieldValues = kirraNG.buildFieldValues(entity, instance);
    	}, function(error) {
    	    console.log("Received an error");
    	    console.log(error);
    	    $scope.error = error;
    	});
    };
    controller.$inject = ['$scope', '$stateParams', 'instanceService'];
    return controller;
};


kirraNG.buildInstanceService = function() {
    var serviceFactory = function($rootScope, $http) {
        var Instance = function (data) {
            angular.extend(this, data);
        };	
        Instance.query = function (entity) {
            var extentUri = entity.extentUri;
	        return $http.get(extentUri).then(function (response) {
	            var instances = [];
	            angular.forEach(response.data.contents, function(data){
	                instances.push(new Instance(data));
	            });
	            return instances;
            });
	    };
	    Instance.get = function (entity, objectId) {
            var instanceUri = entity.instanceUriTemplate.replace('(objectId)', objectId);
	        return $http.get(instanceUri).then(function (response) {
	            return new Instance(response.data);
            });
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
        });
        
        kirraModule.controller('InstanceListCtrl', kirraNG.buildInstanceListController());
        kirraModule.controller('InstanceShowCtrl', kirraNG.buildInstanceShowCtrlController());
        kirraModule.factory('instanceService', kirraNG.buildInstanceService());
        
        kirraModule.config(function($stateProvider, $urlRouterProvider) {
            var first = entityNames.find(function(name) { return entitiesByName[name].topLevel });
            $urlRouterProvider.otherwise("/" + first + "/");
	        $stateProvider.state('list', {
                url: "/:entityName/",
                controller: 'InstanceListCtrl',
                templateUrl: 'templates/instance-list.html',
                params: { entityName: { value: undefined } }
            });
	        $stateProvider.state('show', {
                url: "/:entityName/:objectId/show",
                controller: 'InstanceShowCtrl',
                templateUrl: 'templates/show-instance.html',
                params: { entityName: { value: undefined }, objectId: { value: undefined } }
            });
        });
        
        angular.element(document).ready(function() {
	      angular.bootstrap(document, ['kirraModule']);
	    });
    });
});	        	
