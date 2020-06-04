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

public class UserPrincipal implements OAuth2User,UserDetails{

	private static final long serialVersionUID = 1909732448083189765L;
	
	private String email;
    private String name;
    private boolean enabled;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;
	
	
	public UserPrincipal(String email, String name,boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.name = name;
        this.authorities = authorities;
    }
	
	public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getAuth().getRole()));

        return new UserPrincipal(
                user.getEmail(),
                user.getName(),
                user.isStatus(),
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
	
	public String getEmail() {
		return email;
	}

	// user's id
	@Override
	public String getName() {
		return email;
	}
	
	@Override
	public String getUsername() {
		return name;
	}

	@Override
	public String getPassword() {
		return "none";
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return enabled;
	}

	@Override
	public boolean isAccountNonLocked() {
		return enabled;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
