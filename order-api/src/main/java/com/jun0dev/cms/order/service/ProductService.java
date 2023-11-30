package com.jun0dev.cms.order.service;

import com.jun0dev.cms.order.domain.model.Product;
import com.jun0dev.cms.order.domain.product.AddProductForm;
import com.jun0dev.cms.order.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form) {
        return productRepository.save(Product.of(sellerId, form));
    }
}
