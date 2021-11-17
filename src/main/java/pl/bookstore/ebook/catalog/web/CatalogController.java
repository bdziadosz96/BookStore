package pl.bookstore.ebook.catalog.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.CreateBookCommand;

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
      return catalog.findByAuthorAndTitle(author.get(), title.get());
    } else if (title.isPresent()) {
      return catalog.findByTitle(title.get());
    } else if (author.isPresent()) {
      return catalog.findByAuthor(author.get());
    } else {
      return catalog.findAll();
    }
  }

  @GetMapping("/{id}")
  private ResponseEntity<?> getById(@PathVariable Long id) {
    return catalog.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> addBook(@Valid @RequestBody RestCreateBookCommand command) {
    final Book book = catalog.addBook(command.toCommand());
    final URI uri = createdBookURI(book);
    return ResponseEntity.created(uri).build();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBookBy(@PathVariable Long id) {
    catalog.removeById(id);
  }

  private URI createdBookURI(Book book) {
    return ServletUriComponentsBuilder.fromCurrentRequestUri()
        .path("/" + book.getId().toString())
        .build()
        .toUri();
  }

  @Data
  private static class RestCreateBookCommand {
    @DecimalMin("0.01")
    @NotNull
    BigDecimal price;

    @NotBlank private String title;
    @NotBlank private String author;
    @NotNull private Integer year;

    CreateBookCommand toCommand() {
      return new CreateBookCommand(title, author, year, price);
    }
  }
}
