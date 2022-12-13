package com.todo.projectboard.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("View 컨트롤러 - 인증")
@WebMvcTest
public class AuthControllerTest {

    private final MockMvc mockMvc;

    public AuthControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("[view] [GET] 로그인 페이지 - 정상 호출")
    public void givenNothing_whenTryingToLogIn_thenReturnsLogInView() throws Exception {
        // when & then
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)); // contentTypeCompatibleWith 옵션이 추가된 호환되는 타입까지 확인 ex) text/html;charset=UTF-8
//                .andExpect(model().attributeExists("articles")); // modelAttribute의 articles란 키가 있는지 확인 (데이터를 받아올거기 때문에 넣어주자)
//                .andExpect(view().name("articles/index")); -> view name은 검사 안함 thymeleaf + spring security에서 자동 생성해주기 때문
    }


}
