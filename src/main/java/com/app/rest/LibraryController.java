package com.app.rest;

import com.app.dto.BookDto;
import com.app.dto.RegisterDto;
import com.app.dto.UsersDto;
import com.app.entity.Book;
import com.app.entity.Register;
import com.app.entity.Users;
import com.app.service.LibraryService;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/library")
@RestController
public class LibraryController {
	
	@Autowired
	LibraryService libraryService;
	
	@Autowired
    private ModelMapper modelMapper;

	@CrossOrigin
	@GetMapping(path="/viewBooks", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="View Books Available in Library",
	response = BookDto.class)
	public List<BookDto> viewBooks (){
		return libraryService.viewBooks().stream()
				.map(this :: convertToDto)
				.collect(Collectors.toList());
		
	}
	
	@CrossOrigin
	@PostMapping(path="/addBook",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Add Book to library",
			response = Book.class)
	public Book addBooks (@RequestBody BookDto book){
		return libraryService.addBook(convertToEntity(book));
	}
	
	@CrossOrigin
	@PostMapping(path="/borrowBook",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Borrow books from library",
			response = Book.class)
	public Register borrowBook (@RequestBody RegisterDto registerDto ) throws Exception{
		return libraryService.borrowBook(convertToEntity(registerDto));
	}
	
	@CrossOrigin
	@PostMapping(path="/addUser",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Add users to library",
			response = Book.class)
	public Users addUsers (@RequestBody UsersDto userDto){

		return libraryService.addUsers(convertToEntity(userDto));

	}
	
	@CrossOrigin
	@PostMapping(path="/returnBook",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="Return books to library",
			response = Book.class)
	public ResponseEntity<RegisterDto> returnBook (@RequestBody RegisterDto registerDto ) throws Exception{
		
		return new ResponseEntity<>(convertToDto(libraryService.returnBook(convertToEntity(registerDto))),HttpStatus.CREATED);
	}
	
	@CrossOrigin
	@GetMapping(path="/viewBorrowedBooks", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value="View borrowed books of the user",
			notes = "Give the user name",
			response = Book.class)
	public ResponseEntity<List<BookDto>> viewBorrowedBooks (@RequestParam("userId") Integer userId){

		return new ResponseEntity<>(libraryService.viewBorrowedBooks(userId).stream()
				.map(book -> convertToDto(book.get())).collect(Collectors.toList()),HttpStatus.OK);

	}
	
	private BookDto convertToDto(Book book) {
		return modelMapper.map(book, BookDto.class);
	}
	
	private Book convertToEntity(BookDto bookDto){
		return modelMapper.map(bookDto, Book.class);
	}
	private RegisterDto convertToDto(Register entity) {
		return modelMapper.map(entity, RegisterDto.class);
	}
	private Register convertToEntity(RegisterDto dto) {
		return modelMapper.map(dto, Register.class);
	}
	private Users convertToEntity(UsersDto dto) {
		return modelMapper.map(dto, Users.class);
	}
	private UsersDto convertToDto(Users entity) {
		return modelMapper.map(entity, UsersDto.class);
	}
}
