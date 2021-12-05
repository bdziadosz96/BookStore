package pl.bookstore.ebook.catalog.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase.SaveUploadCommand;
import pl.bookstore.ebook.uploads.domain.Upload;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {
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
  public Book addBook(CreateBookCommand createBookCommand) {
    Book book = toBook(createBookCommand);
    return catalogRepository.save(book);
  }

  @Override
  public UpdateBookResponse updateBook(UpdateBookCommand updateBookCommand) {
    return catalogRepository
        .findById(updateBookCommand.id())
        .map(
            book -> {
              final Book updatedBook = updateFiels(updateBookCommand, book);
              catalogRepository.save(updatedBook);
              return new UpdateBookResponse(true, Collections.emptyList());
            })
        .orElseGet(
            () ->
                new UpdateBookResponse(
                    false, List.of("Nie znaleziono książki o id " + updateBookCommand.id())));
  }

  @Override
  public void removeById(Long id) {
    catalogRepository.deleteById(id);
  }

  @Override
  public void updateBookCover(UpdateBookCoverCommand command) {
    catalogRepository
        .findById(command.id())
        .ifPresent(
            book -> {
              final Upload savedUpload =
                  upload.save(
                      new SaveUploadCommand(
                          command.fileName(), command.file(), command.contentType()));
              book.setCoverId(savedUpload.getId());
              catalogRepository.save(book);
            });
  }

  @Override
  public void removeBookCover(Long id) {
    catalogRepository
        .findById(id)
        .ifPresent(
            book -> {
              if (book.getCoverId() != null) {
                upload.removeById(book.getCoverId());
                book.setCoverId(null);
                catalogRepository.save(book);
              }
            });
  }

  private Book toBook(CreateBookCommand command) {
    Book book = new Book(command.title(), command.year(), command.price());
    final Set<Author> authors = fetchAuthorsByIds(command.authors());
    book.setAuthors(authors);
    return book;
  }

  private Book updateFiels(UpdateBookCommand command, Book book) {
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
      book.setAuthors(fetchAuthorsByIds(command.authors()));
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
                                "Unable to find author with id: " + authorId)))
        .collect(Collectors.toSet());
  }
}
