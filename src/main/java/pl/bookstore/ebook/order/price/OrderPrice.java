package pl.bookstore.ebook.order.price;

import java.math.BigDecimal;

public record OrderPrice(BigDecimal itemPrice, BigDecimal deliveryPrice, BigDecimal discounts) {
    public BigDecimal finalPrice() {
        return itemPrice.add(deliveryPrice).subtract(discounts);
    }
}
