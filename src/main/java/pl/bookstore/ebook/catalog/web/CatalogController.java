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
@RequestMapping("/catalog")
@AllArgsConstructor
class CatalogController {
  private final CatalogUseCase catalog;

  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  private List<Book> getAll(
      @RequestParam Optional<String> title, @RequestParam Optional<String> author) {
      if (title.isPresent() && author.isPresent()) {
        return catalog.findByAuthorAndTitle(author.get(),title.get());
      }
      else if (title.isPresent()) {
        return catalog.findByTitle(title.get());
      }
      else if (author.isPresent()) {
        return catalog.findByAuthor(author.get());
      } else {
        return catalog.findAll();
      }
  }

  @GetMapping("/{id}")
  private ResponseEntity<?> getById(@PathVariable Long id) {
    return catalog.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}
