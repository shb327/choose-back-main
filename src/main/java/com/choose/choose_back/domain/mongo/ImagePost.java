package com.choose.choose_back.domain.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class ImagePost extends BasePost {
    private String description;
    private String url;
}
