package com.todo.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//@EnableWebSecurity -> 안넣어도 됨 springboot autoconfiguration에 들어가있음
@Configuration 
public class SecurityConfig {

    /**
     * springboot 2.7 version 이후부터는 클래스에서 WebSecurityConfigurerAdapter를 상속받는것을 deprecated 한다.
     * 대신 @Bean으로 SecurityFileterChain을 반환한다.
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // auth를 받고, auth의 어떤 request 든 다 열겠다
                .formLogin().and() // login page를 만들게끔 유도
                .build();
    }

}
