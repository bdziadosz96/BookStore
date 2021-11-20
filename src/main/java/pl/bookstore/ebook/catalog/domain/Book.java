package pl.bookstore.ebook.catalog.domain;

import lombok.*;

import java.math.BigDecimal;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {
  private Long id;
  private String title;
  private String author;
  private Integer year;
  private BigDecimal price;
  private Long coverId;

  public Book(String title, String author, Integer year, BigDecimal price) {
    this.title = title;
    this.author = author;
    this.year = year;
    this.price = price;
  }
}
