package com.abstratt.kirra.rest.tests;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.KirraErrorCode;
import com.abstratt.kirra.KirraException;

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
		runAs(() -> readInstance(expenseRef), kirraEmployeeUsername, kirraEmployeePassword, null);
		runAs(() -> readInstance(expenseRef), kirraEmployee2Username, kirraEmployee2Password, KirraErrorCode.NO_AUTHORIZATION);
		runAs(() -> readInstance(expenseRef), kirraAdminUsername, kirraAdminPassword, null);
		runAs(() -> readInstance(expenseRef), null, null, KirraErrorCode.AUTHENTICATION_REQUIRED);
	}
	
	private Instance readInstance(InstanceRef ref) {
		return instanceManagement.getInstance(ref);
	}

	private void runAs(Runnable attempt, String username, String password, KirraErrorCode errorCode)  {
		runAs(() -> { attempt.run(); return null;  }, username, password, errorCode);
	}
	
	private <T> T runAs(Supplier<T> attempt, String username, String password, KirraErrorCode errorCode)  {
		try {
			login(username, password);
			T result = attempt.get();
			assertNull(errorCode); 
			return result;
		} catch (KirraException e) {
			assertNotNull(MessageFormat.format("kind: {0} - {1}", e.getKind(), e.getMessage()), errorCode); 
			assertEquals(e.getKind(), errorCode.getKind());
			assertEquals(e.getMessage(), errorCode.getMessage());
		} catch (IOException e) {
			fail(e.toString());
		}
		return null;
	}
}