package com.choose.choose_back.dto.assembler;

import com.choose.choose_back.SecurityUtils;
import com.choose.choose_back.domain.mongo.BasePost;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.dto.post.LikeStatus;
import com.choose.choose_back.dto.post.PostDTO;

public interface Assembler<DOCUMENT extends BasePost, DTO extends PostDTO> {
    DTO assemble(PostEntity entity, DOCUMENT document);

    boolean isApplicable(Object object);

    default void assembleBase(PostEntity entity, DOCUMENT post, DTO dto) {
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setType(entity.getType());
        dto.setAuthorUsername(entity.getAuthor().getUsername());
        dto.setAuthorId(entity.getAuthor().getId());

        if (post.getLikedUsers().contains(SecurityUtils.getCurrentUser().getId())){
            dto.setLikeStatus(LikeStatus.LIKE);
        } else if (post.getDislikedUsers().contains(SecurityUtils.getCurrentUser().getId())){
            dto.setLikeStatus(LikeStatus.DISLIKE);
        }
        dto.setLikesCount((long) post.getLikedUsers().size() - post.getDislikedUsers().size());
    }
}
