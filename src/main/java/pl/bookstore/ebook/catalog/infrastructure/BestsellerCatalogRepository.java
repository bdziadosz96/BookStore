package pl.bookstore.ebook.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.domain.repository.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class BestsellerCatalogRepository implements CatalogRepository {
  private final Map<Long, Book> storage = new ConcurrentHashMap<>();

  public BestsellerCatalogRepository() {
    storage.put(1L, new Book(1L, "Think in Java", "Robert O Brain", 2020));
    storage.put(2L, new Book(2L, "Python for beginners", "Kasper Velgert", 2011));
    storage.put(3L, new Book(3L, "Spring in Action", "Joshua Bloch", 2018));
    storage.put(4L, new Book(4L, "Clean Code", "Uncle Bob", 2013));
  }

  @Override
  public List<Book> findAll() {
    return new ArrayList<>(storage.values());
  }
}
