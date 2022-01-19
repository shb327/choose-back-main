package com.choose.choose_back.dto.post;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.choose.choose_back.domain.postgres.PostType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    private PostType type;

    private Long communityId;

    private Long authorId;

    private String authorUsername;

    private Long likesCount;

    private LikeStatus likeStatus;
}
