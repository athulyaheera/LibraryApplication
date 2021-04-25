package com.app.rest;


import com.app.dto.BookDto;
import com.app.entity.Book;
import com.app.repository.BookRepository;
import com.app.repository.RegisterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)

public class LibraryAppIntegrationTestNew {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    RegisterRepository registerRepository;

    @Autowired
    ModelMapper modelMapper;

    HttpHeaders headers = new HttpHeaders();

    public ArrayList<BookDto> books = null;
    public ArrayList<BookDto> emptyBooks = null;
    public BookDto bookDto = null;

    void initBooks() {
        emptyBooks = new ArrayList<BookDto>();
        books = new ArrayList<BookDto>();
        bookDto = new BookDto(1, "B001", "Programming in C", "YES");
        books.add(bookDto);
    }



}