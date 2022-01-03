package pl.bookstore.ebook.order.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public
enum Delivery {
    SELF_PICKUP(BigDecimal.ZERO),
    COURIER(new BigDecimal("9.90"));

    private final BigDecimal price;
}
