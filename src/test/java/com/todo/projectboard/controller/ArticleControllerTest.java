package com.todo.projectboard.controller;

import com.todo.projectboard.config.SecurityConfig;
import com.todo.projectboard.dto.ArticleCommentDto;
import com.todo.projectboard.dto.ArticleDto;
import com.todo.projectboard.dto.ArticleWithCommentsDto;
import com.todo.projectboard.dto.UserAccountDto;
import com.todo.projectboard.service.ArticleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class) // SecurityConfig를 열어줘야지 테스트를 할 수 있음, 현재 SecurityConfig에서 모든 페이지를 열어놨기 때문에 테스트를 통과할 수 있음
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    /**
     * WebMvcTest는 모든 Controller를 읽어드린다 그래서 테스트 대상이 되는 Controller만 Bean으로 읽어드리는게 가능
     *
     * @WebMvcTest(ArticleController.class)
     */

    private MockMvc mockMvc;

    /**
     * ArticleController에서 ArticleService를 배제하기 위해서 MockMvc가 api의 입출력만 보기 위해서
     * ArticleService와 끊어줘야 되는데 이때 mocking을 해야되는데 이때 springtest에서 지원하는 @MockBean 사용
     * 스프링 슬라이스테스트에서 사용
     *
     * @Autowired는 생성자 파라미터로 넣을 수 있는데 @MockBean은 필드주입만 가능
     */
    @MockBean
    private ArticleService articleService;

    public ArticleControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * BDDMockito.given() 테스트시 파라미터는 ArgumentMatchers...()로 넣어줘야됨
     */

    @Test
    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    public void givenNothing_whenRequsetingArticlesView_thenReturnsArticleView() throws Exception {
        // given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());

        // when & then
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // contentTypeCompatibleWith 옵션이 추가된 호환되는 타입까지 확인 ex) text/html;charset=UTF-8
                .andExpect(model().attributeExists("articles")) // modelAttribute의 articles란 키가 있는지 확인 (데이터를 받아올거기 때문에 넣어주자)
                .andExpect(view().name("articles/index"));// resolving되는 view page 이름 확인

        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상 호출")
    public void givenNothing_whenRequsetingArticleView_thenReturnsArticleView() throws Exception {
        // given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

        // when & then
        mockMvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("article")) // modelAttribute의 article란 키가 있는지 확인 (데이터를 받아올거기 때문에 넣어주자)
                .andExpect(model().attributeExists("articleComments")) // modelAttribute의 articleComments란 키가 있는지 확인 (데이터를 받아오고 그에대한 댓글이 있는지 확인)
                .andExpect(view().name("articles/detail"));

        then(articleService).should().getArticle(articleId);
    }

    @Disabled("구현 중")
    @Test
    @DisplayName("[view] [GET] 게시글 검색 전용 페이지 - 정상 호출")
    public void givenNothing_whenRequsetingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // 검색화면에는 데이터가 현재상황에서는 없어야 되니까 attribute 확인은 X
                .andExpect(view().name("articles/search"));

    }

    @Disabled("구현 중")
    @Test
    @DisplayName("[view] [GET] 게시글 해시태그 검색 전용 페이지 - 정상 호출")
    public void givenNothing_whenRequsetingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        // when & then
        mockMvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // 검색화면에는 데이터가 없어야 되니까 attribute 확인은 X
                .andExpect(view().name("articles/search-hashtag"));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "sari",
                LocalDateTime.now(),
                "sari"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "sari",
                "pw",
                "www.google.com",
                "Sari",
                "memo",
                LocalDateTime.now(),
                "sari",
                LocalDateTime.now(),
                "sari"
        );
    }

}