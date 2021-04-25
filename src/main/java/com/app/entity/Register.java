package com.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class Register {

	public Register (){}
	public Register(int id, Integer bookId, String action, int borrowedUser) {
		super();
		this.id = id;
		this.bookId = bookId;
		this.action = action;
		this.borrowedUser = borrowedUser;
	}

	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="book_id")
	private Integer bookId;
	
	@Column
	private String action;

	@Column(name="user_id")
	private int borrowedUser;

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}


	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getBorrowedUser() {
		return borrowedUser;
	}

	public void setBorrowedUser(int borrowedUser) {
		this.borrowedUser = borrowedUser;
	}
	
	



}
