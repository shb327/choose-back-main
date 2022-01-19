package com.choose.choose_back.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagePostDTO extends PostDTO {
    private String description;
    private String url;
}
