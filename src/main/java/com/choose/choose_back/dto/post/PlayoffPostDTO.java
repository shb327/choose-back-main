package com.choose.choose_back.dto.post;

import com.choose.choose_back.domain.mongo.BasePost;
import com.choose.choose_back.dto.VotingOption;
import com.choose.choose_back.dto.VotingOptionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayoffPostDTO extends PostDTO {
    private String description;
    private List<VotingOptionDTO> options;

}
