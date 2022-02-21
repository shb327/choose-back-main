package com.choose.choose_back.dto.assembler;

import com.choose.choose_back.SecurityUtils;
import com.choose.choose_back.domain.mongo.PlayoffPost;
import com.choose.choose_back.domain.mongo.VotingPost;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.dto.VotingOptionDTO;
import com.choose.choose_back.dto.post.PlayoffPostDTO;
import com.choose.choose_back.dto.post.VotingPostDTO;

import java.util.stream.Collectors;

public class PlayoffPostAssembler implements Assembler<PlayoffPost, PlayoffPostDTO> {
    @Override
    public PlayoffPostDTO assemble(PostEntity entity, PlayoffPost textPost) {
        PlayoffPostDTO dto = new PlayoffPostDTO();
        assembleBase(entity, textPost,  dto);
        dto.setOptions(textPost.getOptions().stream()
                .map(option -> {
                    VotingOptionDTO votingOption = new VotingOptionDTO();
                    votingOption.setId(option.getId());
                    votingOption.setTitle(option.getTitle());
                    votingOption.setVotedUsers((long) option.getVotedUsers().size());
                    votingOption.setMedia(option.getMedia());
                    votingOption.setVoted(option.getVotedUsers().contains(SecurityUtils.getCurrentUser().getId()));
                    return votingOption;
                })
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public boolean isApplicable(Object object) {
        return object instanceof PlayoffPost;
    }
}
