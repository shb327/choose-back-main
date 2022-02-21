package com.choose.choose_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlayoffPostRequest {
    private String title;
    private List<PlayoffOption> options;

    @Data
    public static class PlayoffOption {
        private String title;
        private MultipartFile file;
    }
}
