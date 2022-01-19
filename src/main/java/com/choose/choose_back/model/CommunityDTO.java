package com.choose.choose_back.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.choose.choose_back.domain.postgres.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommunityDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String username;

    @NotNull
    private String description;

    @NotNull
    @JsonIgnoreProperties("communities")
    private User owner;

    private List<Long> communityPosts;

}
