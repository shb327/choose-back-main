package com.choose.choose_back.domain.mongo;

import com.choose.choose_back.dto.VotingOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayoffPost extends BasePost {
    private String description;
    private List<VotingOption> options;
}
