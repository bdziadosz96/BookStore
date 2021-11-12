package pl.bookstore.ebook.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
class CatalogController {
    private CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    private List<Book> getAll() {
        return catalog.findAll();
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> getById(@PathVariable Long id) {
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}