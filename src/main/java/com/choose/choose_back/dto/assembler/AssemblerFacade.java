package com.choose.choose_back.dto.assembler;

import com.choose.choose_back.domain.mongo.BasePost;
import com.choose.choose_back.domain.postgres.PostEntity;
import com.choose.choose_back.dto.PostEntityWithCommunityId;
import com.choose.choose_back.dto.post.PostDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("ALL")
@Component
public class AssemblerFacade {
    private List<Assembler> assemblers = List.of(
            new TextPostAssembler(),
            new ImagePostAssembler(),
            new PetitionPostAssembler(),
            new VotingPostAssembler(),
            new PlayoffPostAssembler()
    );

    public <T extends PostDTO> T assemble(PostEntity entity, BasePost post, Class<T> targetClass) {
        Assembler assembler = assemblers.stream()
                .filter(a -> a.isApplicable(post))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown type"));

        T assembledPost = targetClass.cast(assembler.assemble(entity, post));
        if (entity instanceof PostEntityWithCommunityId) {
            assembledPost.setCommunityId(((PostEntityWithCommunityId) entity).getCommunityId());
        }
        return assembledPost;
    }
}
