package com.jun0dev.cms.order.service;

import com.jun0dev.cms.order.domain.model.Product;
import com.jun0dev.cms.order.domain.model.ProductItem;
import com.jun0dev.cms.order.domain.product.AddProductForm;
import com.jun0dev.cms.order.domain.product.UpdateProductForm;
import com.jun0dev.cms.order.domain.product.UpdateProductItemForm;
import com.jun0dev.cms.order.exception.CustomException;
import com.jun0dev.cms.order.exception.ErrorCode;
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

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form) {
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        product.setName(form.getName());
        product.setDescription(form.getDescription());

        for (UpdateProductItemForm itemForm : form.getItems()) {
            ProductItem item = product.getProductItems().stream()
                    .filter(pi -> pi.getId().equals(itemForm.getId()))
                    .findFirst().orElseThrow((() -> new CustomException(ErrorCode.NOT_FOUND_ITEM)));
            item.setName(itemForm.getName());
            item.setPrice(itemForm.getPrice());
            item.setCount(itemForm.getCount());
        }
        return product;
    }
}