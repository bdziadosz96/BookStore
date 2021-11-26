package pl.bookstore.ebook.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book,Long> {
}
