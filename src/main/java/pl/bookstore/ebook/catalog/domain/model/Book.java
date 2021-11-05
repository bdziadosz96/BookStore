package pl.bookstore.ebook.catalog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Book {
  Long id;
  String title;
  String author;
  Integer year;
}
