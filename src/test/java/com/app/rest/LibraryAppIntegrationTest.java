
package com.app.rest;


import com.app.dto.BookDto;
import com.app.dto.RegisterDto;
import com.app.dto.UsersDto;
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
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class LibraryAppIntegrationTest {

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
	    bookDto = new BookDto(1,"B001","Programming in C","YES");
	    books.add(bookDto);
	}

    @Test
    @DisplayName("User Can see empty list when there are no books in the library")
    public void viewEmptyBookListTest() throws Exception {
    	
    	String url ="http://localhost:"+port+"/library/viewBooks";
    	
    	ResponseEntity<List<BookDto>> responseEntity =
    	        restTemplate.exchange(url,
    	            HttpMethod.GET, null, new ParameterizedTypeReference<List<BookDto>>() {
    	            });
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }
    
    @Test
    @DisplayName("User can see list of books when books exist in library")
   // @SQLInsert(sql = "insert into BookDto values (1,'B001','Dummy','YES')")
    public void viewBookTest() throws Exception {
    	
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	
    	String url ="http://localhost:"+port+"/library/viewBooks";
    	
    	ResponseEntity<List<BookDto>> responseEntity =
    	        restTemplate.exchange(url,
    	            HttpMethod.GET, null, new ParameterizedTypeReference<List<BookDto>>() {
    	            });
    	System.out.println(responseEntity);
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
    @DisplayName("Add users to the library")
    public void addUserTest() throws Exception {
    	
    	UsersDto userDto = new UsersDto(1, "Jake");
    	String url ="http://localhost:"+port+"/library/addUser";
    	HttpEntity<UsersDto> request = new HttpEntity<UsersDto>(userDto,headers);
    	ResponseEntity<UsersDto> response = restTemplate.postForEntity
    			(url, request, UsersDto.class);
    	
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }
    
    @Test
    @DisplayName("User can borrow books from the library")
    public void borrowBookTest() throws Exception {
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", 1);
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
    	
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }
    
    
    @Test
    @DisplayName("borrowed book is added to user's borrowed list")
    public void borrowBookUsersBorrowedListTest() throws Exception {
    	
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", 1);
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
    	
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        
    	Integer userId = 1;
    	String url2 ="http://localhost:"+port+"/library/viewBorrowedBooks?userId="+userId;
    	HttpEntity<Integer> request2 = new HttpEntity<Integer>(userId,headers);
    	
        ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.GET, request2, String.class);
    	
        assertEquals(HttpStatus.OK,response2.getStatusCode());
        assertTrue(response2.getBody().contains("Programming in C"));
    }
    
    @Test
    @DisplayName("borrowed book is removed from library")
	public void borrowBook_removeFromLibrary_Test() throws Exception {
    	
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", 1);
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
    	
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        
    	String url2 ="http://localhost:"+port+"/library/viewBooks";
    	
    	ResponseEntity<List<BookDto>> responseEntity =
    	        restTemplate.exchange(url2,
    	            HttpMethod.GET, null, new ParameterizedTypeReference<List<BookDto>>() {
    	            });
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isEmpty());
    }
    
    @Test
	@DisplayName("User can not borrow more than 2 books")
	public void borrowBook_borrowLimit_Test() throws Exception {
    	
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	bookRepository.save(new Book(2,"B002","Data structures","YES"));
    	bookRepository.save(new Book(3,"B003","System Analysis and Design","YES"));
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", 1);
    	RegisterDto registerDto2 = new RegisterDto(1, 2, "BORROWED", 1);
    	RegisterDto registerDto3 = new RegisterDto(1, 3, "BORROWED", 1);
    	
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        
        HttpEntity<RegisterDto> request2 = new HttpEntity<RegisterDto>(registerDto2,headers);
        ResponseEntity<String> response2 = restTemplate.exchange(url, HttpMethod.POST, request2, String.class);
        
        assertEquals(HttpStatus.OK,response2.getStatusCode());
        assertFalse(response2.getBody().isEmpty());
        
        
        HttpEntity<RegisterDto> request3 = new HttpEntity<RegisterDto>(registerDto3,headers);
        ResponseEntity<String> response3 = restTemplate.exchange(url, HttpMethod.POST, request3, String.class);
        
        assertEquals(HttpStatus.NOT_MODIFIED,response3.getStatusCode());
    }
    
    @Test
    @DisplayName("User can borrow copy of book from the library")
    public void borrowCopyBookTest() throws Exception {
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	bookRepository.save(new Book(2,"B001","Programming in C","YES"));
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", 1);
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
    	
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }
    
    @Test
	@DisplayName("User can borrow book,when only one copy of book is left in library")
	public void borrowCopyOfBook_whenOneLeft_Test() throws Exception {
    	
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", 1);
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        
            }
    
    @Test
	@DisplayName("copies of same books are removed from library when borrowed")
	public void borrowCopyOfBook_removeFromLibrary_Test() throws Exception {
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	bookRepository.save(new Book(2,"B001","Programming in C","YES"));
    	int user1 =1;
    	int user2 =2;
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", user1);
    	RegisterDto registerDto2 = new RegisterDto(1, 2, "BORROWED", user2);
    	
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.CREATED,response.getStatusCode());

        
        HttpEntity<RegisterDto> request2 = new HttpEntity<RegisterDto>(registerDto2,headers);
        ResponseEntity<String> response2 = restTemplate.exchange(url, HttpMethod.POST, request2, String.class);
    	
        assertEquals(HttpStatus.CREATED,response2.getStatusCode());
        
        String url2 ="http://localhost:"+port+"/library/viewBooks";
    	
    	ResponseEntity<List<BookDto>> responseEntity =
    	        restTemplate.exchange(url2,
    	            HttpMethod.GET, null, new ParameterizedTypeReference<List<BookDto>>() {
    	            });
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isEmpty());

    }
    @Test
    @DisplayName("user cannot borrow more than one copy of same book")
	public void borrowCopyBook_copyLimit_Test() throws Exception {
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	bookRepository.save(new Book(2,"B001","Programming in C","YES"));
    	int user1 =1;
    	RegisterDto registerDto = new RegisterDto(1, 1, "BORROWED", user1);
    	RegisterDto registerDto2 = new RegisterDto(1, 2, "BORROWED", user1);
    	
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(registerDto,headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.CREATED,response.getStatusCode());

        
        HttpEntity<RegisterDto> request2 = new HttpEntity<RegisterDto>(registerDto2,headers);
        ResponseEntity<String> response2 = restTemplate.exchange(url, HttpMethod.POST, request2, String.class);
    	
        assertEquals(HttpStatus.NOT_MODIFIED,response2.getStatusCode());

    }
    
    @Test
    @DisplayName("User can return books to the library")
    public void returnBookTest() throws Exception {
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	RegisterDto borrowRegisterDto = new RegisterDto(1, 1, "BORROWED", 1);
    	RegisterDto returnRegisterDto = new RegisterDto(2, 1, "RETURNED", 1);
    	
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(borrowRegisterDto,headers);
    	
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        
    	String url2 ="http://localhost:"+port+"/library/returnBook";
    	HttpEntity<RegisterDto> request2 = new HttpEntity<RegisterDto>(returnRegisterDto,headers);
    	
        ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.POST, request2, String.class);
    	
        assertEquals(HttpStatus.CREATED,response2.getStatusCode());
    }
    
    @Test
    @DisplayName("Returned book is removed from user's borrowed list")
    public void bookReturn_removeBookFromList_Test() throws Exception {
    	
    	int userId =1;
    	RegisterDto borrowRegisterDto = new RegisterDto(1, 1, "BORROWED", userId);
    	RegisterDto returnRegisterDto = new RegisterDto(2, 1, "RETURNED", userId);
    	
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(borrowRegisterDto,headers);
    	
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        
    	String url2 ="http://localhost:"+port+"/library/returnBook";
    	HttpEntity<RegisterDto> request2 = new HttpEntity<RegisterDto>(returnRegisterDto,headers);
    	
        ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.POST, request2, String.class);
    	
        assertEquals(HttpStatus.CREATED,response2.getStatusCode());
        
        
    	
        String url3 ="http://localhost:"+port+"/library/viewBorrowedBooks?userId="+userId;
    	HttpEntity<Integer> request3 = new HttpEntity<Integer>(userId,headers);
    	
        ResponseEntity<String> response3 = restTemplate.exchange(url3, HttpMethod.GET, request3, String.class);
    	
        System.out.println(response3);
        assertEquals(HttpStatus.OK,response3.getStatusCode());
        assertFalse(response3.getBody().contains("Programming in C"));
    }
    
    @Test
	@DisplayName("library reflects returned book")
	public void bookReturn_reflectBook_Test() throws Exception {
    	
    	int userId =1;
    	bookRepository.save(new Book(1,"B001","Programming in C","YES"));
    	RegisterDto borrowRegisterDto = new RegisterDto(1, 1, "BORROWED", userId);
    	RegisterDto returnRegisterDto = new RegisterDto(2, 1, "RETURNED", userId);
    	
    	String url ="http://localhost:"+port+"/library/borrowBook";
    	HttpEntity<RegisterDto> request = new HttpEntity<RegisterDto>(borrowRegisterDto,headers);
    	
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    	
        //assertEquals(HttpStatus.CREATED,response.getStatusCode());
        
    	String url2 ="http://localhost:"+port+"/library/returnBook";
    	HttpEntity<RegisterDto> request2 = new HttpEntity<RegisterDto>(returnRegisterDto,headers);
    	
        ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.POST, request2, String.class);
    	
       // assertEquals(HttpStatus.CREATED,response2.getStatusCode());
        
    	String url3 ="http://localhost:"+port+"/library/viewBooks";
    	
        ResponseEntity<List<BookDto>> responseEntity =
    	        restTemplate.exchange(url3,
    	            HttpMethod.GET, null, new ParameterizedTypeReference<List<BookDto>>() {
    	            });
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }


}
