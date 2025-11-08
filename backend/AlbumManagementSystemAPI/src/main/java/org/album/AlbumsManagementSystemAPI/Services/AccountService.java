package org.album.AlbumsManagementSystemAPI.Services;

import org.album.AlbumsManagementSystemAPI.Models.Account;
import org.album.AlbumsManagementSystemAPI.Repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public Account save(Account account) {
        if (account.getPassword() != null && !account.getPassword().startsWith("$2a$")) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        if (account.getAuthorities() == null || account.getAuthorities().isEmpty()) {
            account.setAuthorities(List.of("ROLE_USER"));
        }

        return accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + email));
        List<SimpleGrantedAuthority> grantedAuthorities = account.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new User(
                account.getEmail(),
                account.getPassword(),
                grantedAuthorities
        );
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }
}
