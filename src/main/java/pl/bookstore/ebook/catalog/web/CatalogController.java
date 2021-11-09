package pl.bookstore.ebook.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;

import java.util.List;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
class CatalogController {
    private CatalogUseCase catalog;

    @GetMapping
    private List<Book> findAll() {
        return catalog.findAll();
    }
}