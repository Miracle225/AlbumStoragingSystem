package org.album.AlbumsManagementSystemAPI.payload.album;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlbumViewDTO {

    private Long id;
    @NotBlank
    @Schema(description = "Album name", example = "Travel", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @NotBlank
    @Schema(description = "Album description", example = "Description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    private List<PhotoDTO> photos;
}
