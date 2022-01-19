package com.choose.choose_back.domain.mongo;

import com.choose.choose_back.dto.post.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetitionPost extends BasePost {
    private String description;
    private String mediaUrl;
    private Long goal;
    private Set<Long> votedUsers = new HashSet<>();
}
