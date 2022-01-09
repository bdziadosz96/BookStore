package pl.bookstore.ebook.uploads.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.catalog.app.CatalogService;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase.UpdateBookCommand;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase.UpdateBookCoverCommand;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.catalog.web.CatalogController;
import pl.bookstore.ebook.uploads.app.port.UploadUseCase;
import pl.bookstore.ebook.uploads.web.UploadController.UploadResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.bookstore.ebook.catalog.app.port.CatalogUseCase.CreateBookCommand;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class UploadControllerTest {
    @Autowired
    UploadController uploadController;

    @Autowired
    CatalogController catalogController;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    UploadUseCase uploadUseCase;

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    CatalogService catalogService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void uploadFilesReturnResponseOk() throws Exception {
        //given
        Book book = catalogUseCase.addBook(givenEffectiveJavaCommand());

        //when
        catalogUseCase.updateBookCover(newUploadCoverCommand(book));

        //then
        List<UploadResponse> list = uploadController.getAll();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("jpg", list.get(0).contentType());
        Assertions.assertEquals("cover", list.get(0).fileName());
    }

    @Test
    public void shoudlReturnEmptyList() throws Exception {
        this.mockMvc.perform(get("/uploads"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void should() throws Exception {
        //when
        Book book = catalogService.addBook(givenEffectiveJavaCommand());
        catalogService.updateBookCover(newUploadCoverCommand(book));

        MvcResult result = this.mockMvc.perform(get("/uploads"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Assertions.assertTrue(content.contains("jpg"));
    }


    private UpdateBookCoverCommand newUploadCoverCommand(Book book) {
        return new UpdateBookCoverCommand(
                book.getId(),
                new byte[12],
                "jpg",
                "cover"
        );
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
        return new Author("Shannon Brian");
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