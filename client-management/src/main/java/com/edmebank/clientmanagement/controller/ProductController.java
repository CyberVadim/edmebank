package com.edmebank.clientmanagement.controller;

import com.edmebank.clientmanagement.dto.bank_product.ClientProductDTO;
import com.edmebank.clientmanagement.dto.bank_product.ClientProductRequest;
import com.edmebank.clientmanagement.model.bank_product.BankProduct;
import com.edmebank.clientmanagement.service.ClientService;
import com.edmebank.clientmanagement.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableProducts(@PathVariable UUID clientId) {
        return ResponseEntity.ok(productService.getAvailableProducts(clientId));
    }

    @GetMapping("/fixed")
    public ResponseEntity<List<ClientProductDTO>> getClientProducts(@PathVariable UUID clientId) {
        return ResponseEntity.ok(productService.getClientProducts(clientId));
    }

    @PostMapping
    public ResponseEntity<String> linkProductToClient(
            @PathVariable UUID clientId,
            @RequestBody ClientProductRequest request) {
        productService.linkProductToClient(clientId, request);
        return ResponseEntity.ok("Продукт привязан к клиенту");
    }
}

