package pl.bookstore.ebook.catalog.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CatalogController.class)
class CatalogControllerTest {

    @Autowired
    CatalogController catalogController;

    @MockBean
    CatalogUseCase catalogUseCase;

    @MockBean
    AuthorJpaRepository repository;

    @Test
    public void getAllBooks() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effectiveJava, javaPuzzlers));

        //when
        List<Book> all = catalogController.getAll(Optional.empty(), Optional.empty());

        //then
        assertEquals(2, all.size());
    }

    @Test
    public void addBook_thenFindByTitle_thenReturnEqualTitleAndSize() {
        //given
        Author author = givenAuthor();
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Mockito.when(catalogUseCase.findByTitle(effectiveJava.getTitle())).thenReturn(List.of(effectiveJava));


        //when
        List<Book> bookList = catalogController.getAll(Optional.of("Effective Java"), Optional.empty());

        //then

        assertEquals(1, bookList.size());
        assertEquals("Effective Java", bookList.get(0).getTitle());
    }

    @Test
    public void addBook_thenFindByAuthor_thenReturnEqualTitleAndSize() {
        //given
        Author author = givenAuthor();
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Mockito.when(catalogUseCase.findByAuthor(author.getName())).thenReturn(List.of(effectiveJava));

        //when
        List<Book> bookList = catalogController.getAll(Optional.empty(), Optional.of("Shannon Brian"));

        //then
        assertEquals(1, bookList.size());
        assertEquals("Effective Java", bookList.get(0).getTitle());
    }


    private Author givenAuthor() {
        return new Author("Shannon Brian");
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