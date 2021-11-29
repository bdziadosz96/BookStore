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
    return catalogRepository.findAll().stream()
        .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
        .toList();
  }

  @Override
  public Optional<Book> findOneByTitle(String title) {
    return catalogRepository.findAll().stream()
        .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
        .findAny();
  }

  @Override
  public List<Book> findByAuthor(String author) {
    return catalogRepository.findAll().stream()
        //        .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
        .toList();
  }

  @Override
  public List<Book> findByAuthorAndTitle(String author, String title) {
    return catalogRepository.findAll().stream()
        //        .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
        .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
        .toList();
  }

  @Override
  public Optional<Book> findOneByAuthorAndTitle(String author, String title) {
    return catalogRepository.findAll().stream()
        //        .filter(book -> book.getAuthor().startsWith(author))
        .filter(book -> book.getTitle().startsWith(title))
        .findFirst();
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
        .findById(updateBookCommand.getId())
        .map(
            book -> {
              final Book updatedBook = updateBookCommand.updateFields(book);
              catalogRepository.save(updatedBook);
              return new UpdateBookResponse(true, Collections.emptyList());
            })
        .orElseGet(
            () ->
                new UpdateBookResponse(
                    false, List.of("Nie znaleziono książki o id " + updateBookCommand.getId())));
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
    final Set<Author> authors =
        command.authors().stream()
            .map(
                authorId ->
                    authorJpaRepository
                        .findById(authorId)
                        .orElseThrow(
                            () ->
                                new IllegalStateException(
                                    "Unable to find author with id: " + authorId)))
            .collect(Collectors.toSet());
    book.setAuthors(authors);
    return book;
  }
}
