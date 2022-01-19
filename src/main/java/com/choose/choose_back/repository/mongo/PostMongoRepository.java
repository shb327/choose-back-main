package com.choose.choose_back.repository.mongo;

import com.choose.choose_back.domain.mongo.BasePost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostMongoRepository extends MongoRepository<BasePost, Long> {
}
