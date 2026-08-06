package com.webshop.JWT;

import com.webshop.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    private final long id;
    private final String email;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user){
        id = user.getId();
        email = user.getEmail();
        username = user.getUsername();
        password = user.getPassword();

        authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
    }
}
