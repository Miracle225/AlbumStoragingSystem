package org.album.AlbumsManagementSystemAPI.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true)
    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "account_authorities",
            joinColumns = @JoinColumn(name = "account_id")
    )
    @Column(name = "authority")
    private List<String> authorities = new ArrayList<>();

    public Account(String email,
                   String password,
                   List<String> authorities) {
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

}
