package com.app.repository;


import com.app.entity.Book;
import com.app.entity.Register;
import com.app.entity.Users;
import com.app.exception.BorrowLimitException;
import com.app.exception.CopyLimitException;
import com.app.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DisplayName("BookRepositoryTest _User can view books in the library")
public class BookRepositoryTest {
	
	 @Autowired
	 BookRepository bookRepository;

	@Autowired
	RegisterRepository registerRepository;

	@Autowired
	UsersRepository usersRepository;
	 
	 
	 public ArrayList<Book> books = null;
	    @BeforeEach
	    void initBooks() {
		    books = new ArrayList<Book>();
			books.add(new Book(1,"B001","Programming in C","YES"));
			books.add(new Book(2,"B002","Harry Potter Chapter 1","YES"));
			books.add(new Book(3,"B003","System Analysis And Design","YES"));
			books.add(new Book(4,"B003","System Analysis And Design","YES"));
		}
	 
	     @Test
		 @DisplayName("User Can see empty list when there are no books in the library")
		    public void test_FindBooksByIsAvailableFlag_withOutBooks() {
			List<Book> books = bookRepository.findAll();
		    assertTrue(!books.isEmpty());
		    }
	    @Test
	    @DisplayName("Adding books to library")
	    public void test_addBooks() {
		 List<Book> bookList= (List<Book>) bookRepository.saveAll(books);
	     assertTrue(!bookList.isEmpty());
	    }
	
	    @Test
	    @DisplayName("Can see list of books when books exist in library")
	    public void test_FindBooksByIsAvailableFlag_withBooks() {
		 bookRepository.saveAll(books);
		 List<Book> books = bookRepository.findAll();
	     assertTrue(!books.isEmpty());
	    }

	@Test
	@DisplayName("User can borrow books from the library")
	public void borrowBookTest() throws Exception{

		Book book = bookRepository.save(new Book(1,"Programming in C","B001","YES"));

		Users user = usersRepository.save(new Users(1,"Jake"));

		Optional<Book> bookToBorrow = bookRepository.findById(book.getId());
		bookToBorrow.get().setIsAvailable("NO");
		Book borrowedBook = bookRepository.save(bookToBorrow.get());
		List<Optional<Register>> registerEntries =  registerRepository.findByBorrowedUserAndAction(user.getId(), "BORROWED");

		Register borrowEntry = registerRepository.save(new Register(1, book.getId(), "BORROWED", user.getId()));

		assertTrue(bookToBorrow.isPresent());
		assertTrue(registerEntries.size()<2);
		assertTrue(borrowedBook.getIsAvailable().equals("NO"));
		assertTrue(borrowEntry.getAction().equals("BORROWED"));
	}

	@Test
	@DisplayName("book is added to user's borrowed list")
	public void borrowBook_borrowList_Test() throws Exception{

		Book book = bookRepository.save(new Book(1,"Programming in C","B001","YES"));

		Users user = usersRepository.save(new Users(1,"Jake"));

		Optional<Book> bookToBorrow = bookRepository.findById(book.getId());
		bookToBorrow.get().setIsAvailable("NO");
		Book borrowedBook = bookRepository.save(bookToBorrow.get());

		Register borrowEntry = registerRepository.save(new Register(1, book.getId(), "BORROWED", user.getId()));

		List<Optional<Register>> registerEntries = registerRepository.findByBorrowedUserAndAction(user.getId(), "BORROWED");

		List<Book> borrowedList = (List<Book>) bookRepository.findAllById(registerEntries.stream()
				.map(register -> register.get().getBookId()).collect(Collectors.toList()));

		assertTrue(borrowedList.contains(borrowedBook));
		assertTrue(borrowEntry.getAction().equals("BORROWED"));
	}

	@Test
	@DisplayName("borrowed book is removed from library")
	public void borrowBook_removeFromLibrary_Test() throws Exception{

		Book book = bookRepository.save(new Book(1,"Programming in C","B001","YES"));

		Users user = usersRepository.save(new Users(1,"Jake"));

		Optional<Book> bookToBorrow = bookRepository.findById(book.getId());
		bookToBorrow.get().setIsAvailable("NO");
		Book borrowedBook = bookRepository.save(bookToBorrow.get());

		Register borrowEntry = registerRepository.save(new Register(1, book.getId(), "BORROWED", user.getId()));

		assertTrue(borrowedBook.getIsAvailable().equals("NO"));
		assertTrue(borrowEntry.getAction().equals("BORROWED"));
	}

	@Test
	@DisplayName("User can not borrow more than 2 books")
	public void borrowBook_borrowLimit_Test() throws Exception{

		registerRepository.save(new Register(1, 1, "BORROWED", 1));
		registerRepository.save(new Register(2, 2, "BORROWED", 1));

		List<Optional<Register>> borrowEntries = registerRepository.findByBorrowedUserAndAction(1, "BORROWED");
		Object result = null;
		if(borrowEntries.size() >= 2) {
			result =new BorrowLimitException();
		} else {
			result = registerRepository.save(new Register(3, 3, "BORROWED", 1));
		}
		assertTrue(result.getClass().equals(BorrowLimitException.class));
	}

	@Test
	@DisplayName("Add new user to the system")
	public void addUser_Test() throws Exception{

		Users user = usersRepository.save(new Users(1,"Jake"));

		assertTrue(user.getName().equals("Jake"));
	}


	@Test
	@DisplayName("can borrow a copy of book from the library")
	public void borrowCopyOfBookTest() throws Exception{

		Book book = bookRepository.save(new Book(1,"Programming in C","B001","YES"));

		Book book2 = bookRepository.save(new Book(2,"Programming in C","B001","YES"));

		Users user = usersRepository.save(new Users(1,"Jake"));

		Optional<Book> bookToBorrow = bookRepository.findById(book.getId());
		bookToBorrow.get().setIsAvailable("NO");
		Book borrowedBook = bookRepository.save(bookToBorrow.get());
		List<Optional<Register>> registerEntries =  registerRepository.findByBorrowedUserAndAction(user.getId(), "BORROWED");

		Register borrowEntry = registerRepository.save(new Register(1, book.getId(), "BORROWED", user.getId()));

		assertTrue(bookToBorrow.isPresent());
		assertTrue(registerEntries.size()<2);
		assertTrue(borrowedBook.getIsAvailable().equals("NO"));
		assertTrue(borrowEntry.getAction().equals("BORROWED"));
	}

	@Test
	@DisplayName("only one copy of book is left in library")
	public void borrowCopyOfBook_whenOneLeft_Test() throws Exception {

		Book book = bookRepository.save(new Book(1,"Programming in C","B001","YES"));

		Users user = usersRepository.save(new Users(1,"Jake"));

		Optional<Book> bookToBorrow = bookRepository.findById(book.getId());
		bookToBorrow.get().setIsAvailable("NO");
		Book borrowedBook = bookRepository.save(bookToBorrow.get());
		List<Optional<Register>> registerEntries =  registerRepository.findByBorrowedUserAndAction(user.getId(), "BORROWED");

		Register borrowEntry = registerRepository.save(new Register(1, book.getId(), "BORROWED", user.getId()));

		assertTrue(bookToBorrow.isPresent());
		assertTrue(registerEntries.size()<2);
		assertTrue(borrowedBook.getIsAvailable().equals("NO"));
		assertTrue(borrowEntry.getAction().equals("BORROWED"));
	}

	@Test
	@DisplayName("copies of same books are removed from library when borrowed")
	public void borrowCopyBook_removeFromLibrary_Test() throws Exception{

		Book book = bookRepository.save(new Book(1,"Programming in C","B001","YES"));
		Book book2 = bookRepository.save(new Book(2,"Programming in C","B001","YES"));

		Users user = usersRepository.save(new Users(1,"Jake"));
		Users user2 = usersRepository.save(new Users(1,"Jay"));

		Optional<Book> bookToBorrow = bookRepository.findById(book.getId());
		bookToBorrow.get().setIsAvailable("NO");
		Book borrowedBook = bookRepository.save(bookToBorrow.get());
		Register borrowEntry = registerRepository.save(new Register(1, book.getId(), "BORROWED", user.getId()));

		Optional<Book> bookToBorrow2 = bookRepository.findById(book2.getId());
		bookToBorrow2.get().setIsAvailable("NO");
		Book borrowedBook2 = bookRepository.save(bookToBorrow.get());
		Register borrowEntry2 = registerRepository.save(new Register(2, book2.getId(), "BORROWED", user2.getId()));

		assertTrue(borrowedBook.getIsAvailable().equals("NO"));
		assertTrue(borrowedBook2.getIsAvailable().equals("NO"));
		assertTrue(borrowEntry.getAction().equals("BORROWED"));
		assertTrue(borrowEntry2.getAction().equals("BORROWED"));
	}

	@Test
	@DisplayName("user cannot borrow more than one copy of same book")
	public void borrowCopyBook_copyLimit_Test() throws Exception {

		Book book = bookRepository.save(new Book(1,"Programming in C","B001","YES"));
		Book bookToBorrow = bookRepository.save(new Book(2,"Programming in C","B001","YES"));

		registerRepository.save(new Register(1, book.getId(), "BORROWED", 1));
		List<Optional<Register>> borrowEntries = registerRepository.findByBorrowedUserAndAction(1, "BORROWED");
		Optional<Book> bookBorrowed= bookRepository.findById(borrowEntries.get(0).get().getBookId());

		Object result = null;
		if(borrowEntries.size() == 1 && bookBorrowed.get().getBookCode().equals(bookToBorrow.getBookCode())) {
			result =new CopyLimitException();
		} else {
			result = registerRepository.save(new Register(2, 2, "BORROWED", 1));
		}
		assertTrue(result.getClass().equals(CopyLimitException.class));
	}
	@Test
	@DisplayName("user can return book to library")
	public void bookReturnTest() throws Exception {
		Book book =bookRepository.save(new Book(1,"Dummy","B001","NO"));
		Users user =usersRepository.save(new Users(1,"Jake"));
		registerRepository.save(new Register(1, book.getId(), "BORROWED", user.getId()));

		Optional<Register> borrowEntry = registerRepository.findByBorrowedUserAndBookIdAndAction(user.getId(), book.getId(), "BORROWED");
		borrowEntry.get().setAction("RETURNED");
		Optional<Book> borrowedBook = bookRepository.findById(borrowEntry.get().getBookId());
		borrowedBook.get().setIsAvailable("YES");

		Book returnedBook =bookRepository.save(borrowedBook.get());
		Register returnEntry = registerRepository.save(borrowEntry.get());

		assertEquals("YES", returnedBook.getIsAvailable());
		assertEquals("RETURNED",returnEntry.getAction());

	}

	@Test
	@DisplayName("Returned book is removed from user's borrowed list")
	void bookReturn_removeBookFromList_Test() throws Exception {

		Book book = bookRepository.save(new Book(2,"Dummy","B001","NO"));
		Users user= usersRepository.save(new Users(2,"Jake"));
		registerRepository.save(new Register(2, book.getId(), "BORROWED", user.getId()));

		Optional<Register> borrowEntry = registerRepository.findByBorrowedUserAndBookIdAndAction(user.getId(), book.getId(), "BORROWED");
		borrowEntry.get().setAction("RETURNED");
		Optional<Book> borrowedBook = bookRepository.findById(borrowEntry.get().getBookId());
		borrowedBook.get().setIsAvailable("YES");

		bookRepository.save(borrowedBook.get());
		registerRepository.save(borrowEntry.get());

		Optional<Register> borrowEntryLatest = registerRepository.findByBorrowedUserAndBookIdAndAction(user.getId(), book.getId(), "BORROWED");

		assertTrue(borrowEntryLatest.isEmpty());

	}

	@Test
	@DisplayName("library reflects returned book")
	public void bookReturn_reflectBook_Test() throws Exception {

		Book book = bookRepository.save(new Book(2,"Dummy","B001","NO"));
		Users user= usersRepository.save(new Users(2,"Jake"));
		registerRepository.save(new Register(2, book.getId(), "BORROWED", user.getId()));

		Optional<Register> borrowEntry = registerRepository.findByBorrowedUserAndBookIdAndAction(user.getId(), book.getId(), "BORROWED");
		borrowEntry.get().setAction("RETURNED");
		Optional<Book> borrowedBook = bookRepository.findById(borrowEntry.get().getBookId());
		borrowedBook.get().setIsAvailable("YES");

		bookRepository.save(borrowedBook.get());
		registerRepository.save(borrowEntry.get());

		List<Book> books = bookRepository.findAll();

		assertTrue(books.get(0).getId().equals(book.getId()));
	}

	@Test
	@DisplayName("User's borrowed list is empty when all books are returned")
	void bookReturn_emptyBorrowedList_Test() throws Exception {

		Book book = bookRepository.save(new Book(2,"Dummy","B001","NO"));
		Users user= usersRepository.save(new Users(2,"Jake"));
		registerRepository.save(new Register(2, book.getId(), "BORROWED", user.getId()));

		Optional<Register> borrowEntry = registerRepository.findByBorrowedUserAndBookIdAndAction(user.getId(), book.getId(), "BORROWED");
		borrowEntry.get().setAction("RETURNED");
		Optional<Book> borrowedBook = bookRepository.findById(borrowEntry.get().getBookId());
		borrowedBook.get().setIsAvailable("YES");

		bookRepository.save(borrowedBook.get());
		registerRepository.save(borrowEntry.get());

		Optional<Register> borrowEntryLatest = registerRepository.findByBorrowedUserAndBookIdAndAction(user.getId(), book.getId(), "BORROWED");

		assertTrue(borrowEntryLatest.isEmpty());
	}

}
