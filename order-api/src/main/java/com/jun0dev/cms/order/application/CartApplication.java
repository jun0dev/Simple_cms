package com.jun0dev.cms.order.application;

import com.jun0dev.cms.order.domain.model.Product;
import com.jun0dev.cms.order.domain.model.ProductItem;
import com.jun0dev.cms.order.domain.product.AddProductCartForm;
import com.jun0dev.cms.order.domain.redis.Cart;
import com.jun0dev.cms.order.exception.CustomException;
import com.jun0dev.cms.order.exception.ErrorCode;
import com.jun0dev.cms.order.service.CartService;
import com.jun0dev.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static com.jun0dev.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form) {

        Product product = productSearchService.getByProductId(form.getId());
        if (product == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }
        Cart cart = cartService.getCart(customerId);

        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);
    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
                .findFirst().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        Map<Long, Integer> cartItemCount = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));
        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCount.get(formItem.getId());
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    if (formItem.getCount() + cartCount > currentCount) {
                        return true;
                    }
                    return false;
                });
    }
}
