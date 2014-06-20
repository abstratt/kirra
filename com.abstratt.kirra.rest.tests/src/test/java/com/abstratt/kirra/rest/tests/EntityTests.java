package com.abstratt.kirra.rest.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Operation;

public class EntityTests extends AbstractRestTests {
    public void testInvokeAction() {
        Entity entity = schemaManagement.getEntity("expenses", "Expense");
        Operation operation = findByName(entity.getOperations(), "newExpense");
        // provide the number of arguments expected by the operation
        List<?> result = instanceManagement.executeOperation(operation, null, Arrays.asList(null, null, null, null, null));
        assertEquals(1, result.size());
        assertTrue(result.get(0).getClass().getSimpleName(), result.get(0) instanceof Map<?, ?>);
    }
}