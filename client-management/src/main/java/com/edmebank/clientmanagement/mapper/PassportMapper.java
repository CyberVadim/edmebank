package com.edmebank.clientmanagement.mapper;

import com.edmebank.clientmanagement.dto.PassportDto;
import com.edmebank.clientmanagement.model.Passport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassportMapper {
    PassportDto toDto(Passport passport);

    Passport toEntity(PassportDto dto);
}
