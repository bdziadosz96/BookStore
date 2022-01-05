package pl.bookstore.ebook.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.price.OrderPrice;


class OrderDtoTest {

    @Test
    public void givenEmptyOrderDtoWhenCalculateTotalPriceWithCourier_thenReturnOk(){
        //given
        Book book = new Book("Test",2000,BigDecimal.TEN,5L);
        OrderItem orderItem = new OrderItem(book,5);
        OrderPrice orderPrice = new OrderPrice(
                BigDecimal.ZERO,
                Delivery.COURIER.getPrice(),
                BigDecimal.ZERO
        );
        OrderDto orderDto = new OrderDto(
                1L,
                OrderStatus.NEW,
                Set.of(),
                Recipient.builder().build(),
                LocalDateTime.now(),
                orderPrice,
                orderPrice.finalPrice()
        );
        //when
        BigDecimal price = orderDto.getFinalPrice();

        //then
        Assertions.assertEquals("9.90", price.toPlainString());
    }

    @Test
    public void givenNotEmptyOrderDtoCalculateTotalPrice_thenReturnNotEquals(){
        //given
        Book book = new Book("Test",2000,BigDecimal.TEN,5L);
        OrderItem orderItem = new OrderItem(book,5);
        OrderPrice orderPrice = new OrderPrice(
                book.getPrice(),
                Delivery.COURIER.getPrice(),
                BigDecimal.ZERO
        );
        OrderDto orderDto = new OrderDto(
                1L,
                OrderStatus.NEW,
                Set.of(orderItem),
                Recipient.builder().build(),
                LocalDateTime.now(),
                orderPrice,
                orderPrice.finalPrice()
        );
        //when
        BigDecimal price = orderDto.getFinalPrice();

        //then
        Assertions.assertNotEquals(BigDecimal.ZERO, price);
    }

}