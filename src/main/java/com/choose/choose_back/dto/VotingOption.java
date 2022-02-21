package com.choose.choose_back.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class VotingOption {
    private Long id;
    private String title;
    private String media;
    private Set<Long> votedUsers = new HashSet<>();
}
