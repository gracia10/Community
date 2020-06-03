package com.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.community.model.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
}
