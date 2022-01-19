package com.choose.choose_back.domain.mongo;

import com.choose.choose_back.dto.VotingOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VotingPost extends BasePost {
    private List<VotingOption> options;
}
