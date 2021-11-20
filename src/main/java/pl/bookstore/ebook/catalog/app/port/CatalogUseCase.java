package pl.bookstore.ebook.catalog.app.port;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import pl.bookstore.ebook.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface CatalogUseCase {
  List<Book> findAll();

  List<Book> findByTitle(String title);

  Optional<Book> findOneByTitle(String title);

  Optional<Book> findById(Long id);

  Optional<Book> findOneByAuthor(String author);

  List<Book> findByAuthor(String author);

  List<Book> findByAuthorAndTitle(String author, String title);

  Optional<Book> findOneByAuthorAndTitle(String author, String title);

  Book addBook(CreateBookCommand createBookCommand);

  UpdateBookResponse updateBook(UpdateBookCommand updateBookCommand);

  void removeById(Long id);

  void updateBookCover(UpdateBookCoverCommand command);

  record UpdateBookCoverCommand(Long id, byte[] file, String contentType, String fileName) {}

  record CreateBookCommand(String title, String author, Integer year, BigDecimal price) {
    public Book toBook() {
      return new Book(title,author,year,price);
    }
  }

  @Value
  @Builder
  @AllArgsConstructor
  class UpdateBookCommand {
    Long id;
    String title;
    String author;
    Integer year;
    BigDecimal price;

    public Book updateFields(Book book) {
      if (StringUtils.isNotBlank(title)) {
          book.setTitle(title);
      }
      if (StringUtils.isNotBlank(author)) {
        book.setAuthor(author);
      }
      if (year != null) {
        book.setYear(year);
      }
      if (price != null) {
        book.setPrice(BigDecimal.valueOf(price.longValue()));
      }
      return book;
    }
  }

  record UpdateBookResponse(boolean success, List<String> errors) {}
}
