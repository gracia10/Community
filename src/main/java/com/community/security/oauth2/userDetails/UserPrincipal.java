package com.community.security.oauth2.userDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.community.model.domain.User;

import lombok.Getter;

@Getter
public class UserPrincipal implements OAuth2User,UserDetails{

	private static final long serialVersionUID = 1909732448083189765L;
	
	private String email;
    private String name;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;
	
	public UserPrincipal(String email, String name, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.name = name;
        this.authorities = authorities;
    }
	
	public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getAuth().getRole()));

        return new UserPrincipal(
                user.getEmail(),
                user.getName(),
                authorities
        );
    }
	
	public static UserPrincipal create(User user, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = UserPrincipal.create(user);
		userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }
	
	private void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getId() {
        return email;
    }

	@Override
	public String getPassword() {
		return "none";
	}
	
	@Override
	public String getUsername() {
		return name;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
