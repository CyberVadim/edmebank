package com.edmebank.clientmanagement.dto.spectrum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ApplicantData {
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("patronymic")
    private String patronymic;

    @JsonProperty("birth")
    private String birth;

    @JsonProperty("passport")
    private String passport;

    @JsonProperty("passport_date")
    private String passportDate;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("inn")
    private String inn;
}
