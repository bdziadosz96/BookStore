package pl.bookstore.ebook.catalog.db;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.bookstore.ebook.catalog.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
  List<Book> findByAuthors_firstnameContainsIgnoreCaseOrAuthors_lastnameContainsIgnoreCase(
      String firstname, String lastname);

  @Override
  @Query("SELECT DISTINCT b FROM Book b join fetch b.authors")
  List<Book> findAll();

  @Query(
      " SELECT b FROM Book b JOIN b.authors a "
          + " WHERE "
          + " lower(a.firstname) LIKE lower(concat('%', :author,'%')) "
          + " OR lower(a.lastname) LIKE lower(concat('%', :author,'%')) ")
  List<Book> findByAuthor(@Param("author") String author);

  List<Book> findByTitleStartsWithIgnoreCase(String title);

  Optional<Book> findDistinctFirstByTitle(String title);

  @Query(
      " SELECT b FROM Book b JOIN b.authors a "
          + " WHERE lower(b.title) LIKE lower(concat('%', :title,'%')) "
          + " AND "
          + " (lower(a.firstname) LIKE lower(concat('%', :name,'%')) "
          + " OR "
          + " lower(a.lastname) LIKE lower(concat('%', :name,'%'))) ")
  List<Book> findByTitleAndAuthor(@Param("name") String name, @Param("title") String title);
}
