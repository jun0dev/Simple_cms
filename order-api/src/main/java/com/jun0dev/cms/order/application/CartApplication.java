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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        if (!addAble(cart, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);
    }

    public Cart updateCart(Long customerId, Cart cart) {
        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }

    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));
        cartService.putCart(cart.getCustomerId(), cart);
        Cart returnCart = new Cart();
        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>());
        cartService.putCart(customerId, cart);
        return returnCart;
    }

    public void clearCart(Long customerId) {
        cartService.putCart(customerId, null);
    }


    protected Cart refreshCart(Cart cart) {
        Map<Long, Product> productMap = productSearchService.getListByProductIds(cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Product::getId, product -> product));

        for (int i = 0; i < cart.getProducts().size(); i++) {

            Cart.Product cartProduct = cart.getProducts().get(i);

            Product p = productMap.get(cartProduct.getId());
            if (p == null) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품이 삭제되었습니다.");
                continue;
            }
            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            List<String> tmpMessage = new ArrayList<>();

            for (int j = 0; j < cartProduct.getItems().size(); j++) {
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());
                if (pi == null) {
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    tmpMessage.add(cartProductItem.getName() + "옵션이 삭제되었습니다.");
                    continue;
                }
                boolean isPriceChanged = false, isCountNotEnough = false;

                if (!cartProductItem.getPrice().equals(pi.getPrice())) {
                    isPriceChanged = true;
                    cartProductItem.setPrice(pi.getPrice());
                }
                if (cartProductItem.getCount() > pi.getCount()) {
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }
                if (isPriceChanged && isCountNotEnough) {
                    tmpMessage.add(cartProductItem.getName() + "가격변동, 수량이 부족하여 구매 가능한 최대치로 변겨오디었습니다.");
                } else if (isPriceChanged) {
                    tmpMessage.add(cartProductItem.getName() + "가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    tmpMessage.add(cartProductItem.getName() + "수량이 부족하여 구매 가능한 최대치로 변겨오디었습니다.");
                }
            }
            if (cartProduct.getItems().size() == 0) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
                continue;
            } else if (tmpMessage.size() > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName() + " 상품의 변동 사항 :");
                for (String message : tmpMessage) {
                    builder.append(message);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }

        }
        return cart;
    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
                .findFirst().orElse(Cart.Product.builder().id(product.getId())
                        .items(Collections.emptyList()).build());
        Map<Long, Integer> cartItemCount = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));
        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCount.get(formItem.getId());
                    if (cartCount == null) {
                        cartCount = 0;
                    }
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    if (formItem.getCount() + cartCount > currentCount) {
                        return true;
                    }
                    return false;
                });
    }
}
