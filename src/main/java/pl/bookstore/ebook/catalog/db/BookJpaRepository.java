package pl.bookstore.ebook.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.bookstore.ebook.catalog.domain.Book;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
  List<Book> findByAuthors_firstnameContainsIgnoreCaseOrAuthors_lastnameContainsIgnoreCase(
      String firstname, String lastname);

  @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.firstname LIKE :author")
  List<Book> findByAuthor(@Param("author") String author);
}
