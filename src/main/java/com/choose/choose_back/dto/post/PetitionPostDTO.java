package com.choose.choose_back.dto.post;

import com.choose.choose_back.domain.mongo.BasePost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetitionPostDTO extends PostDTO {
    private String description;
    private String mediaUrl;
    private Long votedCount;
    private Long goal;
    private boolean voted;
}
