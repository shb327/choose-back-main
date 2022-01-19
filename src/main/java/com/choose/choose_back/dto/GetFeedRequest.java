package com.choose.choose_back.dto;

import lombok.Data;

@Data
public class GetFeedRequest {
    private Long size;
    private Long page;
}
