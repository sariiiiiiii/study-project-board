package com.todo.projectboard.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Spring Data REST 통합 테스트는 불필요하므로 제외시킴") // 전체 테스트시 클래스 레벨에 @Disabled를 해주었기 때문에 동작하지 않음
@DisplayName("Data REST - API 테스트")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class DataRestTest {

    private final MockMvc mockMvc;

    public DataRestTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticles_thenReturnsArticlesJsonResponse() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticles_thenReturnsArticleJsonResponse() throws Exception {
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andExpect(status().isOk());
    }

    @DisplayName("[api] 게시글 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleCommentsFromArticle_thenReturnsArticleCommentsJsonResponse() throws Exception {
        mockMvc.perform(get("/api/articles/1/articleComments"))
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andExpect(status().isOk());
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleComments_thenReturnsArticleCommentsJsonResponse() throws Exception {
        mockMvc.perform(get("/api/articleComments"))
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andExpect(status().isOk());
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticleComment_thenReturnsArticleCommentJsonResponse() throws Exception {
        mockMvc.perform(get("/api/articleComments/1"))
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andExpect(status().isOk());
    }

}
