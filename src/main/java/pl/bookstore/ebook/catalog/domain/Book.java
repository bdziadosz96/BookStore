package pl.bookstore.ebook.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bookstore.ebook.jpa.BaseEntity;

@ToString(exclude = "authors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity {
  private String title;
  private Integer year;
  private BigDecimal price;
  private Long coverId;
  @CreatedDate private LocalDateTime createdAt;
  @LastModifiedDate private LocalDateTime updatedAt;

  @ManyToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable
  @JsonIgnoreProperties("books")
  private Set<Author> authors = new HashSet<>();

  public Book(final String title, final Integer year, final BigDecimal price) {

    this.title = title;
    this.year = year;
    this.price = price;
  }

  public void addAuthor(final Author author) {
    authors.add(author);
    author.getBooks().add(this);
  }

  public void removeAuthor(final Author author) {
    authors.remove(author);
    author.getBooks().remove(this);
  }

  public void removeAuthors() {
    final Book self = this;
    authors.forEach(author -> author.getBooks().remove(self));
    authors.clear();
  }
}
