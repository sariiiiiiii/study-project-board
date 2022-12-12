package com.todo.projectboard.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    /**
     * WebMvcTest는 모든 Controller를 읽어드린다 그래서 테스트 대상이 되는 Controller만 Bean으로 읽어드리는게 가능
     *
     * @WebMvcTest(ArticleController.class)
     */

    private MockMvc mockMvc;

    public ArticleControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    public void givenNothing_whenRequsetingArticlesView_thenReturnsArticleView() throws Exception {
        // when & then
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // contentTypeCompatibleWith 옵션이 추가된 호환되는 타입까지 확인 ex) text/html;charset=UTF-8
                .andExpect(model().attributeExists("articles")) // modelAttribute의 articles란 키가 있는지 확인 (데이터를 받아올거기 때문에 넣어주자)
                .andExpect(view().name("articles/index"));// resolving되는 view page 이름 확인
    }

    @Test
    @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상 호출")
    public void givenNothing_whenRequsetingArticleView_thenReturnsArticleView() throws Exception {
        // when & then
        mockMvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("article")) // modelAttribute의 article란 키가 있는지 확인 (데이터를 받아올거기 때문에 넣어주자)
                .andExpect(model().attributeExists("articleComments")) // modelAttribute의 articleComments란 키가 있는지 확인 (데이터를 받아오고 그에대한 댓글이 있는지 확인)
                .andExpect(view().name("articles/detail"));

    }

    @Disabled("구현 중")
    @Test
    @DisplayName("[view] [GET] 게시글 검색 전용 페이지 - 정상 호출")
    public void givenNothing_whenRequsetingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
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

}