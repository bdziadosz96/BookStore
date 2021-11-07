package pl.bookstore.ebook.catalog.domain.repository;

import pl.bookstore.ebook.catalog.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
  List<Book> findAll();

  void save(Book book);

  Optional<Book> findById(Long id);

  void removeById(Long id);
}
