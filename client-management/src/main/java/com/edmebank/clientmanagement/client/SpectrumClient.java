package com.edmebank.clientmanagement.client;

import com.edmebank.clientmanagement.dto.spectrum.ApplicantRequest;
import com.edmebank.clientmanagement.dto.spectrum.getReport.ResponseData;
import com.edmebank.clientmanagement.dto.spectrum.checkUid.ResponseUidData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spectrumClient", url = "https://b2b-api.spectrumdata.ru/b2b/api/v1/user/reports")
public interface SpectrumClient {

    @PostMapping(value = "/report_check_applicant_maximum_test@bakset/_make",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseUidData checkApplicant(
            @RequestHeader("Authorization") String authorization,
            @RequestBody ApplicantRequest request
    );

    @GetMapping("/{uid}")
    ResponseData getReport(
            @PathVariable("uid") String uid,
            @RequestParam("_content") boolean content,
            @RequestParam("_detailed") boolean detailed,
            @RequestHeader("Authorization") String authHeader
    );
}
