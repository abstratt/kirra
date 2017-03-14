package com.abstratt.kirra.rest.resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KirraJaxRsApplication extends javax.ws.rs.core.Application {
	@Override
	public Set<Class<?>> getClasses() {
		Class<?>[] resourceClasses = new Class[] { IndexResource.class,
				AllEntityCapabilityResource.class,
				EntityResource.class, EntityListResource.class,
				ServiceResource.class, ServiceListResource.class,
				ParameterDomainResource.class, RelationshipDomainResource.class,				
				InstanceResource.class, 
				FinderResultResource.class,
				InstanceActionResource.class, EntityActionResource.class, 
				RelatedInstanceListResource.class, RelatedInstanceResource.class,
				InstanceListResource.class,
				LoginResource.class, LogoutResource.class, SignupResource.class};
		return new HashSet<Class<?>>(Arrays.asList(resourceClasses));
	}
}