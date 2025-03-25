package com.edmebank.clientmanagement.mapper;

import com.edmebank.clientmanagement.dto.ClientDTO;
import com.edmebank.clientmanagement.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO toDto(Client client);

    @Mapping(target = "id", ignore = true)
    Client toEntity(ClientDTO clientDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "passportNumber", ignore = true)
    @Mapping(target = "passportIssuedBy", ignore = true)
    @Mapping(target = "passportIssueDate", ignore = true)
    @Mapping(target = "inn", ignore = true)
    @Mapping(target = "snils", ignore = true)
    @Mapping(target = "amlChecked", ignore = true)
    void updateClientFromDto(ClientDTO clientDTO, @MappingTarget Client client);
}

