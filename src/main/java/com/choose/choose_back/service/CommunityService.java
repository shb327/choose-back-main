package com.choose.choose_back.service;

import com.choose.choose_back.SecurityUtils;
import com.choose.choose_back.domain.postgres.Community;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.domain.postgres.User;
import com.choose.choose_back.dto.*;
import com.choose.choose_back.model.CommunityDTO;
import com.choose.choose_back.repository.postgres.CommunityRepository;
import com.choose.choose_back.repository.postgres.PostRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Transactional
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;

    public CommunityService(final CommunityRepository communityRepository,
                            final PostRepository postRepository) {
        this.communityRepository = communityRepository;
        this.postRepository = postRepository;
    }

    public List<CommunityDTO> findAll(GetAllCommunitiesRequestDTO request) {
        return communityRepository.getCommunities(request.getSize(), request.getPage() * request.getSize())
                .stream()
                .map(community -> mapToDTO(community, new CommunityDTO()))
                .collect(Collectors.toList());
    }

    public CommunityDTO get(final Long id) {
        return communityRepository.findById(id)
                .map(community -> mapToDTO(community, new CommunityDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Long create(final CreateCommunityDTO communityDTO) {
        final Community community = new Community();
        community.setOwner(SecurityUtils.getCurrentUser());
        community.setDescription(communityDTO.getDescription());
        community.setUsername(communityDTO.getUsername());
        community.setName(communityDTO.getName());

        community.setUsers(Set.of(SecurityUtils.getCurrentUser()));
        return communityRepository.save(community).getId();
    }

    public CommunityDTO update(final Long id, final UpdateCommunityRequestDTO request) {
        return communityRepository.findById(id)
                .map(community -> {
                    if (request.getName() != null) {
                        community.setName(request.getName());
                    }
                    if (request.getDescription() != null) {
                        community.setDescription(request.getDescription());
                    }
                    return community;
                })
                .map(communityRepository::save)
                .map(community -> mapToDTO(community, new CommunityDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void delete(final Long id) {
        communityRepository.deleteById(id);
    }

    private CommunityDTO mapToDTO(final Community community, final CommunityDTO communityDTO) {
        communityDTO.setId(community.getId());
        communityDTO.setName(community.getName());
        communityDTO.setUsername(community.getUsername());
        communityDTO.setDescription(community.getDescription());
        communityDTO.setOwner(community.getOwner());
        communityDTO.setCommunityPosts(community.getPosts() == null ? null : community.getPosts().stream()
                .map(PostEntity::getId)
                .collect(Collectors.toList()));
        return communityDTO;
    }

    private Community mapToEntity(final CommunityDTO communityDTO, final Community community) {
        community.setName(communityDTO.getName());
        community.setUsername(communityDTO.getUsername());
        community.setDescription(communityDTO.getDescription());
        community.setOwner(communityDTO.getOwner());
        if (communityDTO.getCommunityPosts() != null) {
            final List<PostEntity> communityPosts = postRepository.findAllById(communityDTO.getCommunityPosts());
            if (communityPosts.size() != communityDTO.getCommunityPosts().size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of communityPosts not found");
            }
            community.setPosts(new HashSet<>(communityPosts));
        }
        return community;
    }

    public List<CommunityDTO> getUserCommunities() {
        User user = SecurityUtils.getCurrentUser();
        return communityRepository.findByUsersIs(user)
                .stream()
                .map(community -> mapToDTO(community, new CommunityDTO()))
                .collect(Collectors.toList());
    }

    public void joinCommunity(JoinCommunityRequest request) {
        User user = SecurityUtils.getCurrentUser();
        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(() -> new IllegalArgumentException("not found"));

        community.getUsers().add(user);
        communityRepository.save(community);
    }

    public void leaveCommunity(LeaveCommunityRequest request) {
        User user = SecurityUtils.getCurrentUser();
        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(() -> new IllegalArgumentException("not found"));

        community.getUsers().removeIf(communityUser -> communityUser.getId().equals(user.getId()));
        communityRepository.save(community);
    }

    public void addPostToCommunity(AddPostRequest request) {
        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow();
        PostEntity post = postRepository.findById(request.getPostId()).orElseThrow();
        community.getPosts().add(post);
        communityRepository.save(community);
    }

    public List<CommunityDTO> getDiscover(GetAllCommunitiesRequestDTO request) {
        return communityRepository.getDiscoverCommunities(
                        SecurityUtils.getCurrentUser().getId(),
                        request.getSize(),
                        request.getPage() * request.getSize())
                .stream()
                .map(community -> mapToDTO(community, new CommunityDTO()))
                .collect(Collectors.toList());
    }

    public List<CommunityDTO> getUserOwning() {
        User user = SecurityUtils.getCurrentUser();
        return communityRepository.findByOwner(user.getId())
                .stream()
                .map(community -> mapToDTO(community, new CommunityDTO()))
                .collect(Collectors.toList());
    }
}
