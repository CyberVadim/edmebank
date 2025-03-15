package com.edmebank.clientmanagement.dto.documenter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerrorismCheckResponse {
    private String requestId;
    private String datecreated;
    private String state;
    private int tasks;
    private Results results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Results {
        private TerrorismResult terror;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TerrorismResult {
        private String taskId;
        private String requestId;
        private Result result;
        private String dateupdated;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private int status;
        private List<TerroristRecord> data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TerroristRecord {
        private String id;
        private String type_row;
        private String fio;
        private String status;
        private String datecreated;
    }
}

