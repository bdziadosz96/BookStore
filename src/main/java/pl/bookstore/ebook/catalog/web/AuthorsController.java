package pl.bookstore.ebook.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.catalog.app.port.AuthorUseCase;
import pl.bookstore.ebook.catalog.domain.Author;

import java.util.List;

@RequestMapping("/authors")
@RestController
@AllArgsConstructor
class AuthorsController {
    private AuthorUseCase authorUseCase;

    @GetMapping
    public List<Author> findAll() {
        return authorUseCase.findAll();
    }
}
