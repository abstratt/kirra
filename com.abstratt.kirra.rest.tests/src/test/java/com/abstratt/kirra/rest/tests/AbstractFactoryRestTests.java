package com.abstratt.kirra.rest.tests;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.Operation;

public abstract class AbstractFactoryRestTests extends AbstractRestTests {

	
	/**
	 * @param employee optional - will use current employee if not provided
	 * @return
	 */
	public Instance newExpense() {
		Entity entity = schemaManagement.getEntity("expenses", "Expense");
        Operation operation = findByName(entity.getOperations(), "newExpense");
        // provide the number of arguments expected by the operation
        Instance category = getAnyInstance("expenses", "Category");
        assertNotNull(category);
        Instance employee = null;
		List<?> result = instanceManagement.executeOperation(operation, null, Arrays.asList("Some expense", 200d, LocalDate.now(), category, employee ));
        assertEquals(1, result.size());
		return (Instance) result.get(0);
	}

	public Instance createCategory() {
		Instance newInstance = new Instance("expenses", "Category");
		newInstance.setValue("name", unique("My Category"));
		return instanceManagement.createInstance(newInstance);
	}

}
