package com.choose.choose_back.dto;

import com.choose.choose_back.dto.post.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserFeedResponse {
    private List<PostDTO> posts;
}
