package com.community.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.community.model.AuthorityName;
import com.community.model.domain.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long>{
	Optional<Authority> findByName(AuthorityName name);
}
