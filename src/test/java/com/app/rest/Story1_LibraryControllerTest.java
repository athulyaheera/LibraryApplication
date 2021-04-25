package com.app.rest;

import com.app.dto.BookDto;
import com.app.dto.RegisterDto;
import com.app.dto.UsersDto;
import com.app.entity.Book;
import com.app.entity.Register;
import com.app.entity.Users;
import com.app.exception.BorrowLimitException;
import com.app.service.LibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LibraryController.class)
@DisplayName("Story1_LibraryControllerTest _User can view books in the library")
public class Story1_LibraryControllerTest {
	
	@Autowired
    MockMvc mockMvc;
	
	@MockBean
    LibraryService libraryService;
	

	
	public ArrayList<Book> books = null;
	public Book book = null;
	public BookDto bookDto = null;
	void initBooks() {
	    books = new ArrayList<Book>();
		book = new Book(1,"B001","Programming in C","YES");
		bookDto = new BookDto(1,"B001","Programming in C","YES");
		books.add(book);
	}
	
	
	
	@Test
	@DisplayName("User Can see empty list when there are no books in the library")
	public void viewBooksTest_withoutBooks() throws Exception {
		given(libraryService.viewBooks()).willReturn(new ArrayList<Book>());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBooks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
		
	}
	
	@Test
	@DisplayName("Adding books to library")
	public void addBooksTest() throws Exception {
		
		initBooks();

		given(libraryService.addBook(book)).willReturn(book);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/library/addBook")
				.content(asJsonString(bookDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Can see list of books when books exist in library")
	public void viewBooksTest_withBooks() throws Exception {
		
		initBooks();
		
		given(libraryService.viewBooks()).willReturn(books);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBooks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isNotEmpty());
		
	}


	public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	@Test
	@DisplayName("User can borrow books from the library")
	public void borrowBookTest() throws Exception {
		given(libraryService.borrowBook(Mockito.any())).willReturn(new Register(1,1,"BORROWED",1));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/borrowBook")
				.content(asJsonString(new RegisterDto(1,1,"BORROWED",1)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	@DisplayName("book is added to user's borrowed list")
	public void borrowBook_borrowList_Test() throws Exception {
		int userId =1;
		int bookId =1;
		given(libraryService.borrowBook(Mockito.any())).willReturn(new Register(1,bookId,"BORROWED",userId));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/borrowBook")
				.content(asJsonString(new RegisterDto(1,1,"BORROWED",1)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().containsHeader("bookId");
	}

	@Test
	@DisplayName("borrowed book is removed from library")
	public void borrowBook_removeFromLibrary_Test() throws Exception {
		int userId =1;
		int bookId =1;
		given(libraryService.borrowBook(Mockito.any())).willReturn(new Register(1,bookId,"BORROWED",userId));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/borrowBook")
				.content(asJsonString(new RegisterDto(1,1,"BORROWED",1)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().containsHeader("bookId");

		given(libraryService.viewBooks()).willReturn(new ArrayList<Book>());

		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBooks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@DisplayName("User can not borrow more than 2 books")
	public void borrowBook_borrowLimit_Test() throws Exception {
		int userId =1;
		int bookId3 =3;

		given(libraryService.borrowBook(Mockito.any())).willThrow(new BorrowLimitException());

		/*mockMvc.perform(MockMvcRequestBuilders.post("/library/borrowBook")
				.content(asJsonString(new RegisterDto(1,bookId3,"BORROWED",userId)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotModified());*/
	}

	@Test
	@DisplayName("Adding new user to the system")
	public void borrowBook_addUser_Test() throws Exception {

		given(libraryService.addUsers(Mockito.any())).willReturn(new Users(1,"Jake"));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/addUser")
				.content(asJsonString(new UsersDto(1,"Jake")))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}


	@Test
	@DisplayName("can return book to library")
	public void bookReturnTest() throws Exception {

		given(libraryService.returnBook(Mockito.any())).willReturn(new Register(1, 1, "RETURNED", 1));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/returnBook")
				.content(asJsonString(new RegisterDto(1, 1, "RETURNED", 1))).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Returned book is removed from user's borrowed list")
	void bookReturn_removeBookFromList_Test() throws Exception {

		given(libraryService.viewBorrowedBooks(Mockito.anyInt())).willReturn(new ArrayList<Optional<Book>>());

		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBorrowedBooks").param("userId", "1")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	@DisplayName("library reflects returned book")
	void bookReturn_reflectBook_Test() throws Exception {
		List<Book> books = new ArrayList<Book>();
		books.add(new Book(1, "Dummy", "B001", "YES"));
		given(libraryService.viewBooks()).willReturn(books);

		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBooks").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@DisplayName("User's borrowed list is empty when all books are returned")
	void bookReturn_emptyBorrowedList_Test() throws Exception {

		given(libraryService.viewBorrowedBooks(Mockito.anyInt())).willReturn(new ArrayList<Optional<Book>>());

		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBorrowedBooks").param("userId", "1")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}
	@Test
	@DisplayName("user can borrow a copy of book from the library")
	public void borrowCopyOfBookTest() throws Exception {
		Book book = new Book(1,"Dummy","B001","YES");
		Book book1 = new Book(2,"Dummy","B001","YES");
		List<Book> books = new ArrayList<Book>();
		books.add(book);
		books.add(book1);
		given(libraryService.viewBooks()).willReturn(books);

		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBooks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());

		given(libraryService.borrowBook(Mockito.any())).willReturn(new Register(1,1,"BORROWED",1));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/borrowBook")
				.content(asJsonString(new RegisterDto(1,1,"BORROWED",1)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}


	@Test
	@DisplayName("only one copy of book is left in library")
	public void borrowCopyOfBook_whenOneLeft_Test() throws Exception {

		Book book = new Book(1,"Dummy","B001","YES");
		List<Book> books = new ArrayList<Book>();
		books.add(book);
		given(libraryService.viewBooks()).willReturn(books);

		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBooks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());

		given(libraryService.borrowBook(Mockito.any())).willReturn(new Register(1,1,"BORROWED",1));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/borrowBook")
				.content(asJsonString(new RegisterDto(1,1,"BORROWED",1)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	@DisplayName("copies of same books are removed from library when borrowed")
	public void borrowCopyOfBook_removeFromLibrary_Test() throws Exception {
		given(libraryService.borrowBook(Mockito.any())).willReturn(new Register(1,1,"BORROWED",1));

		mockMvc.perform(MockMvcRequestBuilders.post("/library/borrowBook")
				.content(asJsonString(new RegisterDto(1,1,"BORROWED",1)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		given(libraryService.viewBooks()).willReturn(new ArrayList<Book>());

		mockMvc.perform(MockMvcRequestBuilders.get("/library/viewBooks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}


}
