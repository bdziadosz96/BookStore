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
  private Long id;
  private String title;
  private String author;
  private Integer year;
}
