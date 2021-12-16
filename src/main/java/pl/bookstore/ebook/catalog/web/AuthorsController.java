package pl.bookstore.ebook.catalog.web;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.catalog.app.port.AuthorUseCase;
import pl.bookstore.ebook.catalog.domain.Author;

@Slf4j
@RequestMapping("/authors")
@RestController
@AllArgsConstructor
class AuthorsController {
    private AuthorUseCase authorUseCase;

    @GetMapping
    public List<Author> findAll() {
        AuthorsController.log.info("findAll() ");
        return authorUseCase.findAll();
    }
}
