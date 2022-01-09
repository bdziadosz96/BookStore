package pl.bookstore.ebook.catalog.app;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.CreateBookCommand;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.UpdateBookCommand;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.UpdateBookResponse;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class CatalogServiceBookTest {
    @Autowired
    CatalogService catalogService;

    @Autowired
    BookJpaRepository repository;

    @Autowired
    AuthorJpaRepository authorRepository;

    @Test
    public void updateBookWithCommandWorksCorrectly() {
        //given
        Book effectiveJava = catalogService.addBook(givenEffectiveJavaCommand());

        //when
        UpdateBookResponse response = catalogService.updateBook(givenUpdateBookCommand(effectiveJava.getId()));

        //then
        assertTrue(response.success());
        assertEquals("Effective Java in Spring",repository.findById(effectiveJava.getId()).get().getTitle());
    }

    @Test
    public void updateBookWithNotExistingBookReturnErrors() {
        //given
        Book effectiveJava = catalogService.addBook(givenEffectiveJavaCommand());
        long randomId = effectiveJava.getId() + 1;
        //when
        UpdateBookResponse response = catalogService.updateBook(givenUpdateBookCommand(randomId));

        //then
        assertFalse(response.success());
        assertTrue(response.errors().get(0).contains("Nie znaleziono książki o id " + randomId));
    }

    @Test
    public void deleteBookWorksCorrectly() {
        //given
        Book effectiveJava = catalogService.addBook(givenEffectiveJavaCommand());

        //when
        catalogService.removeById(effectiveJava.getId());

        //then
        assertEquals(0, catalogService.findAll().size());
        assertFalse(catalogService.findById(effectiveJava.getId()).isPresent());
    }

    @Test
    public void findByAuthorAndTitle() {
        //given
        Author shannon = givenAuthor();
        Book effectiveJavaWithAuthor = catalogService.addBook(givenEffectiveJavaCommand(shannon));

        //when
        List<Book> books = catalogService.findByAuthorAndTitle(shannon.getName(), effectiveJavaWithAuthor.getTitle());

        //then
        assertEquals(1, books.size());
    }

    private UpdateBookCommand givenUpdateBookCommand(Long id) {
        return new UpdateBookCommand(
                id,
                "Effective Java in Spring",
                Set.of(),
                2012,
                new BigDecimal("29.99"),
                40L
        );
    }


    private Author givenAuthor() {
        Author shannon = new Author("Shannon Brian");
        authorRepository.save(shannon);
        return shannon;
    }

    private CreateBookCommand givenEffectiveJavaCommand() {
        return new CreateBookCommand(
                "Effective Java",
                Set.of(),
                2005,
                new BigDecimal("19.99"),
                50L
        );
    }

    private CreateBookCommand givenEffectiveJavaCommand(Author author) {
        return new CreateBookCommand(
                "Effective Java",
                Set.of(author.getId()),
                2005,
                new BigDecimal("19.99"),
                50L
        );
    }

    private Book givenEffectiveJava() {
        return new Book(
                "Effective Java",
                2005,
                new BigDecimal("19.99"),
                50L
        );
    }

    private Book givenJavaPuzzlers() {
        return new Book(
                "Java Puzzlers",
                2006,
                new BigDecimal("39.99"),
                50L
        );
    }
}