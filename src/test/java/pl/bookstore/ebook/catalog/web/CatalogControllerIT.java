package pl.bookstore.ebook.catalog.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.CreateBookCommand;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class CatalogControllerIT {

    @Autowired
    CatalogController catalogController;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Test
    public void getAllBooks() {
        //given
        givenEffectiveJava();
        givenJavaPuzzlers();


        //when
        List<Book> all = catalogController.getAll(Optional.empty(), Optional.empty());

        //then

        assertEquals(2, all.size());
    }

    @Test
    public void addBook_thenFindByTitle_thenReturnEqualTitleAndSize() {
        //given
        givenEffectiveJava();
        givenJavaPuzzlers();


        //when
        List<Book> bloch = catalogController.getAll(Optional.of("Effective Java"), Optional.empty());

        //then

        assertEquals(1, bloch.size());
        assertEquals("Effective Java", bloch.get(0).getTitle());
    }

    @Test
    public void addBook_thenFindByAuthor_thenReturnEqualTitleAndSize() {
        //given
        givenEffectiveJava();
        givenJavaPuzzlers();


        //when
        List<Book> connor = catalogController.getAll(Optional.empty(), Optional.of("Steven Connor"));

        //then

        assertEquals(connor.size(), 1);
        assertEquals(connor.get(0).getTitle(), "Java Puzzlers");
        assertNotEquals(connor.get(0).getTitle(), "Effective Java");
    }

    private void givenJavaPuzzlers() {
        Author steven_connor = authorJpaRepository.save(new Author("Steven Connor"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Puzzlers",
                Set.of(steven_connor.getId()),
                2010,
                new BigDecimal("39.99"),
                50L
        ));
    }

    private void givenEffectiveJava() {
        Author joshua_bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Effective Java",
                Set.of(joshua_bloch.getId()),
                2005,
                new BigDecimal("19.99"),
                50L
        ));
    }
}