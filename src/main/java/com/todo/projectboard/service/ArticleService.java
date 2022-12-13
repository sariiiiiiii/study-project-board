package com.todo.projectboard.service;

import com.todo.projectboard.domain.Article;
import com.todo.projectboard.domain.type.SearchType;
import com.todo.projectboard.dto.ArticleDto;
import com.todo.projectboard.dto.ArticleWithCommentsDto;
import com.todo.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        /**
         * service계층에서는 Article domain과 AricleDto까지만 안다
         * Controller에서 ArticleDto를 바로 return 시켜줄지 service계층에서는 모름 dto로 보내도 상관없음 entity를 dto로 변환해서 return 해주기 때문
         *
         * ENUM 타입에 따라 query이 다르게 나가야 하기 때문에 분기처리
         */
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);
        };

    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {
        /**
         * 개발자는 dto의 id가 있는 것을 알아서 굳이 select문 날리고 싶지 않다 (id값이 있으니까 잘 쓰고 넘어왔겠지)
         * 그러나, findById는 select문을 날리기 때문에 그게 싫어서 나온것이 getReferenceById();
         * getReferenceById()는 해당 데이터가 없으면 EntityNotFoundException 발생
         *
         * hashtag는 nullable = true여서 null값 상관없는데 title과 content는 null이 들어가면 안되기 때문에 파라미터로 넘어온 dto의 title과 content를 확인 후 방어로직을 작성
         */
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            if (dto.title() != null) { article.setTitle(dto.title()); }
            if (dto.content() != null) { article.setContent(dto.content()); }
            article.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}", dto);
        }
    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }
}
