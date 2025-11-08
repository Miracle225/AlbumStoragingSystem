package org.album.AlbumsManagementSystemAPI.payload.album;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDTO {

    private Long id;
    private String name;
    private String description;
    private String fileName;
    private String downloadLink;
}
