package com.choose.choose_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserFeedRequest {
    private Long userId;
    private Long size;
    private Long page;
}
