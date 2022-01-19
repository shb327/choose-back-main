package com.choose.choose_back.dto;

import lombok.Data;

@Data
public class VotingOptionDTO {
    private Long id;
    private String title;
    private Long votedUsers;
    private boolean voted;
}
