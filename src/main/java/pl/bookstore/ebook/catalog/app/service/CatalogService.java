package pl.bookstore.ebook.catalog.app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.domain.repository.CatalogRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {
  private CatalogRepository catalogRepository;

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
    return catalogRepository.findAll().stream()
        .filter(book -> book.getAuthor().startsWith(author))
        .filter(book -> book.getTitle().startsWith(title))
        .findAny();
  }

  @Override
  public void addBook(CommandCreateBook commandCreateBook) {
    Book book =
        new Book(commandCreateBook.title(), commandCreateBook.author(), commandCreateBook.year());
    catalogRepository.save(book);
  }

  @Override
  public UpdateBookResponse updateBook(CommandUpdateBook commandUpdateBook) {
    return catalogRepository
        .findById(commandUpdateBook.getId())
        .map(
            book -> {
              final Book updatedBook = commandUpdateBook.updateFields(book);
              catalogRepository.save(updatedBook);
              return new UpdateBookResponse(true, Collections.emptyList());
            })
        .orElseGet(
            () ->
                new UpdateBookResponse(
                    false, List.of("Nie znaleziono książki o id " + commandUpdateBook.getId())));
  }

  @Override
  public void removeById(Long id) {
    catalogRepository.removeById(id);
  }
}
