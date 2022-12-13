package com.todo.projectboard.service;

import com.todo.projectboard.domain.Article;
import com.todo.projectboard.domain.type.SearchType;
import com.todo.projectboard.dto.ArticleDto;
import com.todo.projectboard.dto.ArticleUpdateDto;
import com.todo.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList() {
        /**
         * 검색 목록
         * 제목, 본문, ID, 닉네임, 해시태그
         * Spring data JPA에서는 pagable interface가 잘되있기 때문에 pagination 수월.
         */

        // given

        // when
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");

        // then
        assertThat(articles).isNotNull();
    }

    @Test
    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        // given

        // when
        ArticleDto articleDto = sut.searchArticle(1L);

        // then
        assertThat(articleDto).isNotNull();
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
        given(articleRepository.save(any(Article.class))).willReturn(null); // articleRepository 의존성의 save 메소드를 호출할 것이다를 보여줌

        // when
        sut.saveArticle(ArticleDto.of(LocalDateTime.now(), "sari", "title", "content", "#java")); // 실제 테스트

        // then
        then(articleRepository).should().save(any(Article.class)); // 실제로 테스트 대상의 articleRepository.save() 메소드를 호출했나 확인
    }

    @Test
    @DisplayName("게시글의 ID와 수정 정보를 입력하면, 게시글을 수정한다.")
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // given
        given(articleRepository.save(any(Article.class))).willReturn(null); // articleRepository 의존성의 save 메소드를 호출할 것이다를 보여줌

        // when
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "content", "#java")); // 실제 테스트

        // then
        then(articleRepository).should().save(any(Article.class)); // 실제로 테스트 대상의 articleRepository.save() 메소드를 호출했나 확인
    }

    @Test
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // given
        willDoNothing().given(articleRepository).delete(any(Article.class)); // articleRepository 의존성의 save 메소드를 호출할 것이다를 보여줌

        // when
        sut.deleteArticle(1L); // 실제 테스트

        // then
        then(articleRepository).should().delete(any(Article.class)); // 실제로 테스트 대상의 articleRepository.save() 메소드를 호출했나 확인
    }

}