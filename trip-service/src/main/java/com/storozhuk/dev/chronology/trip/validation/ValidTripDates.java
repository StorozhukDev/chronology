package com.storozhuk.dev.chronology.trip.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TripDatesValidator.class)
public @interface ValidTripDates {
  String message() default "Start date must be before end date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
