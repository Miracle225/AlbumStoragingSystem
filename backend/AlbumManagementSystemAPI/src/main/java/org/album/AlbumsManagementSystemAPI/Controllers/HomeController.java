package org.album.AlbumsManagementSystemAPI.Controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000",maxAge = 3600)
public class HomeController {
    @GetMapping
    public String demo(){
        return "Hello world!";
    }

}
