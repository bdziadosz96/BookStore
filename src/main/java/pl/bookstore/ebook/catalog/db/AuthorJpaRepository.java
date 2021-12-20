package pl.bookstore.ebook.catalog.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
  Optional<Author> findByNameIgnoreCase(String name);
}
