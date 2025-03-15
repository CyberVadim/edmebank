package com.edmebank.clientmanagement.dto.spectrum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicantRequest {
    @JsonProperty("queryType")
    private String queryType = "MULTIPART";

    @JsonProperty("query")
    private String query = " ";

    @JsonProperty("data")
    private ApplicantData data;
}

