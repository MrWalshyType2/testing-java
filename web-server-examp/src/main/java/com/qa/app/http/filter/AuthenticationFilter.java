package com.qa.app.http.filter;


import java.util.function.Predicate;

import com.qa.app.http.message.HttpRequest;
import com.qa.app.security.User;
import com.qa.app.security.UserAuthenticationManager;
import com.qa.app.security.UserLogin;

@Filterable
public class AuthenticationFilter implements Filter<UserLogin> {
	
	private UserAuthenticationManager authManager;

	@Override
	public boolean filter(UserLogin t) {
		return authManager.authenticate(t);
	}

}