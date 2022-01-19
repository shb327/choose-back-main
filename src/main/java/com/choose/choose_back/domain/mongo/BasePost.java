package com.choose.choose_back.domain.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document("post")
public class BasePost {
    @Id
    private Long id;
    private String title;
    private Set<Long> likedUsers = new HashSet<>();
    private Set<Long> dislikedUsers = new HashSet<>();
}
