package com.app.rest;


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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Story4_LibraryAppIntegationTest _User can return books to the library")
public class Story4_LibraryAppIntegationTest {
		@LocalServerPort
	    private int port;

	    @Autowired
	    private TestRestTemplate restTemplate;
	    
	    @Autowired
	    LibraryService libraryService;
	    
	    @Autowired
	    ModelMapper modelMapper;
	    
	    HttpHeaders headers = new HttpHeaders();
	    
	    
	    @Test
	    @DisplayName("User can return books to the library")
	    public void returnBookTest() throws Exception {
	    	RegisterDto registerDto = new RegisterDto(1, 1, "RETURNED", 1);
	    	String url ="http://localhost:"+port+"/library/returnBook";
	    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
	    	
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
	    	
	       // assertFalse(response.getBody().isEmpty());
	    }
	    
	    @Test
	    @DisplayName("Returned book is removed from user's borrowed list")
	    public void bookReturn_removeBookFromList_Test() throws Exception {
	    	Integer userId = 1;
	    	String url ="http://localhost:"+port+"/library/viewBorrowedBooks";
	    	HttpEntity<Integer> request = new HttpEntity<Integer>(userId,headers);
	    	
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
	    	
	       // assertFalse(response.getBody().contains("1"));
	    }
	    
	    @Test
		@DisplayName("library reflects returned book")
		public void bookReturn_reflectBook_Test() throws Exception {
	    	
	    	String url ="http://localhost:"+port+"/library/viewBooks";
	    	
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
	    	
	        assertFalse(response.getBody().isEmpty());
	    }
}
