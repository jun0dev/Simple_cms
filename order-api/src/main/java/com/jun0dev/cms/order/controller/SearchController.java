package com.jun0dev.cms.order.controller;

import com.jun0dev.cms.order.domain.product.ProductDto;
import com.jun0dev.cms.order.service.ProductSearchService;
import com.jun0dev.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search/product")
@RequiredArgsConstructor
public class SearchController {
    private final ProductSearchService productSearchService;
    private final JwtAuthenticationProvider provider;

    @GetMapping
    public ResponseEntity<List<ProductDto>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(
                productSearchService.searchByName(name).stream()
                        .map(ProductDto::from).collect(Collectors.toList())
        );
    }

    @GetMapping("/detail")
    public ResponseEntity<ProductDto> getDetail(@RequestParam Long productId) {
        return ResponseEntity.ok(
                ProductDto.from(productSearchService.getByProductId(productId))
        );
    }

}

