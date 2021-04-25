package com.app.service;

import com.app.entity.Book;
import com.app.repository.BookRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
@DisplayName("Story1_LibraryServiceTest _User can view books in the library")
public class Story1_LibraryServiceTest {

	@Mock
    BookRepository bookRepository;

    @InjectMocks
    LibraryService libraryService = new LibraryService();

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    public Book book = null;
    public ArrayList<Book> books = null;
    void initBooks() {
    	books = new ArrayList<Book>();
	    book = new Book(1,"B001","Programming in C","YES");
	    books.add(book);
	    
	}
    
    
    @Test
    @DisplayName("Can see list of books when books exist in library")
    public void viewBooks_test() {
    	initBooks();
    	given(bookRepository.findAll()).willReturn(books);
    	
    	List<Book> bookList = libraryService.viewBooks();
    	
        assertEquals(book,bookList.get(0));

    }
    
    @Test
    @DisplayName("User Can see empty list when there are no books in the library")
    public void viewEmptyBooks_test() {
    	given(bookRepository.findAll()).willReturn(new ArrayList<Book>());
    	
    	List<Book> bookList = libraryService.viewBooks();
    	
        assertTrue(bookList.isEmpty());

    }
    
    @Test
    @DisplayName("Adding books to library")
    public void addBooks_test() {
    	initBooks();
    	given(bookRepository.save(Mockito.any(Book.class))).willReturn(book);
    	
    	Book returnBook = libraryService.addBook(book);
    	
        assertEquals(book,returnBook);

    }
}
