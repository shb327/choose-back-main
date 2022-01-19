package com.choose.choose_back.dto;

import lombok.Data;

@Data
public class GetCommunityFeedRequest {
    private Long size;
    private Long page;
    private Long communityId;
}
