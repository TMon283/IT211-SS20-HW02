package org.example.bai2.security;

import lombok.RequiredArgsConstructor;
import org.example.bai2.entity.Account;
import org.example.bai2.repository.AccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + username));

        // Convert roles to GrantedAuthority using Stream API
        var authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());

        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .authorities(authorities)
                .disabled(!account.isActive())
                .build();
    }
}
