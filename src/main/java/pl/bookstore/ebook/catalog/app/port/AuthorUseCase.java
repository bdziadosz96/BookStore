package pl.bookstore.ebook.catalog.app.port;

import java.util.List;
import pl.bookstore.ebook.catalog.domain.Author;

public interface AuthorUseCase {
    List<Author> findAll();
}
