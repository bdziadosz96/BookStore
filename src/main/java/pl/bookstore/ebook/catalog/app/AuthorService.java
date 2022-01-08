package pl.bookstore.ebook.catalog.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.app.port.AuthorUseCase;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService implements AuthorUseCase {
    private final AuthorJpaRepository repository;

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }
}
