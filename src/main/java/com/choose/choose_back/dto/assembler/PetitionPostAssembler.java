package com.choose.choose_back.dto.assembler;

import com.choose.choose_back.SecurityUtils;
import com.choose.choose_back.domain.mongo.ImagePost;
import com.choose.choose_back.domain.mongo.PetitionPost;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.domain.postgres.User;
import com.choose.choose_back.dto.post.ImagePostDTO;
import com.choose.choose_back.dto.post.PetitionPostDTO;

public class PetitionPostAssembler implements Assembler<PetitionPost, PetitionPostDTO> {

    @Override
    public PetitionPostDTO assemble(PostEntity entity, PetitionPost petitionPost) {
        PetitionPostDTO dto = new PetitionPostDTO();
        assembleBase(entity,petitionPost, dto);
        dto.setDescription(petitionPost.getDescription());
        dto.setGoal(petitionPost.getGoal());
        dto.setMediaUrl(petitionPost.getMediaUrl());
        User user = SecurityUtils.getCurrentUser();
        dto.setVotedCount((long) petitionPost.getVotedUsers().size());
        if (user != null) {
            dto.setVoted(petitionPost.getVotedUsers().contains(user.getId()));
        }
        return dto;
    }

    @Override
    public boolean isApplicable(Object object) {
        return object instanceof PetitionPost;
    }
}
