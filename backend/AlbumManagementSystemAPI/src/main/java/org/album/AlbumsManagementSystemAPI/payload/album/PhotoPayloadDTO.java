package org.album.AlbumsManagementSystemAPI.payload.album;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PhotoPayloadDTO {

    @NotBlank
    @Schema(description = "Photo name",example = "Selfie", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "Description of a photo", example = "Description", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;
}
