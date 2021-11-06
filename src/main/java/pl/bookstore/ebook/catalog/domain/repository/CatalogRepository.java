package pl.bookstore.ebook.catalog.domain.repository;

import pl.bookstore.ebook.catalog.domain.model.Book;

import java.util.List;

public interface CatalogRepository {
  List<Book> findAll();

  void save(Book book);
}
