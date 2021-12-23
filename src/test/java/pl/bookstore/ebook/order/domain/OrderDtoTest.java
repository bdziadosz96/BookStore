package pl.bookstore.ebook.order.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.bookstore.ebook.catalog.domain.Book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderDtoTest {

    @Test
    public void givenEmptyOrderDto_whenCalculateTotalPrice_thenReturnOk(){
        //given
        OrderDto orderDto = new OrderDto(
                1L,
                OrderStatus.NEW,
                Collections.emptySet(),
                Recipient.builder().build(),
                LocalDateTime.now()
        );
        //when
        BigDecimal price = orderDto.totalPrice();

        //then
        Assertions.assertEquals(BigDecimal.ZERO, price);
    }

    @Test
    public void givenNotEmptyOrderDto_whenCalculateTotalPrice_thenReturnNotEquals(){
        //given
        Book book = new Book("Test",2000,BigDecimal.TEN,5L);
        OrderItem orderItem = new OrderItem(book,5);
        OrderDto orderDto = new OrderDto(
                1L,
                OrderStatus.NEW,
                Set.of(orderItem),
                Recipient.builder().build(),
                LocalDateTime.now()
        );
        //when
        BigDecimal price = orderDto.totalPrice();

        //then
        Assertions.assertNotEquals(BigDecimal.ZERO, price);
    }

}