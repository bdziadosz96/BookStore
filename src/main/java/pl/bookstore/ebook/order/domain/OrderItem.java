package pl.bookstore.ebook.order.domain;

import lombok.Value;
import pl.bookstore.ebook.catalog.domain.Book;

@Value
public class OrderItem {
  Book book;
  int quantity;
}
