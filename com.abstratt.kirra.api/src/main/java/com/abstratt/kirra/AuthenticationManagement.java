package com.abstratt.kirra;

public interface AuthenticationManagement {
	public void login(Object credentials);
	public void logout();
	public Instance getCurrentUser();
}
