package com.choose.choose_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVotingPostRequest {
    private String title;
    private List<String> options;
}
