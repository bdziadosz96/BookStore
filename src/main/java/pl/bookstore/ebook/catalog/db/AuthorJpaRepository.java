package pl.bookstore.ebook.catalog.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.bookstore.ebook.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
  @Override
  @Query("SELECT a FROM Author a JOIN FETCH a.books")
  List<Author> findAll();
}
