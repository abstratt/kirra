package com.abstratt.kirra;

import com.abstratt.kirra.KirraException.Kind;

public enum KirraErrorCode {
	NO_AUTHORIZATION(Kind.AUTHORIZATION), 
	AUTHENTICATION_REQUIRED(Kind.AUTHENTICATION),
	INTERNAL(Kind.INTERNAL);

	private Kind kind;

	KirraErrorCode(Kind kind) {
		this.kind = kind;
	}
	
	public Kind getKind() {
		return kind;
	}

	public String getMessage() {
		return name();
	}
}
