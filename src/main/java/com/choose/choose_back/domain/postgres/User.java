package com.choose.choose_back.domain.postgres;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Set;
import javax.persistence.*;

import com.choose.choose_back.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String email;

    @Column
    @JsonIgnore
    private String password;

    @ManyToMany(mappedBy = "users", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnoreProperties(value = {"users", "owner"})
    private Set<Community> communities;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @Column(nullable = false)
    @JsonIgnore
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

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
