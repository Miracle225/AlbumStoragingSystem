package org.album.AlbumsManagementSystemAPI.payload.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDTO {

    private Long id;
    private String email;
    private List<String> authorities;
}
