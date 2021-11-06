package pl.bookstore.ebook.catalog.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.domain.repository.CatalogRepository;

import java.util.List;
import java.util.Optional;

@Service
public
class CatalogService implements CatalogUseCase {
  @Autowired private CatalogRepository catalogRepository;

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
    return catalogRepository.findAll();
  }

  @Override
  public Optional<Book> findOneByAuthorAndTitle(String author, String title) {
    return Optional.empty();
  }

  @Override
  public void addBook(CommandCreateBook commandCreateBook) {
    Book book = new Book(commandCreateBook.title(), commandCreateBook.author(), commandCreateBook.year());
    catalogRepository.save(book);
  }

  @Override
  public void removeById(Long id) {}
}
