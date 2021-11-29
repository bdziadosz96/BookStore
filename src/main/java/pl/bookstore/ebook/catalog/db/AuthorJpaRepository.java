package pl.bookstore.ebook.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author,Long> {
}
