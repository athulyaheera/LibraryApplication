package main.java.com.app;

import com.app.exception.BorrowLimitException;
import com.app.exception.CopyLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class LibraryExceptionHandler {

	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> exception(Exception exception) {
		
		return new ResponseEntity<>("Exception occured :"+exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = BorrowLimitException.class)
	public ResponseEntity<Object> borrowLimitException(BorrowLimitException exception) {
		
		return new ResponseEntity<>("User Cannot borrow more than 2 books", HttpStatus.NOT_MODIFIED);
	}	
	
	@ExceptionHandler(value = CopyLimitException.class)
	public ResponseEntity<Object> copyLimitException(CopyLimitException exception) {
		
		return new ResponseEntity<>("User Cannot borrow more than 2 books", HttpStatus.NOT_MODIFIED);
	}	

}
