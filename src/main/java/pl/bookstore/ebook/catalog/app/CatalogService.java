package pl.bookstore.ebook.catalog.app;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase.SaveUploadCommand;
import pl.bookstore.ebook.uploads.domain.Upload;

@Slf4j
@Service
@AllArgsConstructor
public class CatalogService implements CatalogUseCase {
    private final BookJpaRepository catalogRepository;
    private final AuthorJpaRepository authorJpaRepository;
    private final UploadUseCase upload;

    @Override
    public Optional<Book> findById(Long id) {
        return catalogRepository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findByTitleStartsWithIgnoreCase(title);
    }

    @Override
    public List<Book> findByAuthorAndTitle(String author, String title) {
        return catalogRepository.findByTitleAndAuthor(author, title);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return catalogRepository.findDistinctFirstByTitle(title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return catalogRepository.findByAuthor(author);
    }

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAll();
    }

    @Override
    @Transactional
    public Book addBook(CreateBookCommand createBookCommand) {
        Book book = toBook(createBookCommand);
        CatalogService.log.info("Book added: " + book);
        return catalogRepository.save(book);
    }

    @Override
    @Transactional
    public UpdateBookResponse updateBook(UpdateBookCommand updateBookCommand) {
        return catalogRepository
                .findById(updateBookCommand.id())
                .map(
                        book -> {
                            Book updatedBook = updateFields(updateBookCommand, book);
                            CatalogService.log.info("Update book " + updatedBook.getId());
                            catalogRepository.save(updatedBook);
                            return new UpdateBookResponse(true, Collections.emptyList());
                        })
                .orElseGet(
                        () ->
                                new UpdateBookResponse(
                                        false,
                                        List.of(
                                                "Nie znaleziono książki o id "
                                                        + updateBookCommand.id())));
    }

    @Override
    public void removeById(Long id) {
        CatalogService.log.info("Deleted book with id: " + id);
        catalogRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateBookCover(UpdateBookCoverCommand command) {
        catalogRepository
                .findById(command.id())
                .ifPresent(
                        book -> {
                            Upload savedUpload =
                                    upload.save(
                                            new SaveUploadCommand(
                                                    command.fileName(),
                                                    command.file(),
                                                    command.contentType()));
                            CatalogService.log.info("Updated book cover: " + book.getId());
                            book.setCoverId(savedUpload.getId());
                            catalogRepository.save(book);
                        });
    }

    @Override
    @Transactional
    public void removeBookCover(Long id) {
        catalogRepository
                .findById(id)
                .ifPresent(
                        book -> {
                            if (book.getCoverId() != null) {
                                upload.removeById(book.getCoverId());
                                book.setCoverId(null);
                                CatalogService.log.info("Removed book cover: " + book.getId());
                                catalogRepository.save(book);
                            }
                        });
    }

    private Book toBook(CreateBookCommand command) {
        Book book = new Book(command.title(), command.year(), command.price(), command.available());
        Set<Author> authors = fetchAuthorsByIds(command.authors());
        updateBooks(book, authors);
        return book;
    }

    private void updateBooks(Book book, Set<Author> authors) {
        book.removeAuthors();
        authors.forEach(book::addAuthor);
    }

    private Book updateFields(UpdateBookCommand command, Book book) {
        if (command.title() != null) {
            book.setTitle(command.title());
        }
        if (command.year() != null) {
            book.setYear(command.year());
        }
        if (command.price() != null) {
            book.setPrice(command.price());
        }
        if (command.authors() != null && command.authors().size() > 0) {
            updateBooks(book, fetchAuthorsByIds(command.authors()));
        }
        return book;
    }

    private Set<Author> fetchAuthorsByIds(Set<Long> authors) {
        return authors.stream()
                .map(
                        authorId ->
                                authorJpaRepository
                                        .findById(authorId)
                                        .orElseThrow(
                                                () ->
                                                        new IllegalStateException(
                                                                "Unable to find author with id: "
                                                                        + authorId)))
                .collect(Collectors.toSet());
    }
}
