package com.abernathy.webinterface.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.abernathy.webinterface.validation.validator.PhoneValidator;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {

	String message() default "Phone number must be in the following format : 000-000-0000";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
