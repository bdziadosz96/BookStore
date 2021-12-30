package pl.bookstore.ebook.catalog.web;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Book;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
public class CatalogControllerWebTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    MockMvc mockMvc;


    @Test
    public void getAllBooks() throws Exception {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effectiveJava,javaPuzzlers));
        //expect

        mockMvc.perform(get("/catalog"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
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