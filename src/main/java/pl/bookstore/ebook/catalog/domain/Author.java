package pl.bookstore.ebook.catalog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Author {
    @Id
    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "authors")
    private Set<Book> books;
}
