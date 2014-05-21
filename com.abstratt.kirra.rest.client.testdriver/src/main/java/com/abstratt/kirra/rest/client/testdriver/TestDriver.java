package com.abstratt.kirra.rest.client.testdriver;

import java.net.URI;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.rest.client.InstanceManagementOnREST;
import com.abstratt.kirra.rest.client.SchemaManagementOnREST;

public class TestDriver {
	public static void main(String[] args) {
		URI restUri = URI.create("http://develop.cloudfier.com/services/api/demo-cloudfier-examples-");
		SchemaManagementOnREST schemaManagement = new SchemaManagementOnREST(restUri);
		for (Entity entity : schemaManagement.getEntities("expenses")) {
			System.out.println(entity.getTypeRef());
			System.out.println(entity.getNamespace());
			System.out.println(entity.getName());
			System.out.println(entity.getLabel());
			System.out.println(entity.getDescription());
			System.out.println("");
		}
		
		InstanceManagementOnREST instanceManagement = new InstanceManagementOnREST(restUri);
		for (Instance current : instanceManagement.getInstances("expenses", "Expense", true)) {
			System.out.println(current.getTypeRef());
			System.out.println(current.getEntityName());
			System.out.println(current.getEntityNamespace());
			System.out.println(current.getValues());
			System.out.println(current.getShorthand());
			System.out.println("");
		}

	}

}
