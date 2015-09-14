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

kirraNG.filter = function(arrayOrMap, filter, mapping) {
    var result = [];
    mapping = mapping || function(it) { return it; };
    angular.forEach(arrayOrMap, function(it) {
        if (filter(it)) {
            result.push(mapping(it));
        }
    });
    return result;
};

kirraNG.map = function(arrayOrMap, mapping) {
    return arrayOrMap.filter(function() { return true; }, mapping);
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

kirraNG.buildInputFields = function(entity, createMode) {
    var inputFields = [];
    angular.forEach(entity.properties, function(property) {
        if (property.userVisible && ((createMode && property.initializable) || (!createMode && property.editable))) {
        	inputFields.push(property);
    	}
    });
    angular.forEach(entity.relationships, function(relationship) {
        if (relationship.userVisible && !relationship.multiple && ((createMode && relationship.initializable) || (!createMode && relationship.editable))) {
        	inputFields.push(relationship);
    	}
    });
    return inputFields;
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

kirraNG.buildFieldValues = function(instance, properties, relationships, initializable, editable) {
    // need to preserve order to allow retrieval by index
    var fieldValues = [];
    angular.forEach(properties, function(property) {
        if (property.userVisible && (!initializable || property.initializable) && (!editable || property.editable)) {
        	fieldValues.push(instance.values[property.name]);
    	}
    });
    angular.forEach(relationships, function(relationship) {
        if (relationship.userVisible && !relationship.multiple && (!initializable || relationship.initializable) && (!editable || relationship.editable)) {
            if (instance.links[relationship.name]) { 
	            fieldValues.push(instance.links[relationship.name][0].shorthand);
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

kirraNG.getEntityActions = function(entity) {
    return kirraNG.filter(entity.operations, function(op) { return !op.instanceOperation && op.kind == 'Action'; })
};


kirraNG.buildInstanceListController = function(entity) {
    var controller = function($scope, $state, instanceService) {
        $scope.entity = entity;
        $scope.entityName = entity.fullName;
        $scope.tableProperties = kirraNG.buildTableColumns(entity);
        $scope.actions = kirraNG.getInstanceActions(entity);
        $scope.instances = null;
        $scope.entityActions = kirraNG.getEntityActions(entity);
        instanceService.query(entity).then(function(instances) { 
        	$scope.instances = instances;
            $scope.rows = kirraNG.buildTableData(entity, instances);
    	});
    	$scope.performInstanceAction = function(row, action) {
    	    var objectId = row.raw.objectId;
    	    
    	    if (action.parameters.length > 0) {
    	        $state.go(kirraNG.toState(entity.fullName, 'performInstanceAction'), { objectId: objectId, actionName: action.name } );
    	        return;
    	    }
    	    
    	    instanceService.performInstanceAction(entity, objectId, action.name).then(
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
    	
    	$scope.performEntityAction = function(action) {
    	    if (action.parameters.length > 0) {
    	        $state.go(kirraNG.toState(entity.fullName, 'performEntityAction'), { actionName: action.name } );
    	        return;
    	    }
    	    
    	    instanceService.performEntityAction(entity, action.name).then(
                function() {
                    $state.go($state.current.name, $state.params, { reload: true });
                }
            );
    	};
    	
    	$scope.performInstanceAction = function(row, action) {
    	    var objectId = row.raw.objectId;
    	    
    	    if (action.parameters.length > 0) {
    	        $state.go(kirraNG.toState(entity.fullName, 'performInstanceAction'), { objectId: objectId, actionName: action.name } );
    	        return;
    	    }
    	    instanceService.performInstanceAction(entity, objectId, action.name).then(
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
    	$scope.create = function() {
    	    $state.go(kirraNG.toState(entity.fullName, 'create'));
    	};
    };
    controller.$inject = ['$scope', '$state', 'instanceService'];
    return controller;
};

kirraNG.buildInstanceEditController = function(entity, mode) {
    var creation = mode == 'create';
    var editing = mode == 'edit';
    var controller = function($scope, $state, $stateParams, instanceService, $q) {
        var objectId = $stateParams.objectId;

        $scope.objectId = objectId;
        $scope.entity = entity;
        $scope.entityName = entity.fullName;

    	$scope.loadInstanceCallback = function(instance) { 
            $scope.formLabel = creation ? ('Creating ' + entity.label) : ('Editing ' + entity.label + ': ' + instance.shorthand); 
	    	$scope.raw = instance;
	    	$scope.enabledActions = kirraNG.getEnabledActions(instance, kirraNG.getInstanceActions(entity));
	    	$scope.propertyValues = angular.copy(instance.values);
	    	$scope.linkValues = angular.copy(instance.links);
	    	return instance;
		};
    
        $scope.inputFields = kirraNG.buildInputFields(entity, false);
        
        $scope.findCandidatesFor = function(relationship, value) {
            value = value.toUpperCase();
            var relationshipDomain;
            if (creation) {
                var relatedEntity = entitiesByName[relationship.typeRef.fullName];
                relationshipDomain = instanceService.query(relatedEntity);
            } else {
                relationshipDomain = instanceService.getRelationshipDomain(entity, $scope.objectId, relationship.name)
            }
            return relationshipDomain.then(function(instances) {
                return kirraNG.filter(instances, 
                	function(it) { return it.shorthand.toUpperCase().indexOf(value) == 0; },
                	function(it) { return it; }
            	);
            });
        };
        
        $scope.onCandidateSelected =  function(selectedCandidate, inputField, $label) {
            $scope.linkValues[inputField.name] = [selectedCandidate];
        };
        
        $scope.formatCandidate = function(inputField) {
            if (!$scope.linkValues || !$scope.linkValues[inputField.name]) {
                return '';
            }
            return $scope.linkValues[inputField.name][0].shorthand;
        };
        
        $scope.cancel = function() {
            window.history.back();
    	};
    	
        $scope.save = function() {
            var newValues = angular.copy($scope.propertyValues);
            var newLinks = angular.copy($scope.linkValues);
            var newRepresentation = { objectId: $scope.objectId, values: newValues, links: newLinks };
            if (creation) {
                instanceService.post(entity, newRepresentation).then(function(created) {
                    $state.go(kirraNG.toState(entity.fullName, 'show'), { objectId: created.objectId } ); 
            	});
            } else {
                instanceService.put(entity, newRepresentation).then(function() { window.history.back(); });
            } 
    	};
        instanceService.get(entity, creation ? '_template' : objectId).then($scope.loadInstanceCallback);
    };
    controller.$inject = ['$scope', '$state', '$stateParams', 'instanceService', '$q'];
    return controller;
};

kirraNG.buildActionController = function(entity) {
    var controller = function($scope, $state, $stateParams, instanceService, $q) {
        var objectId = $stateParams.objectId;
        var actionName = $stateParams.actionName;
        var action = entity.operations[actionName];

        $scope.objectId = objectId;
        $scope.entity = entity;
        $scope.entityName = entity.fullName;
        $scope.actionName = actionName;
        $scope.action = action;
        $scope.inputFields = action.parameters;
        $scope.parameterValues = {};
        
        $scope.findCandidatesFor = function(parameter, value) {
            value = value.toUpperCase();
            return instanceService.getParameterDomain(entity, $scope.objectId, actionName, parameter.name).then(function(instances) {
                return kirraNG.filter(instances, 
                	function(it) { return it.shorthand.toUpperCase().indexOf(value) == 0; },
                	function(it) { return it; }
            	);
            });
        };
        
        $scope.onCandidateSelected =  function(selectedCandidate, inputField, $label) {
            $scope.parameterValues[inputField.name] = selectedCandidate;
        };
        
        $scope.formatCandidate = function(inputField) {
            if (!$scope.parameterValues || !$scope.parameterValues[inputField.name]) {
                return '';
            }
            return $scope.parameterValues[inputField.name].shorthand;
        };
        
        $scope.cancel = function() {
            window.history.back();
    	};
    	
        $scope.performAction = function() {
            var arguments = angular.copy($scope.parameterValues);
            var objectId = $scope.objectId;
            var actionName = $scope.actionName;
            if (objectId == undefined) {
                instanceService.performEntityAction(entity, actionName, arguments).then(function() { window.history.back(); });
            } else {
                instanceService.performInstanceAction(entity, objectId, actionName, arguments).then(function() { window.history.back(); });
            }
    	};
    };
    controller.$inject = ['$scope', '$state', '$stateParams', 'instanceService', '$q'];
    return controller;
};


kirraNG.buildInstanceShowController = function(entity) {
    var multipleRelationships = kirraNG.filter(entity.relationships, function(rel) { return rel.multiple && rel.userVisible; });

    var controller = function($scope, $state, $stateParams, instanceService, $q) {

        var objectId = $stateParams.objectId;

        $scope.objectId = objectId;

        $scope.entity = entity;

        $scope.entityName = entity.fullName;

    	$scope.loadInstanceCallback = function(instance) { 
	    	$scope.raw = instance;
	    	$scope.enabledActions = kirraNG.getEnabledActions(instance, kirraNG.getInstanceActions(entity));
	    	$scope.fieldValues = kirraNG.buildFieldValues(instance, entity.properties, entity.relationships);
	    	$scope.relatedData = [];
	    	$scope.childrenData = [];
	    	return instance;
		};
    
        $scope.viewFields = kirraNG.buildViewFields(entity);

        $scope.edit = function() {
            $state.go(kirraNG.toState(entity.fullName, 'edit'), { objectId: objectId } );
    	};

    	$scope.unlink = function(relationship, otherId) {
    	    instanceService.unlink(entity, objectId, relationship.name, otherId).then(function() {
    	        return instanceService.get(entity, objectId).then($scope.loadInstanceCallback).then($scope.loadInstanceRelatedCallback);
    	    });
    	};

    	$scope.performAction = function(action) {
    	    if (action.parameters.length > 0) {
    	        $state.go(kirraNG.toState(entity.fullName, 'performInstanceAction'), { objectId: objectId, actionName: action.name } );
    	        return;
    	    }
    	    instanceService.performInstanceAction(entity, objectId, action.name).then(
    	        function() { return instanceService.get(entity, objectId); }
            ).then($scope.loadInstanceCallback).then($scope.loadInstanceRelatedCallback);
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
    controller.$inject = ['$scope', '$state', '$stateParams', 'instanceService', '$q'];
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
        Instance.performInstanceAction = function(entity, objectId, actionName, arguments) {
            return $http.post(entity.instanceActionUriTemplate.replace('(objectId)', objectId).replace('(actionName)', actionName), arguments || {});
        };
        Instance.performEntityAction = function(entity, actionName, arguments) {
            return $http.post(entity.entityActionUriTemplate.replace('(actionName)', actionName), arguments || {});
        };
        Instance.unlink = function(entity, objectId, relationshipName, relatedObjectId) {
            return $http.delete(entity.relatedInstanceUriTemplate.replace('(objectId)', objectId).replace('(relationshipName)', relationshipName).replace('(relatedObjectId)', relatedObjectId), {});
        };
        Instance.query = function (entity) {
            var extentUri = entity.extentUri;
	        return $http.get(extentUri).then(loadMany);
	    };
	    Instance.getRelationshipDomain = function (entity, objectId, relationshipName) {
            var relationshipDomainUri = entity.relationshipDomainUriTemplate.replace('(objectId)', objectId).replace('(relationshipName)', relationshipName);
	        return $http.get(relationshipDomainUri).then(loadMany);
	    };
	    Instance.getParameterDomain = function (entity, objectId, actionName, parameterName) {
            var parameterDomainUri = entity.instanceActionParameterDomainUriTemplate.replace('(objectId)', objectId).replace('(actionName)', actionName).replace('(parameterName)', parameterName);
	        return $http.get(parameterDomainUri).then(loadMany);
	    };
	    
	    Instance.get = function (entity, objectId) {
            var instanceUri = entity.instanceUriTemplate.replace('(objectId)', objectId);
	        return $http.get(instanceUri).then(loadOne);
	    };
	    Instance.put = function (entity, instance) {
	        return $http.put(entity.instanceUriTemplate.replace('(objectId)', instance.objectId), instance).then(loadOne);
	    };
	    Instance.post = function (entity, instance) {
	        return $http.post(entity.extentUri, instance).then(loadOne);
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
        
        kirraModule.controller('TypeAheadCtrl', function($scope) {
            $scope.findCandidatesFor = function(value) {
	            value = value.toUpperCase();
	            return instanceService.getRelationshipDomain(entity, relationship.name).then(function(instances) {
	                return kirraNG.filter(instances, function(it) { return it.shorthand.toUpperCase().indexOf(value) >= 0 });
	            });
	        }; 
        });
        
        angular.forEach(entitiesByName, function(entity, entityName) {
            kirraModule.controller(entityName + 'InstanceShowCtrl', kirraNG.buildInstanceShowController(entity));
            kirraModule.controller(entityName + 'InstanceEditCtrl', kirraNG.buildInstanceEditController(entity, 'edit'));
            kirraModule.controller(entityName + 'InstanceCreateCtrl', kirraNG.buildInstanceEditController(entity, 'create'));            
            kirraModule.controller(entityName + 'ActionCtrl', kirraNG.buildActionController(entity));
            kirraModule.controller(entityName + 'InstanceListCtrl', kirraNG.buildInstanceListController(entity));        
        });
        kirraModule.factory('instanceService', kirraNG.buildInstanceService());
        
        kirraModule.config(function($stateProvider, $urlRouterProvider) {
            var first = entityNames.find(function(name) { return entitiesByName[name].topLevel });
            $urlRouterProvider.otherwise("/" + first + "/");
            
            angular.forEach(entityNames, function(entityName) {
                var entity = entitiesByName[entityName];
		        $stateProvider.state(kirraNG.toState(entityName, 'list'), {
	                url: "/" + entityName + "/",
	                controller: entityName + 'InstanceListCtrl',
	                templateUrl: 'templates/instance-list.html'
	            });
	            $stateProvider.state(kirraNG.toState(entityName, 'show'), {
	                url: "/" + entityName + "/:objectId/show",
	                controller: entityName + 'InstanceShowCtrl',
	                templateUrl: 'templates/show-instance.html',
	                params: { objectId: { value: undefined } }
	            });
	            $stateProvider.state(kirraNG.toState(entityName, 'edit'), {
	                url: "/" + entityName + "/:objectId/edit",
	                controller: entityName + 'InstanceEditCtrl',
	                templateUrl: 'templates/edit-instance.html',
	                params: { objectId: { value: undefined } }
	            });
	            $stateProvider.state(kirraNG.toState(entityName, 'create'), {
	                url: "/" + entityName + "/create",
	                controller: entityName + 'InstanceCreateCtrl',
	                templateUrl: 'templates/edit-instance.html'
	            });
	            $stateProvider.state(kirraNG.toState(entityName, 'performInstanceAction'), {
	                url: "/" + entityName + "/:objectId/perform/:actionName",
	                controller: entityName + 'ActionCtrl',
	                templateUrl: 'templates/execute-action.html',
	                params: { objectId: { value: undefined }, actionName: { value: undefined } }
	            });                
	            $stateProvider.state(kirraNG.toState(entityName, 'performEntityAction'), {
	                url: "/" + entityName + "/perform/:actionName",
	                controller: entityName + 'ActionCtrl',
	                templateUrl: 'templates/execute-action.html',
	                params: { actionName: { value: undefined } }
	            });                
            }); 
        });
        
        angular.element(document).ready(function() {
	      angular.bootstrap(document, ['kirraModule']);
	    });
    });
});	        	
