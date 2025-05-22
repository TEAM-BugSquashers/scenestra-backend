package com.bugsquashers.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String realName;
    private String email;
    private String mobile;
    private Boolean enabled;
    private Boolean isAdmin;
    private LocalDateTime regDate;
    private List<UserGenreInfo> userGenres;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGenreInfo {
        private Integer genreId;
        private String genreName;
    }
}