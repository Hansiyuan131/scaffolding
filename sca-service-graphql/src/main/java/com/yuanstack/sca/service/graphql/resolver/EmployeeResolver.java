package com.yuanstack.sca.service.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.yuanstack.sca.service.graphql.model.Author;
import com.yuanstack.sca.service.graphql.model.Book;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hansiyuan
 */
@Component
public class EmployeeResolver implements GraphQLQueryResolver {

    public List<Book> books() {
        Author author = Author.builder()
                .email("1111")
                .firstName("11111")
                .lastName("222222")
                .phone("121212121")
                .picture("1212121212")
                .build();
        List<Author> authors = new ArrayList<>();
        authors.add(author);
        Book book = Book.builder()
                .authors(authors)
                .isbn("!11")
                .pageCount(11)
                .build();
        List<Book> books = new ArrayList<>();
        books.add(book);
        return books;
    }

    public List<Author> authors() {
        Author author = Author.builder()
                .email("1111")
                .firstName("11111")
                .lastName("222222")
                .phone("121212121")
                .picture("1212121212")
                .build();
        List<Author> authors = new ArrayList<>();
        authors.add(author);
        return authors;
    }
}
