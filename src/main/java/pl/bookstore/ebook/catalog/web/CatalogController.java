package pl.bookstore.ebook.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;

import java.util.List;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping(params = {"title"})
    @ResponseStatus(HttpStatus.OK)
    private List<Book> getAll(@RequestParam String title) {
        return catalog.findByTitle(title);
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> getById(@PathVariable Long id) {
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}