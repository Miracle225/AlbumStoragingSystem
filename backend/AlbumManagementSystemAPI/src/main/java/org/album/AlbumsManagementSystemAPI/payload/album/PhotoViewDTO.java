package org.album.AlbumsManagementSystemAPI.payload.album;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhotoViewDTO {

    private Long id;
    private String name;
    private String description;
}
