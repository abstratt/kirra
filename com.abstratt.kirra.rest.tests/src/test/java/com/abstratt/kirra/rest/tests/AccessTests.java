package com.abstratt.kirra.rest.tests;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.function.Supplier;

import com.abstratt.kirra.EntityCapabilities;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.KirraErrorCode;
import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;

public class AccessTests extends AbstractFactoryRestTests {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		login(kirraEmployeeUsername, kirraEmployeePassword);
	}
	
    public void testCreateCategory() {
        runAs(this::createCategory, kirraAdminUsername, kirraAdminPassword, null);
        runAs(this::createCategory, kirraEmployeeUsername, kirraEmployeePassword, KirraErrorCode.NO_AUTHORIZATION);
    }
    
	public void testReadExpense() {
		InstanceRef expenseRef = runAs(this::newExpense, kirraEmployeeUsername, kirraEmployeePassword, null).getReference();
		runAs(() -> readInstance(expenseRef), "baduser@baddomain.com", "badpassword", KirraErrorCode.AUTHENTICATION_REQUIRED);
		runAs(() -> readInstance(expenseRef), kirraEmployeeUsername, kirraEmployeePassword, null);
		runAs(() -> readInstance(expenseRef), kirraAdminUsername, kirraAdminPassword, null);
		runAs(() -> readInstance(expenseRef), null, null, KirraErrorCode.AUTHENTICATION_REQUIRED);
		runAs(() -> readInstance(expenseRef), kirraEmployee2Username, kirraEmployee2Password, KirraErrorCode.NO_AUTHORIZATION);
	}
	
	private Instance readInstance(InstanceRef ref) {
		return instanceManagement.getInstance(ref);
	}
	
	public void testExpenseEntityCapabilities() {
		runAs(() -> {
			TypeRef expenseType = new TypeRef("expenses.Expense", TypeKind.Entity);
			EntityCapabilities capabilities = getEntityCapabilities(expenseType);
			assertEquals(Arrays.asList(), capabilities.getEntity());
			assertEquals(Arrays.asList("StaticCall"), capabilities.getQueries().get("myExpenses"));
			assertEquals(Arrays.asList("StaticCall"), capabilities.getActions().get("newExpense"));
		},kirraEmployeeUsername, kirraEmployeePassword, null);
		runAs(() -> {
			TypeRef expenseType = new TypeRef("expenses.Expense", TypeKind.Entity);
			EntityCapabilities capabilities = getEntityCapabilities(expenseType);
			assertEquals(Arrays.asList("Create", "List"), capabilities.getEntity());
			assertEquals(Arrays.asList("StaticCall"), capabilities.getQueries().get("myExpenses"));
			assertEquals(Arrays.asList("StaticCall"), capabilities.getActions().get("newExpense"));
		},kirraAdminUsername, kirraAdminPassword, null);
	}
	
	public void testEmployeeEntityCapabilities() {
		runAs(() -> {
			TypeRef expenseType = new TypeRef("expenses.Employee", TypeKind.Entity);
			EntityCapabilities capabilities = getEntityCapabilities(expenseType);
			assertEquals(Arrays.asList("Create", "List"), capabilities.getEntity());
			assertEquals(Arrays.asList("StaticCall"), capabilities.getQueries().get("employeesWithNoExpenses"));
		},kirraEmployeeUsername, kirraEmployeePassword, null);
		runAs(() -> {
			TypeRef expenseType = new TypeRef("expenses.Employee", TypeKind.Entity);
			EntityCapabilities capabilities = getEntityCapabilities(expenseType);
			assertEquals(Arrays.asList("Create", "List"), capabilities.getEntity());
			assertEquals(Arrays.asList(), capabilities.getQueries().get("employeesWithNoExpenses"));
		},kirraAdminUsername, kirraAdminPassword, null);
	}
	
	private EntityCapabilities getEntityCapabilities(TypeRef typeRef) {
		return instanceManagement.getEntityCapabilities(typeRef);
	}


	private void runAs(Runnable attempt, String username, String password, KirraErrorCode errorCode)  {
		runAs(() -> { attempt.run(); return null;  }, username, password, errorCode);
	}
	
	private <T> T runAs(Supplier<T> attempt, String username, String password, KirraErrorCode errorCode)  {
		try {
			login(username, password);
			T result = attempt.get();
			assertEquals(errorCode, null); 
			return result;
		} catch (KirraException e) {
			assertNotNull(MessageFormat.format("kind: {0} - {1}", e.getKind(), e.getMessage()), errorCode); 
			assertEquals(errorCode.getKind(), e.getKind());
			assertEquals(errorCode.getMessage() , e.getMessage());
		} catch (IOException e) {
			fail(e.toString());
		}
		return null;
	}
}