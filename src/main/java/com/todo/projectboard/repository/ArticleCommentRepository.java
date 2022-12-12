package com.todo.projectboard.repository;

import com.todo.projectboard.domain.ArticleComment;
import com.todo.projectboard.domain.QArticle;
import com.todo.projectboard.domain.QArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>, // 엔티티 필드에 대한 검색기능 추가
        QuerydslBinderCustomizer<QArticleComment> {


    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {

    }


}
