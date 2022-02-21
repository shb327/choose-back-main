package com.choose.choose_back.service;

import com.choose.choose_back.SecurityUtils;
import com.choose.choose_back.domain.mongo.*;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.domain.postgres.PostType;
import com.choose.choose_back.dto.*;
import com.choose.choose_back.dto.assembler.AssemblerFacade;
import com.choose.choose_back.dto.post.*;
import com.choose.choose_back.repository.mongo.PostMongoRepository;
import com.choose.choose_back.repository.postgres.PostRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMongoRepository postMongoRepository;
    private final AssemblerFacade assemblerFacade;
    private final S3Client s3Client;


    public GetUserFeedResponse getFeed(GetFeedRequest request) {
        List<PostEntity> entities = postRepository.getFeed(SecurityUtils.getCurrentUser().getId(),
                request.getSize(),
                request.getSize() * request.getPage());

        return getGetUserFeedResponse(entities);
    }

    public GetUserFeedResponse getSelfFeed(GetFeedRequest request) {
        return getUserFeed(new GetUserFeedRequest(
                SecurityUtils.getCurrentUser().getId(), request.getSize(), request.getPage()
        ));
    }

    public PostDTO get(final Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    BasePost basePost = postMongoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                    return assemblerFacade.assemble(post, basePost, PostDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public TextPostDTO createTextPost(CreateTextPostRequest request) {
        PostEntity postEntity = createPostEntity(request.getTitle(), PostType.TEXT);
        TextPost textPost = new TextPost();
        textPost.setId(postEntity.getId());
        textPost.setContent(request.getContent());
        textPost.setTitle(request.getTitle());
        postMongoRepository.save(textPost);

        return assemblerFacade.assemble(postEntity, textPost, TextPostDTO.class);
    }

    public PostEntity createPostEntity(String title, PostType postType) {
        PostEntity post = new PostEntity();
        post.setTitle(title);
        post.setAuthor(SecurityUtils.getCurrentUser());
        post.setType(postType);

        return postRepository.save(post);
    }


    public void delete(final Long id) {
        postRepository.deleteById(id);
    }

    private PostDTO mapToDTO(final PostEntity post, final PostDTO postDTO) {
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        return postDTO;
    }

    private <T, E> Stream<Pair<T, E>> zip(List<T> first, List<E> second) {
        return IntStream.range(0, first.size()).mapToObj(i -> Pair.of(first.get(i), second.get(i)));
    }

    private <T> List<T> convertToList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    private <T, K> Map<K, T> convertToMap(Iterable<T> iterable, Function<T, K> keySelector) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toMap(keySelector, value -> value));
    }

    public GetUserFeedResponse getCommunityFeed(GetCommunityFeedRequest request) {
        List<PostEntity> entities = postRepository.getCommunityFeed(request.getCommunityId(),
                request.getSize(),
                request.getSize() * request.getPage());

        return getGetUserFeedResponse(entities);
    }


    private <T extends PostEntity> GetUserFeedResponse getGetUserFeedResponse(List<T> entities) {
        Map<Long, BasePost> posts = convertToMap(postMongoRepository.findAllById(entities.stream()
                        .map(PostEntity::getId)
                        .collect(Collectors.toList())),
                BasePost::getId);

        return new GetUserFeedResponse(entities.stream()
                .map(entity -> {
                    BasePost post = posts.get(entity.getId());
                    if (post != null) {
                        return Pair.of(entity, post);
                    } else {
                        log.warn("Post with id {} is not found in mongo", entity.getId());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(pair -> assemblerFacade.assemble(pair.getFirst(), pair.getSecond(), PostDTO.class))
                .collect(Collectors.toList()));
    }

    @SneakyThrows
    public ImagePostDTO createMediaPost(MultipartFile file, String title, String description) {
        String url = uploadImage(file);
        PostEntity postEntity = createPostEntity(title, PostType.IMAGE);
        ImagePost imagePost = new ImagePost();
        imagePost.setTitle(title);
        imagePost.setId(postEntity.getId());
        imagePost.setUrl(url);
        imagePost.setDescription(description);
        postMongoRepository.save(imagePost);

        return assemblerFacade.assemble(postEntity, imagePost, ImagePostDTO.class);

    }

    private String uploadImage(MultipartFile file) throws IOException {
        String[] split = file.getOriginalFilename().split("\\.");
        String ext = split[split.length - 1];
        String bucket = "choose-image";
        String key = UUID.randomUUID() + "." + ext;
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(ext)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        return "https://choose-image.s3.us-east-2.amazonaws.com/" + key;
    }

    private String uploadImageBase64(String base64, String ext) {
        String bucket = "choose-image";
        String key = UUID.randomUUID() + "." + ext;
        byte[] bytes = Base64.getDecoder().decode(base64);
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(ext)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build(),
                RequestBody.fromBytes(bytes));

        return "https://choose-image.s3.us-east-2.amazonaws.com/" + key;
    }

    public GetUserFeedResponse getUserFeed(GetUserFeedRequest request) {
        List<PostEntity> entities = postRepository.getUserFeed(request.getUserId(),
                request.getSize(),
                request.getSize() * request.getPage());

        return getGetUserFeedResponse(entities);
    }

    @SneakyThrows
    public PetitionPostDTO createPetitionPost(MultipartFile media, String title, String description, Long goal) {
        PostEntity postEntity = createPostEntity(title, PostType.PETITION);
        PetitionPost petitionPost = new PetitionPost();
        petitionPost.setId(postEntity.getId());
        petitionPost.setDescription(description);
        if (media != null) {
            petitionPost.setMediaUrl(uploadImage(media));
        }
        if (goal != null) {
            petitionPost.setGoal(goal);
        }
        postMongoRepository.save(petitionPost);

        return assemblerFacade.assemble(postEntity, petitionPost, PetitionPostDTO.class);
    }

    public PostDTO vote(Long id, Long optionId) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found"));
        switch (post.getType()) {
            case PETITION:
                return postMongoRepository.findById(post.getId())
                        .map(PetitionPost.class::cast)
                        .map(petitionPost -> {
                            petitionPost.getVotedUsers().add(SecurityUtils.getCurrentUser().getId());
                            return petitionPost;
                        })
                        .map(postMongoRepository::save)
                        .map(petitionPost -> assemblerFacade.assemble(post, petitionPost, PostDTO.class))
                        .orElseThrow(() -> new IllegalArgumentException("not found"));
            case VOTE:
                return postMongoRepository.findById(post.getId())
                        .map(VotingPost.class::cast)
                        .map(votingPost -> {
                            VotingOption option = votingPost.getOptions().stream()
                                    .filter(voting -> voting.getId().equals(optionId))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("no option found"));
                            option.getVotedUsers().add(SecurityUtils.getCurrentUser().getId());
                            return votingPost;
                        })
                        .map(postMongoRepository::save)
                        .map(votingPost -> assemblerFacade.assemble(post, votingPost, PostDTO.class))
                        .orElseThrow(() -> new IllegalArgumentException("not found"));
            case PLAYOFF:
                return postMongoRepository.findById(post.getId())
                        .map(PlayoffPost.class::cast)
                        .map(playoffPost -> {
                            VotingOption option = playoffPost.getOptions().stream()
                                    .filter(voting -> voting.getId().equals(optionId))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("no option found"));
                            option.getVotedUsers().add(SecurityUtils.getCurrentUser().getId());
                            return playoffPost;
                        })
                        .map(postMongoRepository::save)
                        .map(playoffPost -> assemblerFacade.assemble(post, playoffPost, PostDTO.class))
                        .orElseThrow(() -> new IllegalArgumentException("not found"));
            default:
                throw new IllegalArgumentException("not voting post");
        }
    }

    public VotingPostDTO createVotingPost(CreateVotingPostRequest request) {
        PostEntity postEntity = createPostEntity(request.getTitle(), PostType.VOTE);
        AtomicLong counter = new AtomicLong();
        VotingPost votingPost = new VotingPost();
        votingPost.setId(postEntity.getId());
        votingPost.setOptions(request.getOptions().stream()
                .map(option -> {
                    VotingOption votingOption = new VotingOption();
                    votingOption.setId(counter.getAndIncrement());
                    votingOption.setTitle(option);
                    return votingOption;
                })
                .collect(Collectors.toList()));
        postMongoRepository.save(votingPost);


        return assemblerFacade.assemble(postEntity, votingPost, VotingPostDTO.class);
    }

    public PostDTO like(Long postId, LikeStatus status) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("not found"));

        return postMongoRepository.findById(postId)
                .map(post -> {
                    switch (status) {
                        case LIKE:
                            post.getLikedUsers().add(userId);
                            post.getDislikedUsers().removeIf(user -> user.equals(userId));
                            break;
                        case DISLIKE:
                            post.getDislikedUsers().add(userId);
                            post.getLikedUsers().removeIf(user -> user.equals(userId));
                            break;
                        case UNSET:
                            post.getLikedUsers().removeIf(user -> user.equals(userId));
                            post.getDislikedUsers().removeIf(user -> user.equals(userId));
                    }
                    return post;
                })
                .map(postMongoRepository::save)
                .map(post -> assemblerFacade.assemble(postEntity, post, PostDTO.class))
                .orElseThrow(() -> new IllegalArgumentException("not found"));
    }

    public PlayoffPostDTO createPlayoffPost(CreatePlayoffPostRequest request) {
        PostEntity postEntity = createPostEntity(request.getTitle(), PostType.PLAYOFF);
        AtomicLong counter = new AtomicLong();
        PlayoffPost playoffPost = new PlayoffPost();
        playoffPost.setId(postEntity.getId());
        playoffPost.setTitle(request.getTitle());
        playoffPost.setOptions(request.getOptions().stream()
                .map(option -> {
                    VotingOption votingOption = new VotingOption();
                    votingOption.setId(counter.getAndIncrement());
                    votingOption.setTitle(option.getTitle());
                    if (option.getFile() != null) {
                        try {
                            String url = uploadImage(option.getFile());
                            votingOption.setMedia(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return votingOption;
                })
                .collect(Collectors.toList()));
        postMongoRepository.save(playoffPost);


        return assemblerFacade.assemble(postEntity, playoffPost, PlayoffPostDTO.class);
    }
}
