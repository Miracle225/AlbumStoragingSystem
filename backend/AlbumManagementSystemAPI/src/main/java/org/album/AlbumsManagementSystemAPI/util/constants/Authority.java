package org.album.AlbumsManagementSystemAPI.util.constants;

import java.util.List;

public enum Authority {
    ROLE_READ,
    ROLE_WRITE,
    ROLE_UPDATE,
    ROLE_USER,   // Can update/delete self object, read anything
    ROLE_ADMIN;  // Can update/delete/read anything

    public static List<String> defaultUserAuthorities() {
        return List.of(ROLE_USER.name());
    }

    public static List<String> defaultAdminAuthorities() {
        return List.of(ROLE_ADMIN.name());
    }
}
