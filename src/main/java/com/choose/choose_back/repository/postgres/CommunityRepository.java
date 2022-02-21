package com.choose.choose_back.repository.postgres;

import com.choose.choose_back.domain.postgres.Community;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.domain.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;


public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findByUsersIs(User users);

    @Query(value = "" +
            "select c.*, count(p.id) as count " +
            "from community c " +
            "left join community_post cp on c.id = cp.community_id " +
            "left join post p on p.id = cp.post_id " +
            "group by c.id " +
            "order by count desc " +
            "limit :limit " +
            "offset :offset",
            nativeQuery = true)
    List<Community> getCommunities(Long limit, Long offset);
    
    @Query(value = "" +
            "select community.*, count(cp.post_id) as count " +
            "from community " +
            "left join community_user cu on community.id = cu.community_id and user_id = :userId " +
            "left join community_post cp on community.id = cp.community_id " +
            "where cu.user_id is null " +
            "group by community.id " +
            "order by count desc " +
            "limit :limit " +
            "offset :offset", nativeQuery = true)
    List<Community> getDiscoverCommunities(Long userId, Long limit, Long offset);

    @Query("FROM Community WHERE owner.id = :ownerId")
    List<Community> findByOwner(Long ownerId);
}
