package com.binghe.querycounter.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.binghe.querycounter.query_counter.QueryCounter;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class QueryCountPostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private QueryCounter queryCounter;

    @DisplayName("N + 1 발생 테스트 - Post들을 조회한다.")
    @Test
    void findPosts() {
        // given
        Post post1 = createPostWithComment();
        Post post2 = createPostWithComment();
        Post post3 = createPostWithComment();

        // when
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        flushAndClear();

        // then
        queryCounter.startCount();
        List<Post> posts = postRepository.findAll();
        posts.get(0).getComments().size();
        posts.get(1).getComments().size();
        posts.get(2).getComments().size();
        assertThat(queryCounter.getCount().getValue()).isEqualTo(1L);
        System.out.println("## 총 발생한 쿼리 : " + queryCounter.getCount().getValue());
    }

    private Post createPostWithComment() {
        Comment comment1 = new Comment("comment1");
        Comment comment2 = new Comment("comment2");
        Post post = new Post("title1", "content1");

        post.addComment(comment1);
        post.addComment(comment2);
        return post;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
