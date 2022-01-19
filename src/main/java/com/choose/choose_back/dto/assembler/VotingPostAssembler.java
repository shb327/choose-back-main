package com.choose.choose_back.dto.assembler;

import com.choose.choose_back.SecurityUtils;
import com.choose.choose_back.domain.mongo.TextPost;
import com.choose.choose_back.domain.mongo.VotingPost;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.dto.VotingOptionDTO;
import com.choose.choose_back.dto.post.TextPostDTO;
import com.choose.choose_back.dto.post.VotingPostDTO;

import java.util.stream.Collectors;

public class VotingPostAssembler implements Assembler<VotingPost, VotingPostDTO> {
    @Override
    public VotingPostDTO assemble(PostEntity entity, VotingPost textPost) {
        VotingPostDTO dto = new VotingPostDTO();
        assembleBase(entity, textPost,  dto);
        dto.setOptions(textPost.getOptions().stream()
                .map(option -> {
                    VotingOptionDTO votingOption = new VotingOptionDTO();
                    votingOption.setId(option.getId());
                    votingOption.setTitle(option.getTitle());
                    votingOption.setVotedUsers((long) option.getVotedUsers().size());
                    votingOption.setVoted(option.getVotedUsers().contains(SecurityUtils.getCurrentUser().getId()));
                    return votingOption;
                })
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public boolean isApplicable(Object object) {
        return object instanceof VotingPost;
    }
}
