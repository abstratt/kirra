package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;

public class SerializationHelper {

	public static Type getTargetType(TypeRef typeRef) {
	    if (typeRef.getKind() == TypeKind.Primitive) {
	    	String typeName = typeRef.getTypeName();
	    	if (typeName.equals("Date")) {
	    		return LocalDate.class;
	    	}
	    	if (typeName.equals("Time")) {
	    		return LocalTime.class;
	    	}
	    	if (typeName.equals("DateTime")) {
	    		return LocalDateTime.class;
	    	}
			return Object.class;
	    }
	    if (typeRef.getKind() == TypeKind.Entity) {
	    	return InstanceRef.class;
	    }
	    return Object.class;
	}

}
