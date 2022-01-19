package com.choose.choose_back.dto.assembler;

import com.choose.choose_back.domain.mongo.TextPost;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.dto.post.TextPostDTO;

public class TextPostAssembler implements Assembler<TextPost, TextPostDTO> {
    @Override
    public TextPostDTO assemble(PostEntity entity, TextPost textPost) {
        TextPostDTO dto = new TextPostDTO();
        assembleBase(entity, textPost, dto);
        dto.setContent(textPost.getContent());
        return dto;
    }

    @Override
    public boolean isApplicable(Object object) {
        return object instanceof TextPost;
    }
}
