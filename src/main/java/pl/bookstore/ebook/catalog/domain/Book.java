package pl.bookstore.ebook.catalog.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String title;
  private Integer year;
  private BigDecimal price;
  private Long coverId;
  @CreatedDate private LocalDateTime createdAt;
  @LastModifiedDate private LocalDateTime updatedAt;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable
  private Set<Author> authors;

  public Book(String title, Integer year, BigDecimal price) {

    this.title = title;
    this.year = year;
    this.price = price;
  }
}
