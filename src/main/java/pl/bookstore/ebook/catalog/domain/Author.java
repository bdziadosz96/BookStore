package pl.bookstore.ebook.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToMany;
import lombok.Generated;
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
@ToString(exclude = "books")
@EntityListeners(AuditingEntityListener.class)
public class Author extends BaseEntity {
    private String name;
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonIgnoreProperties("authors")
    @ManyToMany(
            mappedBy = "authors",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Book> books = new HashSet<>();

    public Author(String firstname) {
        this.name = firstname;
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
