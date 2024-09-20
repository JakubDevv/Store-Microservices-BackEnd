package com.example.identity.config;

import com.example.identity.models.User;
import com.example.identity.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUser_name(username).orElseThrow(() -> new UsernameNotFoundException(username));
        if(user.getBanned() == null){
            return new CustomUserDetails(user.getUser_name(), user.getPassword(), user.getRoles());
        }
        return null;
    }
}
