package org.album.AlbumsManagementSystemAPI.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthoritiesDTO {
    @NotEmpty(message = "Authorities list cannot be empty")
    @Schema(description = "Authorities",example = "ROLE_USER",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> authorities;
}
