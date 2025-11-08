package org.album.AlbumsManagementSystemAPI.config;

import org.album.AlbumsManagementSystemAPI.Models.Account;
import org.album.AlbumsManagementSystemAPI.Models.Album;
import org.album.AlbumsManagementSystemAPI.Models.Photo;
import org.album.AlbumsManagementSystemAPI.Services.AccountService;
import org.album.AlbumsManagementSystemAPI.Services.AlbumService;
import org.album.AlbumsManagementSystemAPI.Services.PhotoService;
import org.album.AlbumsManagementSystemAPI.util.constants.Authority;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeedData implements CommandLineRunner {
    private final AccountService accountService;
    private final AlbumService albumService;
    private final PhotoService photoService;

    public SeedData(AccountService accountService,
                    AlbumService albumService,
                    PhotoService photoService) {
        this.accountService = accountService;
        this.albumService = albumService;
        this.photoService = photoService;
    }

    @Override
    public void run(String... args) throws Exception {
        Account account01 = new Account("user@user.com", "pass0101", List.of(Authority.ROLE_USER.toString()));
        Account account02 = new Account("admin@admin.com", "pass0202", List.of(Authority.ROLE_ADMIN.toString(), Authority.ROLE_USER.toString()));
        Album album01 = new Album("Travel", "Description", account01);
        Album album02 = new Album("Office", "Description", account01);
        Album album03 = new Album("Family", "Description", account01);
        Album album04 = new Album("Friends", "Description", account01);
        accountService.save(account01);
        accountService.save(account02);
        albumService.save(album01);
        albumService.save(album02);
        albumService.save(album03);
        albumService.save(album04);
    }
}
