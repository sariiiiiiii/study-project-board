package com.todo.projectboard.repository;

import com.todo.projectboard.config.JpaConfig;
import com.todo.projectboard.domain.Article;
import com.todo.projectboard.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testdb")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class) // DataJpaTest를 진행하고 싶은데 JpaConfig는 내가 지정해준 Configuration Bean 설정이기 때문에 명시를 해줘야 됨
@DataJpaTest // JpaTest를 위한 annotation
class JpaRepositoryTest {

    /**
     * @DatajpaTest를 추가하는 순간 test가 실행될 때 AutoConfiguration에서 알아서 test DB를 실행한다
     * 우리는 yml파일에 profile: testdb를 설정을 해주었는데 알아서 실행해버리기 때문에 이 기능을 무시하기 위해선
     * @ActiveProfiles를 yml에서 설정해준 파일로 지정하고
     * @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)를 설정해준다 (메모리 db로 하지 않고 yml에 설정된 파일)
     *
     * 지역설정은 @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
     * 전역설정은 yml에서 test.database.replace: none
     */

    private final ArticleRepository articleRepository;

    private final ArticleCommentRepository articleCommentRepository;

    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository, @Autowired ArticleCommentRepository articleCommentRepository, @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // given

        // when
        List<Article> articles = articleRepository.findAll();

        // then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // given
        long previousCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("sari", "pw", null, null, null));
        Article article = Article.of(userAccount, "new article", "new content", "#spring");

        // when
        articleRepository.save(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashing = "#SpringBoot";
        article.setHashtag(updatedHashing);

        // when
        Article savedArticle = articleRepository.saveAndFlush(article); // save 후 flush

        // then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashing);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // when
        articleRepository.delete(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }

}








