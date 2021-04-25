package com.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.app.entity.Users;

public interface UsersRepository extends CrudRepository<Users, Integer>{
	

}
