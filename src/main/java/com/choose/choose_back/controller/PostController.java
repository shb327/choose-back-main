package com.choose.choose_back.controller;

import com.choose.choose_back.dto.*;
import com.choose.choose_back.dto.post.*;
import com.choose.choose_back.service.PostService;
import lombok.SneakyThrows;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

    private final PostService postService;

    public PostController(final PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/feed")
    public GetUserFeedResponse getFeed(@RequestBody GetFeedRequest request) {
        return postService.getFeed(request);
    }

    @PostMapping("/feed/user")
    public GetUserFeedResponse getUserFeed(@RequestBody GetUserFeedRequest request) {
        return postService.getUserFeed(request);
    }

    @PostMapping("/feed/self")
    public GetUserFeedResponse getSelfFeed(@RequestBody GetFeedRequest request) {
        return postService.getSelfFeed(request);
    }

    @PostMapping("/feed/community")
    public GetUserFeedResponse getCommunityFeed(@RequestBody GetCommunityFeedRequest request) {
        return postService.getCommunityFeed(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable final Long id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @PostMapping("/create/text")
    public TextPostDTO createTextPost(@RequestBody CreateTextPostRequest request) {
        return postService.createTextPost(request);
    }

    @PostMapping("/create/image")
    public ImagePostDTO createImagePost(@RequestPart MultipartFile file,
                                        @RequestParam String title,
                                        @RequestParam String description) {
        return postService.createMediaPost(file, title, description);
    }

    @PostMapping("/create/petition")
    public PetitionPostDTO createPetitionPost(@RequestPart(required = false) MultipartFile media,
                                              @RequestParam String title,
                                              @RequestParam String description,
                                              @RequestParam(required = false) Long goal) {
        return postService.createPetitionPost(media, title, description, goal);
    }

    @PostMapping("/create/voting")
    public VotingPostDTO createVotingPost(@RequestBody CreateVotingPostRequest request) {
        return postService.createVotingPost(request);
    }

    @SneakyThrows
    @PostMapping(value = "/create/playoff", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PlayoffPostDTO createPlayoffPost(@RequestPart("title") String title,
                                            @RequestPart("options") List<CreatePlayoffPostRequest.PlayoffOption> options,
                                            @RequestPart("files") MultipartFile[] files) {
        CreatePlayoffPostRequest requestBody = new CreatePlayoffPostRequest();
        if (options.size() != files.length) {
            throw new IllegalArgumentException("options size should equals files size");
        }
        requestBody.setOptions(StreamUtils.zip(options.stream(), Stream.of(files), (left, right) -> {
                    left.setFile(right);
                    return left;
                })
                .collect(Collectors.toList()));
        requestBody.setTitle(title);

        return postService.createPlayoffPost(requestBody);
    }


    @PostMapping("/like/{id}")
    public PostDTO like(@PathVariable("id") Long postId, @RequestParam LikeStatus status) {
        return postService.like(postId, status);
    }

    @PutMapping("/vote/{id}")
    public PostDTO voteForPost(@PathVariable("id") Long id,
                               @RequestParam(required = false) Long optionId) {
        return postService.vote(id, optionId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable final Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
