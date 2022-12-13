package com.todo.projectboard.service;

import com.todo.projectboard.domain.Article;
import com.todo.projectboard.domain.UserAccount;
import com.todo.projectboard.domain.type.SearchType;
import com.todo.projectboard.dto.ArticleDto;
import com.todo.projectboard.dto.ArticleWithCommentsDto;
import com.todo.projectboard.dto.UserAccountDto;
import com.todo.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService sut; // sut = system under test 테스트 대상이다란 뜻

    @Mock
    private ArticleRepository articleRepository;


    @Test
    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 리스트를 반환한다.")
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        /**
         * 검색 목록
         * 제목, 본문, ID, 닉네임, 해시태그
         * Spring data JPA에서는 pagable interface가 잘되있기 때문에 pagination 수월.
         */
        // given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @Test
    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        // given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        // then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @Test
    @DisplayName("게시글을 정보를 입력하면 게시글을 생성한다.")
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {

        /**
         * mockito를 이용해서 application을 실행시키지 않고 목객체(가짜 객체)를 만들어서 테스트 진행
         * JpaRepository.save는 Entity를 반환해주기 때문에
         * willReturn(any(Article.class))를 명시
         * 
         * 반환 타입이 없을 경우는 아래 테스트처럼 사용한다
         * BDDMockito.given()을 사용하지 않고 BDDMockito.willDoNothing().given()을 사용
         * willDoNothing은 해당 기능이 있는것이 아니라 코드의 명시적으로 반환타입이 없다는걸 표현해주는것에 불과
         */

        // given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // when
        sut.saveArticle(dto); // 실제 테스트

        // then
        then(articleRepository).should().save(any(Article.class)); // 실제로 테스트 대상의 articleRepository.save() 메소드를 호출했나 확인
    }

    @Test
    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        // when
        sut.updateArticle(dto);

        // then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @Test
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        // when
        sut.deleteArticle(1L); // 실제 테스트

        // then
        then(articleRepository).should().deleteById(articleId);
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

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "Uno",
                LocalDateTime.now(),
                "Uno");
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

}