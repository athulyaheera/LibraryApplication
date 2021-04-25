package com.app.service;

import com.app.entity.Book;
import com.app.entity.Register;
import com.app.repository.BookRepository;
import com.app.repository.RegisterRepository;
import com.app.repository.UsersRepository;
import com.app.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DisplayName("Story4_LibraryServiceTest _User can return books to the library")
public class Story4_LibraryServiceTest {
	@Mock
    RegisterRepository registerRepository;
	
	@Mock
    BookRepository bookRepository;
	
	@Mock
    UsersRepository userRepository;

    @InjectMocks
    LibraryService libraryService = new LibraryService();

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("user can return book to library")
    public void bookReturnTest() throws Exception {
    	Optional<Book> optionalBook = Optional.of(new Book(1, "Dummy", "B001", "NO"));
    	Optional<Register> register = Optional.of(new Register(1, 1, "BORROWED", 1));
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(bookRepository.save(Mockito.any())).willReturn(new Book(1, "Dummy", "B001", "YES"));
    	given(registerRepository.findByBorrowedUserAndBookIdAndAction(1, 1,"BORROWED")).willReturn(register);
    	given(registerRepository.save(Mockito.any())).willReturn(new Register(1, 1, "RETURNED", 1));
    	
    	Register registerReult = libraryService.returnBook(new Register(0, 1, null, 1));
    	
    	assertEquals("RETURNED",registerReult.getAction());
    }
    
    @Test
	@DisplayName("Returned book is removed from user's borrowed list")
	public void bookReturn_removeBookFromList_Test() throws Exception {
    	
    	given(registerRepository.findByBorrowedUserAndAction(Mockito.anyInt(), Mockito.any()))
    	.willReturn(new ArrayList<Optional<Register>>());
    	given(bookRepository.findAllById(Mockito.anyIterable())).willReturn(new ArrayList<Book>());
    	
    	List<Optional<Book>> borrowedList = libraryService.viewBorrowedBooks(1);
    	
    	assertTrue(borrowedList.isEmpty());
    }
    
    @Test
	@DisplayName("library reflects returned book")
	public void bookReturn_reflectBook_Test() throws Exception {
    	
    	List <Book> books = new ArrayList<Book>();
	    books.add(new Book(1,"DUMMY","B001","YES"));
    	given(bookRepository.findAll()).willReturn(books);
    	
    	List<Book> bookList = libraryService.viewBooks();
    	
    	assertFalse(bookList.isEmpty());
    }
    
    @Test
	@DisplayName("User's borrowed list is empty when all books are returned")
	void bookReturn_emptyBorrowedList_Test() throws Exception {
    	
    	given(registerRepository.findByBorrowedUserAndAction(Mockito.anyInt(), Mockito.any()))
    	.willReturn(new ArrayList<Optional<Register>>());
    	given(bookRepository.findAllById(Mockito.anyIterable())).willReturn(new ArrayList<Book>());
    	
    	List<Optional<Book>> borrowedList = libraryService.viewBorrowedBooks(1);
    	
    	assertTrue(borrowedList.isEmpty());
    }
}
