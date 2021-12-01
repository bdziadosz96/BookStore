package pl.bookstore.ebook.catalog.app.port;

import pl.bookstore.ebook.catalog.domain.Author;

import java.util.List;

public interface AuthorUseCase {
    List<Author> findAll();
}
