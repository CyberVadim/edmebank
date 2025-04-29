package com.edmebank.clientmanagement.service;

import com.edmebank.clientmanagement.dto.AddressValidationResultDto;

public interface AddressValidationService {
    AddressValidationResultDto validate(String rawAddress);
}
