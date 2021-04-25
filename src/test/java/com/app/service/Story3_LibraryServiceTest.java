package com.app.service;

import com.app.entity.Book;
import com.app.entity.Register;
import com.app.exception.CopyLimitException;
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
@DisplayName("Story3_LibraryServiceTest _User can borrow a copy of book from library when there are two")
public class Story3_LibraryServiceTest {

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
    public Book book = null;
    public Book book1 = null;
    public ArrayList<Book> books = null;
    public List <Optional<Register>> registerList = null;
    public List <Optional<Register>> registerListSingle = null;
    void initBooks() {
    	books = new ArrayList<Book>();
	    book = new Book(1,"Programming in C","B001","YES");
	    book1 = new Book(2,"Programming in C","B001","YES");
	    books.add(book);
	    
	}
    void initRegisterList() {
    	Optional<Register> register1 = Optional.of(new Register(1, 1, "BORROWED", 1));
    	registerList = new ArrayList<Optional<Register>>();
    	registerList.add(register1);
    	registerListSingle=  new ArrayList<Optional<Register>>();
    }
    
    @Test
    @DisplayName("can borrow a copy of book from the library")
    public void borrowCopyOfBookTest() throws Exception{
    	initBooks();
    	initRegisterList();
    	Optional<Book> optionalBook = Optional.of(books.get(0));
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerListSingle);
    	given(bookRepository.save(Mockito.any())).willReturn(new Book(1, "Programming in C", "B001", "NO"));
    	given(registerRepository.save(Mockito.any())).willReturn(new Register(1, 1, "BORROWED", 1));
    	
    	Register register = libraryService.borrowBook(new Register(1, 1, "BORROWED", 1));
    	
        assertEquals(new Register(1, 1, "BORROWED", 1).getBorrowedUser(),register.getBorrowedUser());
        

    }
    @Test
    @DisplayName("only one copy of book is left in library")
	public void borrowCopyOfBook_whenOneLeft_Test() throws Exception {
    	initBooks();
    	initRegisterList();
    	Optional<Book> optionalBook = Optional.of(books.get(0));
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerListSingle);
    	given(bookRepository.save(Mockito.any())).willReturn(new Book(1, "Programming in C", "B001", "NO"));
    	given(registerRepository.save(Mockito.any())).willReturn(new Register(1, 1, "BORROWED", 1));
    	
    	Register register = libraryService.borrowBook(new Register(1, 1, "BORROWED", 1));
    	
        assertEquals(new Register(1, 1, "BORROWED", 1).getBorrowedUser(),register.getBorrowedUser());
        

    }
    
    @Test
    @DisplayName("copies of same books are removed from library when borrowed")
    public void borrowCopyBook_removeFromLibrary_Test() throws Exception{
    	
    	initBooks();
    	initRegisterList();
    	Optional<Book> optionalBook = Optional.of(books.get(0));
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerListSingle);
    	given(bookRepository.save(Mockito.any())).willReturn(new Book(2, "Programming in C", "B001", "NO"));
    	given(registerRepository.save(Mockito.any())).willReturn(new Register(1, 2, "BORROWED", 1));
    	Register register = libraryService.borrowBook(new Register(1, 1, "BORROWED", 1));
    	
    	initBooks();
    	given(bookRepository.findAll()).willReturn(books);
    	List<Book> bookList = libraryService.viewBooks();
    	
        assertFalse(register.getBookId().equals(bookList.get(0).getId()));
        

    }
    
    @Test
    @DisplayName("user cannot borrow more than one copy of same book")
	public void borrowCopyBook_copyLimit_Test() throws Exception {
    	initBooks();
    	initRegisterList();
    	Optional<Book> optionalBook1 = Optional.of(books.get(0));
    	Optional<Book> optionalBook2 = Optional.of(book1);
    	
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook2);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerList);
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook1);
    	assertThrows(CopyLimitException.class, ()-> libraryService.borrowBook(new Register(2, 2, "BORROWED", 1)));
        

    }
}
