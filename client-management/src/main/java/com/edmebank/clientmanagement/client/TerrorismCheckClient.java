package com.edmebank.clientmanagement.client;

import com.edmebank.clientmanagement.dto.documenter.TerrorismCheckRequest;
import com.edmebank.clientmanagement.dto.documenter.TerrorismCheckResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "terrorismCheckClient", url = "https://api.newdb.net/v2")
public interface TerrorismCheckClient {

    @PostMapping
    @Headers({
            "Content-Type: application/json",
            "X-API-KEY: {apiKey}",
            "X-Real-IP: {realIp}"
    })
    TerrorismCheckResponse checkTerrorism(@RequestBody TerrorismCheckRequest request,
                                          @RequestHeader("X-API-KEY") String apiKey,
                                          @RequestHeader("X-Real-IP") String realIp);
}

