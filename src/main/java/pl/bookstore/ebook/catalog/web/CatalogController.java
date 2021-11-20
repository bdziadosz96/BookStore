package pl.bookstore.ebook.catalog.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.*;

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
  public ResponseEntity<Void> addBook(
      @Validated(CreateValidation.class) @RequestBody CatalogController.RestBookCommand command) {
    final Book book = catalog.addBook(command.toCreateCommand());
    final URI uri = createdBookURI(book);
    return ResponseEntity.created(uri).build();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBookBy(@PathVariable Long id) {
    catalog.removeById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void updateBook(
      @PathVariable Long id,
      @Validated(UpdateValidation.class) @RequestBody RestBookCommand command) {
    final UpdateBookResponse response = catalog.updateBook(command.toUpdateCommand(id));
    if (!response.success()) {
      final String message = String.join(", ", response.errors());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
  }

  @PutMapping("/{id}/cover")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file)
      throws IOException {
    System.out.println(
        "Updated book with following cover: "
            + file.getOriginalFilename()
            + " KB "
            + file.getSize());
    catalog.updateBookCover(
        new UpdateBookCoverCommand(
            id, file.getBytes(), file.getContentType(), file.getOriginalFilename()));
  }

  private URI createdBookURI(Book book) {
    return ServletUriComponentsBuilder.fromCurrentRequestUri()
        .path("/" + book.getId().toString())
        .build()
        .toUri();
  }

  interface UpdateValidation {}

  interface CreateValidation {}

  @Data
  private static class RestBookCommand {
    @DecimalMin(
        value = "0.01",
        message = "Price cannot be lower than 0.01",
        groups = {CreateValidation.class, UpdateValidation.class})
    @NotNull(message = "Please provide correct price")
    private BigDecimal price;

    @NotBlank(message = "title cannot be blank", groups = CreateValidation.class)
    private String title;

    @NotBlank(message = "author cannot be blank", groups = CreateValidation.class)
    private String author;

    @NotNull(message = "year cannot be null", groups = CreateValidation.class)
    private Integer year;

    CreateBookCommand toCreateCommand() {
      return new CreateBookCommand(title, author, year, price);
    }

    UpdateBookCommand toUpdateCommand(Long id) {
      return new UpdateBookCommand(id, title, author, year, price);
    }
  }
}
