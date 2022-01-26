package pl.bookstore.ebook.catalog.web;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.domain.Author;
import pl.bookstore.ebook.catalog.domain.Book;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogControllerApiTest {

    @LocalServerPort
    int portNumber;

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void getAllBooks() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effectiveJava,javaPuzzlers));
        ParameterizedTypeReference<List<Book>> parameter = new ParameterizedTypeReference<>() {};

        //when
        String url = "http://localhost:" + portNumber + "/catalog";
        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost:" + portNumber + "/catalog")).build();
        ResponseEntity<List<Book>> response = restTemplate.exchange(request, parameter);

        //then
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void findByAuthorWhenAutorIsNotFound_thenReturnEmptyList() {
        //given
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effectiveJava,javaPuzzlers));
        ParameterizedTypeReference<List<Book>> parameter = new ParameterizedTypeReference<>() {};

        //when
        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost:" + portNumber + "/catalog?title=Testowy")).build();
        ResponseEntity<List<Book>> response = restTemplate.exchange(request, parameter);

        //then
        assertNotEquals(2, response.getBody().size());
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void findByAuthorWhenAutorIsPresent_thenReturnListContainsBook() {
        //given
        Author author = new Author("Testowy");
        Book effectiveJava = givenEffectiveJava();
        Book javaPuzzlers = givenJavaPuzzlers();
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effectiveJava,javaPuzzlers));
        Mockito.when(catalogUseCase.findByAuthor(author.getName())).thenReturn(List.of(effectiveJava));
        ParameterizedTypeReference<List<RestBook>> parameter = new ParameterizedTypeReference<>() {};

        //when
        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost:" + portNumber + "/catalog?author=Testowy")).build();
        ResponseEntity<List<RestBook>> response = restTemplate.exchange(request, parameter);

        //then
        assertNotEquals(2, response.getBody().size());
        assertNotEquals(0, response.getBody().size());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().toString().contains("title=Effective Java"));
        assertTrue(response.getStatusCode().is2xxSuccessful());
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