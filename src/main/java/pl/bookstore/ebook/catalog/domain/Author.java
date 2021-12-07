package pl.bookstore.ebook.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bookstore.ebook.jpa.BaseEntity;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "books")
@EntityListeners(AuditingEntityListener.class)
public class Author extends BaseEntity {
  private String firstname;
  private String lastname;
  @CreatedDate private LocalDateTime createdAt;

  @JsonIgnoreProperties("authors")
  @ManyToMany(fetch = FetchType.EAGER, mappedBy = "authors")
  private Set<Book> books = new HashSet<>();

  public Author(final String firstname, final String lastname) {
    this.firstname = firstname;
    this.lastname = lastname;
  }

  public void addBook(final Book book) {
    books.add(book);
    book.getAuthors().add(this);
  }

  public void removeBook(final Book book) {
    books.remove(book);
    book.getAuthors().remove(this);
  }
}
