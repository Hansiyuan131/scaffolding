package com.yuanstack.sca.service.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author SEVEN
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {
    private String isbn;
    private String title;
    private int pageCount;
    private List<Author> authors;
}