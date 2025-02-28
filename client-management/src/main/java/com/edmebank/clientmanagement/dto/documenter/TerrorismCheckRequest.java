package com.edmebank.clientmanagement.dto.documenter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TerrorismCheckRequest {
    @JsonProperty("params")
    private Params params;

    @JsonProperty("requestId")
    private String requestId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Params {
        private String firstname;
        private String lastname;
        private String secondname;
        private String dob;
        private String country;
        private String method;
    }
}

