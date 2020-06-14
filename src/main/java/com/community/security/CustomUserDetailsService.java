package com.community.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.community.model.domain.User;
import com.community.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
    UserRepository userRepository;	
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		
		User user = userRepository.findById(id)
                	.orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + id)
        );

        return CustomUserDetails.create(user);
	}

}
