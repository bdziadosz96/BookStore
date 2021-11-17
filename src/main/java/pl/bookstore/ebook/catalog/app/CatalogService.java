package pl.bookstore.ebook.catalog.app;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.catalog.domain.CatalogRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {
    private final CatalogRepository catalogRepository;

    @Override
    public Optional<Book> findById(Long id) {
        return catalogRepository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .findAny();
    }


    @Override
    public List<Book> findByAuthor(String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Book> findOneByAuthor(String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .findAny();
    }

    @Override
    public List<Book> findByAuthorAndTitle(String author, String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Book> findOneByAuthorAndTitle(String author, String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().startsWith(author))
                .filter(book -> book.getTitle().startsWith(title))
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAll();
    }

    @Override
    public void addBook(CreateBookCommand createBookCommand) {
        Book book = createBookCommand.toBook();
        catalogRepository.save(book);
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
        catalogRepository.removeById(id);
    }
}
