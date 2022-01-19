package com.choose.choose_back.domain.postgres;

import java.time.OffsetDateTime;
import java.util.Set;
import javax.persistence.*;

import com.choose.choose_back.domain.postgres.Community;
import com.choose.choose_back.domain.postgres.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PostType type;

    @JoinTable(name = "community_post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id"))
    @ManyToMany
    private Set<Community> communities;

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
