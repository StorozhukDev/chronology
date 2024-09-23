package com.storozhuk.dev.chronology.trip.validation;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.anyNull;

import com.storozhuk.dev.chronology.trip.dto.api.request.TripRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TripDatesValidator implements ConstraintValidator<ValidTripDates, TripRequestDto> {
  @Override
  public boolean isValid(TripRequestDto tripRequestDto, ConstraintValidatorContext context) {
    if (isNull(tripRequestDto) || anyNull(tripRequestDto.startDate(), tripRequestDto.endDate())) {
      return true;
    }
    if (tripRequestDto.startDate().isBefore(tripRequestDto.endDate())) {
      return true;
    }
    context.disableDefaultConstraintViolation();
    context
        .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode("startDate")
        .addConstraintViolation();
    return false;
  }
}
