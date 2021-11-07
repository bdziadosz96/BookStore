package pl.bookstore.ebook.catalog.app.port;

import pl.bookstore.ebook.catalog.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface CatalogUseCase {
  List<Book> findAll();

  List<Book> findByTitle(String title);

  List<Book> findByAuthor(String author);

  Optional<Book> findOneByAuthorAndTitle(String author, String title);

  void addBook(CommandCreateBook commandCreateBook);

  UpdateBookResponse updateBook(CommandUpdateBook commandUpdateBook);

  void removeById(Long id);

  record CommandCreateBook(String title, String author, Integer year) {
  }

  record CommandUpdateBook(Long id, String title, String author, Integer year) {
    public Book updateFields(Book book) {
      if (title != null) {
          book.setTitle(title);
      }
      if (author != null) {
        book.setAuthor(author);
      }
      if (year != null) {
        book.setYear(year);
      }
      return book;
    }
  }

  record UpdateBookResponse(boolean success, List<String> errors) {}
}
