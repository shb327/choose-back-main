package com.choose.choose_back.dto;

import com.choose.choose_back.domain.postgres.Community;
import com.choose.choose_back.domain.postgres.PostEntity;
import lombok.Data;

@Data
public class PostEntityWithCommunityId extends PostEntity {
    private Long communityId;
}
