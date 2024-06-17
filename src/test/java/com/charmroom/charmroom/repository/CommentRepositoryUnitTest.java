package com.charmroom.charmroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;

@DataJpaTest
@TestPropertySource(properties = { "spring.config.location = classpath:application-test.yml" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Comment Repository 단위 테스트")
public class CommentRepositoryUnitTest {
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private ClubRepository clubRepository;
	@Autowired
	private BoardRepository boardRepository;

	private User user;
	private Article article;
	private Comment comment;

	private Image buildImage() {
		return Image.builder().path("/example/example").build();
	}

	private Club buildClub() {
		Image clubImage = buildImage();
		imageRepository.save(clubImage);

		return Club.builder().name("").contact("").description("").image(clubImage).build();
	}

	private User buildUser(String username) {
		Image image = buildImage();
		imageRepository.save(image);

		Club club = buildClub();
		clubRepository.save(club);

		return User.builder().username(username).password("test").email("test@test.com").nickname("test").image(image)
				.club(club).build();
	}

	private Board buildBoard() {
		return Board.builder().type(BoardType.LIST).build();
	}

	private Article buildArticle() {
		Board board = buildBoard();
		boardRepository.save(board);
		User user = buildUser("11111");
		userRepository.save(user);

		return Article.builder().user(user).board(board).title("title").body("body").build();
	}

	private Comment buildComment(User user, Article article) {
		return Comment.builder()
				.user(user)
				.article(article)
				.body("contents").build();
	}

	@BeforeEach
	void setup() {
		user = buildUser("22222");
		userRepository.save(user);
		article = buildArticle();
		articleRepository.save(article);
		comment = buildComment(user, article);
	}

	@Nested
	class Create {
		@Test
		public void success() {
			// given
			// when
			Comment saved = commentRepository.save(comment);
			// then
			assertThat(saved).isNotNull();
			assertThat(saved).isEqualTo(comment);
		}
	}

	@Nested
	class Read {
		@Test
		public void success() {
			// given
			Comment saved = commentRepository.save(comment);

			// when
			var found = commentRepository.findById(comment.getId());
			// then
			assertThat(found).isPresent();
			assertThat(found).get().isEqualTo(saved);
		}

		@Test
		public void fail() {
			// given
			commentRepository.save(comment);

			// when
			var found = commentRepository.findById(12345);

			// then
			assertThat(found).isNotPresent();
		}
	}

	@Nested
	class Update {
		@Test
		public void success() {
			// given
			Comment saved = commentRepository.save(comment);

			// when
			saved.updateBody("New contents");
			// then
			var found = commentRepository.findById(comment.getId()).get();
			assertThat(found).isNotNull();
			assertThat(found.getBody()).isEqualTo("New contents");
		}
	}

	@Nested
	class Delete {
		@Test
		public void success() {
			// given
			Comment saved = commentRepository.save(comment);

			// when
			commentRepository.delete(saved);

			// then
			var found = commentRepository.findAll();
			assertThat(found).isNotNull();
			assertThat(found).isEmpty();
		}
	}
}
