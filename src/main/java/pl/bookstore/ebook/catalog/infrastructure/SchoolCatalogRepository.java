package pl.bookstore.ebook.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.domain.repository.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class SchoolCatalogRepository implements CatalogRepository {
  private final Map<Long, Book> storage = new ConcurrentHashMap<>();

  public SchoolCatalogRepository() {
    storage.put(1L, new Book(1L, "Pan Tadeusz", "Adam Mickiewicz", 1923));
    storage.put(2L, new Book(2L, "Ogniem i mieczem", "Henryk Sienkiewicz", 1911));
    storage.put(3L, new Book(3L, "Chłopi", "Władysław Reymont", 1854));
    storage.put(4L, new Book(4L, "Pan Wolodyjowski", "Henryk Sienkiewicz", 1839));
  }

  @Override
  public List<Book> findAll() {
    return new ArrayList<>(storage.values());
  }
}
