package com.choose.choose_back.controller;

import com.choose.choose_back.dto.*;
import com.choose.choose_back.model.CommunityDTO;
import com.choose.choose_back.service.CommunityService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/communities", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(final CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public ResponseEntity<List<CommunityDTO>> getAllCommunitys(GetAllCommunitiesRequestDTO request) {
        return ResponseEntity.ok(communityService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunity(@PathVariable final Long id) {
        return ResponseEntity.ok(communityService.get(id));
    }

    @GetMapping("/discover")
    public ResponseEntity<List<CommunityDTO>> getDiscoverCommunity(GetAllCommunitiesRequestDTO request) {
        return ResponseEntity.ok(communityService.getDiscover(request));
    }

    @GetMapping("/user/recent")
    public ResponseEntity<List<CommunityDTO>> getCommunity() {
        return ResponseEntity.ok(communityService.getUserCommunities());
    }

    @PostMapping
    public ResponseEntity<Long> createCommunity(
            @RequestBody @Valid final CreateCommunityDTO communityDTO) {
        return new ResponseEntity<>(communityService.create(communityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCommunity(@PathVariable final Long id,
            @RequestBody @Valid final CommunityDTO communityDTO) {
        communityService.update(id, communityDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable final Long id) {
        communityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/all")
    public List<CommunityDTO> getUserCommunities() {
        return communityService.getUserCommunities();
    }

    @PostMapping("/join")
    public void joinCommunity(JoinCommunityRequest request) {
        communityService.joinCommunity(request);
    }

    @PostMapping("/leave")
    public void leaveCommunity(LeaveCommunityRequest request) {
        communityService.leaveCommunity(request);
    }

    @PostMapping("/post/add")
    public void addPost(AddPostRequest request) {
        communityService.addPostToCommunity(request);
    }

}
