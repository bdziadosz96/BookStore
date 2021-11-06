package pl.bookstore.ebook.catalog.app.port;

import pl.bookstore.ebook.catalog.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface CatalogUseCase {
    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findAll();

    Optional<Book> findOneByAuthorAndTitle(String author, String title);

    void addBook();

    void removeById(Long id);
}
