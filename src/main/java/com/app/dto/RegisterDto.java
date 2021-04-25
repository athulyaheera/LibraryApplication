package com.app.dto;

public class RegisterDto {
	
	public RegisterDto() {
	}

	public RegisterDto(int id, int bookId, String action, int borrowedUser) {
		super();
		this.id = id;
		this.bookId = bookId;
		this.action = action;
		this.borrowedUser = borrowedUser;
	}
	
	private int id;
	
	private int bookId;
	
	private String action;
	
	private int borrowedUser;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getBorrowedUser() {
		return borrowedUser;
	}

	public void setBorrowedUser(int borrowedUser) {
		this.borrowedUser = borrowedUser;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	

}
