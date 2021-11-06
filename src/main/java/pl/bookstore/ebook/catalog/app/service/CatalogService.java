package pl.bookstore.ebook.catalog.app.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.domain.repository.CatalogRepository;

import java.util.List;
import java.util.Optional;

@Service
class CatalogService implements CatalogUseCase {
  private final CatalogRepository catalogRepository;

  public CatalogService(
      @Qualifier("bestsellerCatalogRepository") CatalogRepository catalogRepository) {
    this.catalogRepository = catalogRepository;
  }

  @Override
  public List<Book> findByTitle(String title) {
    return catalogRepository.findAll().stream()
        .filter(book -> book.getTitle().startsWith(title))
        .toList();
  }

  @Override
  public List<Book> findByAuthor(String author) {
    return catalogRepository.findAll().stream()
        .filter(book -> book.getAuthor().startsWith(author))
        .toList();
  }

  @Override
  public List<Book> findAll() {
    return null;
  }

  @Override
  public Optional<Book> findOneByAuthorAndTitle(String author, String title) {
    return Optional.empty();
  }

  @Override
  public void addBook() {}

  @Override
  public void removeById(Long id) {}
}
