package org.album.AlbumsManagementSystemAPI.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "album")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;
    @ManyToOne
    @JoinColumn(name = "account_id",referencedColumnName = "id",nullable = false)
    private Account account;

    public Album(String name,
                 String description,
                 Account account) {
        this.name = name;
        this.description = description;
        this.account = account;
    }
}
