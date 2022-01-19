package com.choose.choose_back.dto.post;

import com.choose.choose_back.dto.VotingOptionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VotingPostDTO extends PostDTO {
    private List<VotingOptionDTO> options;
}
