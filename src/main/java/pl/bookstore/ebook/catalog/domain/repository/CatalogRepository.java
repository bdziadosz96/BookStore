package pl.bookstore.ebook.catalog.domain.repository;

import pl.bookstore.ebook.catalog.domain.model.Book;

import java.util.List;

public interface CatalogRepository {
  public List<Book> findAll();
}
