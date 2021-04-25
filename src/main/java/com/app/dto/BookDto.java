package com.app.dto;

public class BookDto {

	private int id;
	
	private String name;
	
	private String bookCode;
	
	private String isAvailable;
	public BookDto(){

	}

	public BookDto(int id, String name, String bookCode, String isAvailable) {
		super();
		this.id = id;
		this.name = name;
		this.bookCode = bookCode;
		this.isAvailable = isAvailable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBookCode() {
		return bookCode;
	}

	public void setBookCode(String bookCode) {
		this.bookCode = bookCode;
	}

	public String getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(String isAvailable) {
		this.isAvailable = isAvailable;
	}
}

