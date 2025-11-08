package org.album.AlbumsManagementSystemAPI.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info=@Info(
            title = "Demo API",
            version = "Versions 1.0",
            contact = @Contact(
                    name = "Danylo Ishchenko", email = "ishchienko44@gmail.com", url="https://github.com/Miracle225"
            ),
            license = @License(
                    name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
            ),
            termsOfService = "https://github.com/Miracle225",
            description = "Spring boot Restful API Demo by Danylo"
    )
)
public class SwaggerConfig {
}
