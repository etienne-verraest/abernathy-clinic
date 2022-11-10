package com.abernathy.webinterface.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.abernathy.webinterface.validation.validator.NameValidator;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = NameValidator.class)
public @interface Name {

	String message() default "Some characters are invalid, please use only letters and hyphen";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
