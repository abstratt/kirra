package com.abstratt.kirra.rest.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class KirraJaxRsApplication extends javax.ws.rs.core.Application {
	@Override
	public Set<Class<?>> getClasses() {
		List<Class<?>> resourceClasses = new ArrayList<Class<?>>(Arrays.asList(new Class<?>[] {
				KirraRestExceptionMapper.class,
				KirraExceptionMapper.class,
				WebApplicationExceptionMapper.class,
				ThrowableMapper.class,
				IndexResource.class,
				AllEntityCapabilityResource.class,
				EntityResource.class, EntityListResource.class,
				ServiceResource.class, ServiceListResource.class,
				InstanceActionParameterDomainResource.class, 
				EntityActionParameterDomainResource.class,
				RelationshipDomainResource.class,				
				InstanceResource.class,
				FinderResultResource.class,
				InstanceBlobResource.class,
				InstanceActionResource.class, EntityActionResource.class, 
				RelatedInstanceListResource.class, RelatedInstanceResource.class,
				InstanceListResource.class,
				LoginResource.class, LogoutResource.class, SignupResource.class}));
		// carefully add JAX-RS 2 dependant providers
		try {
			getClass().getClassLoader().loadClass("javax.ws.rs.container.ContainerResponseFilter");
			resourceClasses.add(ResponseLoggingInterceptor.class);
			resourceClasses.add(KirraCorsFilter.class);
		} catch (ClassNotFoundException e) {
			// not available in this version of jaxrs, ignore it
		}
		return new LinkedHashSet<Class<?>>(resourceClasses);
	}
}