package com.choose.choose_back.dto.assembler;

import com.choose.choose_back.domain.mongo.ImagePost;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.dto.post.ImagePostDTO;

public class ImagePostAssembler implements Assembler<ImagePost, ImagePostDTO> {
    @Override
    public ImagePostDTO assemble(PostEntity entity, ImagePost imagePost) {
        ImagePostDTO dto = new ImagePostDTO();
        assembleBase(entity, imagePost, dto);
        dto.setUrl(imagePost.getUrl());
        dto.setDescription(imagePost.getDescription());
        return dto;
    }

    @Override
    public boolean isApplicable(Object object) {
        return object instanceof ImagePost;
    }
}
