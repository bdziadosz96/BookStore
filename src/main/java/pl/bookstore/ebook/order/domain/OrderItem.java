package pl.bookstore.ebook.order.domain;

import lombok.Value;
import pl.bookstore.ebook.catalog.domain.model.Book;

@Value
public class OrderItem {
  Book book;
  int quantity;
}
