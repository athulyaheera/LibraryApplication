package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.app.entity.Register;

public interface RegisterRepository extends CrudRepository<Register, Integer>{

	List<Optional<Register>> findByBorrowedUserAndAction(int borrowedUser, String action);

	Optional<Register> findByBorrowedUserAndBookIdAndAction(int borrowedUser, int bookId, String string);

}
