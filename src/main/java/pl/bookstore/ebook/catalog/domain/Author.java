package pl.bookstore.ebook.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "books")
@EntityListeners(AuditingEntityListener.class)
public class Author {
    @Id
    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonIgnoreProperties("authors")
    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "authors")
    private Set<Book> books;

    public Author(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
