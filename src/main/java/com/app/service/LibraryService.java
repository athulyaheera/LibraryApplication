package com.app.service;


import org.springframework.beans.factory.annotation.Autowired;
import com.app.entity.Book;
import com.app.entity.Register;
import com.app.entity.Users;
import com.app.exception.BookNotFoundException;
import com.app.exception.BorrowLimitException;
import com.app.exception.CopyLimitException;
import com.app.repository.RegisterRepository;
import com.app.repository.UsersRepository;
import com.app.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryService {



    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BookRepository bookRepository;


    public List<Book> viewBooks() {
        return bookRepository.findAll();
    }


    public Book addBook(Book book) {
        return  bookRepository.save(book);
    }


    public Register borrowBook(Register register) throws BorrowLimitException ,BookNotFoundException{

        Optional<Book> book = bookRepository.findById(register.getBookId());
        if (bookRepository.findById(register.getBookId()).isEmpty())
            throw new BookNotFoundException();

        if (isValidBorrow(register.getBorrowedUser(), book.get()))
            borrowEntryInBook(book.get());
        return borrowEntryInRegister(register.getBorrowedUser(), register.getBookId());
    }


    public List<Optional<Book>> viewBorrowedBooks(int userId) {
        return registerRepository.findByBorrowedUserAndAction(userId, "BORROWED")
                .stream()
                .map(entry -> bookRepository
                        .findById(entry.get().getBookId()))
                .collect(Collectors.toList());
    }

    public Users addUsers(Users users) {
        return usersRepository.save(users);
    }

    private Book borrowEntryInBook(Book book) {

        book.setIsAvailable("NO");
        return bookRepository.save(book);
    }

    private Register borrowEntryInRegister(int userId, int bookId) {

        Register borrowEntry = new Register();

        borrowEntry.setBookId(bookId);
        borrowEntry.setBorrowedUser(userId);
        borrowEntry.setAction("BORROWED");

        return registerRepository.save(borrowEntry);
    }
    private Boolean isValidBorrow(int userId, Book book) throws BorrowLimitException{
        if(viewBorrowedBooks(userId).size() >= 2 ||
                (viewBorrowedBooks(userId).size() == 1 && checkCopyLimit(userId,book.getBookCode()))) {
            throw new BorrowLimitException();
        }
        return true;
    }

    private Boolean checkCopyLimit (int userId, String bookCode) throws CopyLimitException {
        if (bookRepository.findById(viewBorrowedBooks(userId)
                .get(0).get()
                .getId()).get()
                .getBookCode().equalsIgnoreCase(bookCode)) {
            throw new CopyLimitException();
        }
        return false;
    }

    public Register returnBook(Register register) {
        returnEntryInBook(register.getBookId());
        return returnEntryInLibrary(register.getBorrowedUser(),register.getBookId());
    }

    private Book returnEntryInBook(int bookId) {

        Optional<Book> book = bookRepository.findById(bookId);
        book.get().setIsAvailable("YES");
        return bookRepository.save(book.get());
    }

    private Register returnEntryInLibrary(int borrowedUser, int bookId) {

        Optional<Register> entry = registerRepository.findByBorrowedUserAndBookIdAndAction(borrowedUser, bookId, "BORROWED");
        entry.get().setAction("RETURNED");
        return registerRepository.save(entry.get());
    }
}
