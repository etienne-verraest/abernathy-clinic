package com.abernathy.patients.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.abernathy.patients.validation.validator.DateOfBirthValidator;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = DateOfBirthValidator.class)
public @interface DateOfBirth {

	String message() default "Date must be in the following format : YYYY-MM-DD";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
