package org.album.AlbumsManagementSystemAPI.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.album.AlbumsManagementSystemAPI.Models.Account;
import org.album.AlbumsManagementSystemAPI.Services.AccountService;
import org.album.AlbumsManagementSystemAPI.Services.TokenService;
import org.album.AlbumsManagementSystemAPI.payload.auth.*;
import org.album.AlbumsManagementSystemAPI.util.constants.AccountAccess;
import org.album.AlbumsManagementSystemAPI.util.constants.AccountError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000",maxAge = 3600,allowedHeaders = "*")
@Tag(name = "Auth controller", description = "Controller for Account management")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AccountService accountService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.accountService = accountService;
    }


    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLoginDTO) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
        } catch (Exception e) {
            log.debug("{}: {}", AccountError.TOKEN_GENERATION_ERROR, e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/users/add", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new user")
    @ApiResponse(responseCode = "400", description = "Please enter a valid email and password length between 6 to 20 characters")
    @ApiResponse(responseCode = "200", description = "Account added")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) throws Exception {
        try {
            Account account = new Account();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());
            accountService.save(account);
            return ResponseEntity.ok(AccountAccess.ACCOUNT_ADDED + " with id: " + account.getId());
        } catch (Exception e) {
            log.debug("{}: {}", AccountError.ADD_ACCOUNT_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/users", produces = "application/json")
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "album-demo-api")
    public List<AccountViewDTO> getAllUsers() {
        List<AccountViewDTO> accounts = new ArrayList<>();
        for (Account account : accountService.findAll()) {
            accounts.add(new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities()));
        }
        return accounts;
    }

    @GetMapping(value = "/profile", produces = "application/json")
    @Operation(summary = "View profile")
    @ApiResponse(responseCode = "200", description = "User profile")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "album-demo-api")
    public ProfileDTO profile(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> opAccount = accountService.findByEmail(email);
        Account account = opAccount.get();
        return new ProfileDTO(account.getId(), account.getEmail(), account.getAuthorities());
    }


    @PutMapping(value = "/profile/update-password", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update password")
    @ApiResponse(responseCode = "200", description = "Password updated")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "album-demo-api")
    public AccountViewDTO updatePassword(@Valid @RequestBody PasswordDTO passwordDTO,
                                         Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> opAccount = accountService.findByEmail(email);
        Account account = opAccount.get();
        account.setPassword(passwordDTO.getPassword());
        accountService.save(account);
        return new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities());
    }

    @PutMapping(value = "/users/{userId}/update-authorities",
            produces = "application/json",
            consumes = "application/json")
    @Operation(summary = "Update authorities")
    @ApiResponse(responseCode = "200", description = "Authorities updated")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<AccountViewDTO> updateAuth(
            @Valid @RequestBody AuthoritiesDTO authoritiesDTO,
            @PathVariable Long userId) {

        Optional<Account> opAccount = accountService.findById(userId);
        if (opAccount.isPresent()) {
            Account account = opAccount.get();
            account.setAuthorities(authoritiesDTO.getAuthorities());
            accountService.save(account);
            return ResponseEntity.ok(new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities()));
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/profile/delete")
    @Operation(summary = "Delete profile")
    @ApiResponse(responseCode = "200", description = "Delete profile")
    @ApiResponse(responseCode = "400", description = "Invalid user")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "album-demo-api")
    public ResponseEntity<String> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> opAccount = accountService.findByEmail(email);
        if (opAccount.isPresent()) {
            accountService.deleteById(opAccount.get().getId());
            return ResponseEntity.ok("User deleted with id: " + opAccount.get().getId());
        }
        return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);

    }
}
