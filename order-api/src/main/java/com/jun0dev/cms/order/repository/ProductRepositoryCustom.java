package com.jun0dev.cms.order.repository;

import com.jun0dev.cms.order.domain.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchByName(String name);
}
