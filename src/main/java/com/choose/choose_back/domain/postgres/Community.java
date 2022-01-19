package com.choose.choose_back.domain.postgres;

import java.time.OffsetDateTime;
import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Community {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(
            nullable = false,
            name = "\"description\"",
            columnDefinition = "longtext"
    )
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @JoinTable(name = "COMMUNITY_USER",
            joinColumns = @JoinColumn(name = "COMMUNITY_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    @ManyToMany
    @JsonIgnoreProperties("communities")
    private Set<User> users;

    @JoinTable(name = "COMMUNITY_POST",
            joinColumns = @JoinColumn(name = "COMMUNITY_ID"),
            inverseJoinColumns = @JoinColumn(name = "POST_ID"))
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<PostEntity> posts;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    @PrePersist
    public void prePersist() {
        dateCreated = OffsetDateTime.now();
        lastUpdated = dateCreated;
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = OffsetDateTime.now();
    }

}
