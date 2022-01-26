package pl.bookstore.ebook.catalog.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.catalog.domain.RestAuthor;
import pl.bookstore.ebook.commons.CreatedURI;

import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.CreateBookCommand;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.UpdateBookCommand;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.UpdateBookCoverCommand;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.UpdateBookResponse;

@RestController
@RequestMapping("/catalog")
@AllArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<RestBook> getAll(
            HttpServletRequest request,
            @RequestParam final Optional<String> title,
            @RequestParam final Optional<String> author) {
        List<Book> books;
        if (author.isPresent() && title.isPresent()) {
            books =  catalog.findByAuthorAndTitle(author.get(), title.get());
        } else if (author.isPresent()) {
            books =  catalog.findByAuthor(author.get());
        } else if (title.isPresent()) {
            books = catalog.findByTitle(title.get());
        } else {
            books = catalog.findAll();
        }
        return books.stream()
                .map(book -> toRestBook(book,request))
                .collect(Collectors.toList());
    }

    private RestBook toRestBook(Book book, HttpServletRequest request) {
        String coverUrl = Optional.ofNullable(book.getCoverId())
                .map(coverId -> ServletUriComponentsBuilder
                .fromContextPath(request)
                .path("/uploads/{id}/file")
                .build(book.getCoverId())
                .toASCIIString())
                .orElse(null);
        return new RestBook(
                book.getId(),
                book.getTitle(),
                book.getYear(),
                book.getPrice(),
                coverUrl,
                book.getAvailable(),
                toRestAuthors(book.getAuthors())
        );
    }

    private Set<RestAuthor> toRestAuthors(Set<Author> authors) {
        return authors.stream()
                .map(x -> new RestAuthor(x.getName()))
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable final Long id) {
        return catalog.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(
            @Validated(CreateValidation.class) @RequestBody final RestBookCommand command) {
        final Book book = catalog.addBook(command.toCreateCommand());
        final URI uri = createdBookURI(book);
        return ResponseEntity.created(uri).build();
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookBy(@PathVariable final Long id) {
        catalog.removeById(id);
    }

    @Secured({"ROLE_ADMIN"})
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(
            @PathVariable final Long id,
            @Validated(UpdateValidation.class) @RequestBody final RestBookCommand command) {
        final UpdateBookResponse response = catalog.updateBook(command.toUpdateCommand(id));
        if (!response.success()) {
            final String message = String.join(", ", response.errors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable final Long id) {
        catalog.removeBookCover(id);
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(
            @PathVariable final Long id, @RequestParam("file") final MultipartFile file)
            throws IOException {
        catalog.updateBookCover(
                new UpdateBookCoverCommand(
                        id, file.getBytes(), file.getContentType(), file.getOriginalFilename()));
    }

    private URI createdBookURI(final Book book) {
        return new CreatedURI("/" + book.getId().toString()).uri();
    }

    interface UpdateValidation {}

    interface CreateValidation {}

    @Data
    public static class RestBookCommand {
        @DecimalMin(
                value = "0.01",
                message = "Price cannot be lower than 0.01",
                groups = {CreateValidation.class, UpdateValidation.class})
        @NotNull(message = "Please provide correct price")
        private BigDecimal price;

        @NotBlank(message = "title cannot be blank", groups = CreateValidation.class)
        private String title;

        @NotEmpty(message = "author cannot be blank", groups = CreateValidation.class)
        private Set<Long> authors;

        @NotNull(message = "year cannot be null", groups = CreateValidation.class)
        private Integer year;

        @NotNull @PositiveOrZero private Long available;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, authors, year, price, available);
        }

        UpdateBookCommand toUpdateCommand(final Long id) {
            return new UpdateBookCommand(id, title, authors, year, price, available);
        }
    }
}
