package com.todo.projectboard.repository;

import com.todo.projectboard.domain.Article;
import com.todo.projectboard.domain.QArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, // 기본적으로 Article안에 모든 필드에 대한 기본 검색기능을 추가해줌
        QuerydslBinderCustomizer<QArticle>
{
}
