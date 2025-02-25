package com.edmebank.clientmanagement.client;

import com.edmebank.clientmanagement.dto.dadata.DadataPassportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "dadataClient", url = "https://cleaner.dadata.ru/api/v1/clean")
public interface DadataFeignClient {

    @PostMapping("/passport")
    List<DadataPassportResponse> cleanPassport(@RequestBody List<String> passportNumber,
                                               @RequestHeader("Authorization") String token,
                                               @RequestHeader("X-Secret") String secret);
}
