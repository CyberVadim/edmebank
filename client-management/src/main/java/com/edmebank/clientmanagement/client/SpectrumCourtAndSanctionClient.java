package com.edmebank.clientmanagement.client;


import com.edmebank.clientmanagement.dto.spectrum.CourtAndSanctionRequest;
import com.edmebank.clientmanagement.dto.spectrum.SpectrumDataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CourtAndSanction",
            url = "http://localhost:8080/api/v1")
public interface SpectrumCourtAndSanctionClient {

    @PostMapping("/applicants")
    SpectrumDataResponse verifyCourtAndSanction(@RequestBody CourtAndSanctionRequest request);
}
