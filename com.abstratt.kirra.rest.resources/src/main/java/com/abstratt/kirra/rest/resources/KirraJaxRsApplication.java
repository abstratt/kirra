package com.abstratt.kirra.rest.resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KirraJaxRsApplication extends javax.ws.rs.core.Application {
	@Override
	public Set<Class<?>> getClasses() {
		Class<?>[] resourceClasses = new Class[] { IndexResource.class,
				EntityResource.class, EntityListResource.class,
				ServiceResource.class, ServiceListResource.class,
				ParameterDomainResource.class, RelationshipDomainResource.class,				
				InstanceResource.class, InstanceListResource.class,
				FinderResultResource.class,
				InstanceActionResource.class, EntityActionResource.class, 
				RelatedInstanceListResource.class, RelatedInstanceResource.class };
		return new HashSet<Class<?>>(Arrays.asList(resourceClasses));
	}
}