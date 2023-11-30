package com.jun0dev.cms.order.service;

import com.jun0dev.cms.order.domain.model.Product;
import com.jun0dev.cms.order.domain.product.AddProductForm;
import com.jun0dev.cms.order.domain.product.AddProductItemForm;
import com.jun0dev.cms.order.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_PRODUCT_TEST () {
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("nike airForce1", "신발", 3);

        Product p = productService.addProduct(sellerId, form);

        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);

        assertEquals(result.getName(), "nike airForce1");
        assertEquals(result.getDescription(), "신발");

        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "nike airForce10");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
        assertEquals(result.getProductItems().get(0).getCount(), 1);
    }

    private static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name + i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }
    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(1)
                .build();
    }
}