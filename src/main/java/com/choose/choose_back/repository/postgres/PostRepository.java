package com.choose.choose_back.repository.postgres;

import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.dto.PostEntityWithCommunityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query(value = "select p.* " +
            "from community_user " +
            "join community c on c.id = community_user.community_id " +
            "join community_post cp on c.id = cp.community_id " +
            "join post p on p.id = cp.post_id " +
            "where user_id = :userId " +
            "order by p.id desc " +
            "limit :limit " +
            "offset :offset", nativeQuery = true)
    List<PostEntity> getFeed(Long userId, Long limit, Long offset);


    @Query(value = "select * from post " +
            "where author_id = :userId " +
            "order by id desc " +
            "limit :limit " +
            "offset :offset",
            nativeQuery = true)
    List<PostEntity> getUserFeed(Long userId, Long limit, Long offset);


    @Query(value = "select p.* " +
            "from community_post " +
            "join post p on p.id = community_post.post_id " +
            "where community_id = :communityId " +
            "order by p.id " +
            "limit :limit " +
            "offset :offset", nativeQuery = true)
    List<PostEntity> getCommunityFeed(Long communityId, Long limit, Long offset);
}
