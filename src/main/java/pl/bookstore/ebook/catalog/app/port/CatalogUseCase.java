package pl.bookstore.ebook.catalog.app.port;

import pl.bookstore.ebook.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CatalogUseCase {

  List<Book> findAll();

  List<Book> findByTitle(String title);

  Optional<Book> findOneByTitle(String title);

  Optional<Book> findById(Long id);

  List<Book> findByAuthor(String author);

  Book addBook(CreateBookCommand createBookCommand);

  UpdateBookResponse updateBook(UpdateBookCommand updateBookCommand);

  void removeById(Long id);

  void updateBookCover(UpdateBookCoverCommand command);

  void removeBookCover(Long id);

  record UpdateBookCoverCommand(Long id, byte[] file, String contentType, String fileName) {}

  record CreateBookCommand(String title, Set<Long> authors, Integer year, BigDecimal price) {}

  record UpdateBookResponse(boolean success, List<String> errors) {}

  record UpdateBookCommand(
      Long id, String title, Set<Long> authors, Integer year, BigDecimal price) {}
}
