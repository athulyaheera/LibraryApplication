package com.app.rest;

import com.app.dto.BookDto;
import com.app.dto.RegisterDto;
import com.app.service.LibraryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Story1_LibraryAppIntegrationTest _User can view books in the library")
public class Story1_LibraryAppIntegrationTest {

	@LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    LibraryService libraryService;
    
    @Autowired
    ModelMapper modelMapper;
    
    HttpHeaders headers = new HttpHeaders();
    
    public ArrayList<BookDto> books = null;
    public BookDto bookDto = null;
    void initBooks() {
	    books = new ArrayList<BookDto>();
	    bookDto = new BookDto(1,"B001","Programming in C","YES");
	    books.add(bookDto);
	}

    @Test
    @DisplayName("User can view books in the library")
    public void viewBookTest() throws Exception {
    	
    	String url ="http://localhost:"+port+"/library/viewBooks";
    	
    	ResponseEntity<List<BookDto>> responseEntity =
    	        restTemplate.exchange(url,
    	            HttpMethod.GET, null, new ParameterizedTypeReference<List<BookDto>>() {
    	            });
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }
    
    
    @Test
    @DisplayName("Adds books to the library")
    public void addBookTest() throws Exception {
    	
    	initBooks();
    	String url ="http://localhost:"+port+"/library/addBook";
    	HttpEntity<BookDto> request = new HttpEntity<BookDto>(bookDto,headers);
    	ResponseEntity<BookDto> response = restTemplate.postForEntity
    			(url, request, BookDto.class);
    	
    	
        		
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    @DisplayName("User can borrow copy of book from the library")
    public void borrowBookTest() throws Exception {
        RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", 1);
        String url ="http://localhost:"+port+"/library/borrowBook";
        HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        assertFalse(response.getBody().isEmpty());
    }

}
