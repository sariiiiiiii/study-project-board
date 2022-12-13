package com.todo.projectboard.service;

import com.todo.projectboard.domain.Article;
import com.todo.projectboard.domain.ArticleComment;
import com.todo.projectboard.domain.UserAccount;
import com.todo.projectboard.dto.ArticleCommentDto;
import com.todo.projectboard.dto.UserAccountDto;
import com.todo.projectboard.repository.ArticleCommentRepository;
import com.todo.projectboard.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService sut;

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    @Mock
    private ArticleRepository articleRepository;


    @Test
    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
    void givenArticleId_whenSearchingArticleComments_thenReturnsArticleComments() {
        // given
        Long articleId = 1L;
        ArticleComment expected = createArticleComment("content");

        // when
        List<ArticleCommentDto> articleComments =  sut.searchArticleComment(articleId);

        // then
        assertThat(articleComments).isNotNull();
        then(articleRepository).should().findById(articleId);
    }


    @Test
    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    void givenArticleCommentInfo_whenSavingArticleComment_thenSavesArticleComment() {
        // given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // when
        sut.saveArticleComment(dto);

        // then
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @Test
    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다.")
    void givenArticleCommentId_whenDeletingArticleComment_thenDeletingArticleComment() {
        // given
        willDoNothing().given(articleCommentRepository).delete(any(ArticleComment.class));

        // when
        sut.deleteArticleComment(1L);

        // then
        then(articleCommentRepository).should().delete(any(ArticleComment.class));
    }

    /**
     * 테스트 코드 짤 동안 테스트용 데이터 세팅
     * 이런방식으로 코드를 작성하는것을 픽스처라고 한다.
     * 이런 픽스처는 테스트코드 내내 반복적으로 사용할 확률이 크다.
     * 클래스로 뽑아서 사용할수도 있음.
     */

    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                content,
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "uno",
                "password",
                "uno@mail.com",
                "Uno",
                "This is memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
                Article.of(createUserAccount(), "title", "content", "hashtag"),
                createUserAccount(),
                content
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "uno",
                "password",
                "uno@email.com",
                "Uno",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

}