package com.app.service;

import com.app.entity.Book;
import com.app.entity.Register;
import com.app.entity.Users;
import com.app.exception.BorrowLimitException;
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

@DisplayName("Story2_LibraryServiceTest _User can borrow a book from the library")
public class Story2_LibraryServiceTest {
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
    public ArrayList<Book> books = null;
    public List <Optional<Register>> registerList = null;
    public List <Optional<Register>> registerList1 = null;
    void initBooks() {
    	books = new ArrayList<Book>();
	    book = new Book(2,"Programming in C","B001","YES");
	    books.add(book);
	    
	}
    void initRegisterList() {
    	Optional<Register> register1 = Optional.of(new Register(1, 1, "BORROWED", 1));
    	Optional<Register> register2 = Optional.of(new Register(2, 2, "BORROWED", 1));
    	registerList = new ArrayList<Optional<Register>>();
    	registerList.add(register1);
    	registerList.add(register2);
    	registerList1=  new ArrayList<Optional<Register>>();

    }
    @Test
    @DisplayName("User can borrow books from the library")
    public void borrowBookTest() throws Exception{
    	initBooks();
    	initRegisterList();
    	Optional<Book> optionalBook = Optional.of(book);
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerList1);
    	given(bookRepository.save(Mockito.any())).willReturn(new Book(1, "Programming in C", "B001", "NO"));
    	given(registerRepository.save(Mockito.any())).willReturn(new Register(1, 1, "BORROWED", 1));
    	
    	Register register = libraryService.borrowBook(new Register(1, 1, "BORROWED", 1));
    	
        assertEquals(new Register(1, 1, "BORROWED", 1).getBorrowedUser(),register.getBorrowedUser());
        

    }
    
    @Test
    @DisplayName("book is added to user's borrowed list")
    public void borrowBook_borrowList_Test() throws Exception{
    	
    	initBooks();
    	initRegisterList();
    	Optional<Book> optionalBook = Optional.of(book);
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerList1);
    	given(bookRepository.save(Mockito.any())).willReturn(new Book(1, "Programming in C", "B001", "NO"));
    	given(registerRepository.save(Mockito.any())).willReturn(new Register(1, 1, "BORROWED", 1));
    	
    	Register register = libraryService.borrowBook(new Register(1, 1, "BORROWED", 1));
    	
        assertEquals(new Register(1, 1, "BORROWED", 1).getBookId(),register.getBookId());
        

    }
    
    @Test
    @DisplayName("borrowed book is removed from library")
    public void borrowBook_removeFromLibrary_Test() throws Exception{
    	
    	initBooks();
    	initRegisterList();
    	Optional<Book> optionalBook = Optional.of(book);
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerList1);
    	given(bookRepository.save(Mockito.any())).willReturn(new Book(1, "Programming in C", "B001", "NO"));
    	given(registerRepository.save(Mockito.any())).willReturn(new Register(1, 1, "BORROWED", 1));
    	Register register = libraryService.borrowBook(new Register(1, 1, "BORROWED", 1));
    	
    	initBooks();
    	given(bookRepository.findAll()).willReturn(books);
    	List<Book> bookList = libraryService.viewBooks();
    	
        assertFalse(register.getBookId().equals(bookList.get(0).getId()));
        

    }
    
    @Test
    @DisplayName("User can not borrow more than 2 books")
    public void borrowBook_copyLimit_Test() throws Exception{
    	initBooks();
    	initRegisterList();
    	books.add(new Book(3, "Harry Potter chapter 1", "B003", "NO"));
    	Optional<Book> optionalBook = Optional.of(book);
    	
    	given(bookRepository.findById(Mockito.any())).willReturn(optionalBook);
    	given(registerRepository.findByBorrowedUserAndAction(1, "BORROWED")).willReturn(registerList);
    	
    	assertThrows(BorrowLimitException.class, ()-> libraryService.borrowBook(new Register(3, 3, "BORROWED", 1)));
        

    }
    
    @Test
    @DisplayName("Adding new user to the system")
    public void addUser_Test() {
    	Users newUser = new Users(1,"Jake"); 
    	given(userRepository.save(Mockito.any())).willReturn(newUser);
    	
    	Users user = libraryService.addUsers(newUser);
    	
    	assertEquals(newUser, user);
        

    }
}
