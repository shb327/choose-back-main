package com.choose.choose_back.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String username;

    @NotNull
    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 255)
    private String password;

    @NotNull
    private Long userPost;

    private List<Long> userCommunitys;

}
