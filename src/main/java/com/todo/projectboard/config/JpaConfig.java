package com.todo.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing // jpa 기능 활성화
@Configuration
public class JpaConfig {

    /**
     * createdBy, modifiedBy
     */

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("sari"); // TODO: 스프링 시큐리티로 인증 기능을 붙이게 될 때, 수정
    }

}
