package com.todo.projectboard.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.todo.projectboard.domain.Article;
import com.todo.projectboard.domain.QArticle;
import com.todo.projectboard.domain.QArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, // 기본적으로 Article안에 모든 필드에 대한 기본 검색기능을 추가해줌
        QuerydslBinderCustomizer<QArticle> // 선택적 검색기능으로 커스텀
{

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true); // QuerydslPredicateExecutor의 의해 모든 검색기능이 열린걸 선택적으로 변경
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy); // 검색기능 가능하게 할 명시
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase); // like '${v}'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // like '%${v}%' 대소문자 무시
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // like '%${v}%' 대소문자 무시
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase); // like '%${v}%' 대소문자 무시
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // eq = equals는 시분초까지 동일해야되기 때문에 이 검색기능은 맞지 않음
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }

}
