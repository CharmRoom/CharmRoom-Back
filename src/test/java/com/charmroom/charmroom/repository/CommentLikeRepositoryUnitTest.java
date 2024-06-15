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
import com.charmroom.charmroom.entity.CommentLike;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;

@DataJpaTest
@TestPropertySource(properties = { "spring.config.location = classpath:application-test.yml" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("CommentLike Repository 단위 테스트")
public class CommentLikeRepositoryUnitTest {
	@Autowired
	private CommentLikeRepository commentLikeRepository;
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
	private Comment comment;
	private CommentLike commentLike;

	private Image buildImage() {
		return Image.builder().path("/example/example").build();
	}

	private Club buildClub() {
		Image clubImage = buildImage();
		imageRepository.save(clubImage);

		return Club.builder().name("").contact("").description("").image(clubImage).build();
	}

	private User buildUser() {
		Image image = buildImage();
		imageRepository.save(image);

		Club club = buildClub();
		clubRepository.save(club);

		return User.builder().id("test").password("test").email("test@test.com").nickname("test").image(image)
				.club(club).build();
	}

	private Board buildBoard() {
		return Board.builder().type(BoardType.LIST).build();
	}

	private Article buildArticle() {
		Board board = buildBoard();
		boardRepository.save(board);
		User user = buildUser();
		userRepository.save(user);

		return Article.builder().user(user).board(board).title("title").body("body").build();
	}

	private Comment buildComment() {
		User user = buildUser();
		userRepository.save(user);
		Article article = buildArticle();
		articleRepository.save(article);
		return Comment.builder().user(user).article(article).body("contents").build();
	}

	private CommentLike buildCommentLike(User user, Comment comment) {
		return CommentLike.builder()
				.user(user)
				.comment(comment)
				.type(true).build();
	}

	@BeforeEach
	void setup() {
		user = buildUser();
		userRepository.save(user);
		comment = buildComment();
		commentRepository.save(comment);
		commentLike = buildCommentLike(user, comment);
	}
	
	@Nested
	class Create {
		@Test
		public void success() {
			// given
			// when
			CommentLike saved = commentLikeRepository.save(commentLike);
			// then
			assertThat(saved).isNotNull();
			assertThat(saved.getId()).isEqualTo(commentLike.getId());
		}
	}

	@Nested
	class Read {
		@Test
		public void success() {
			// given
			CommentLike saved = commentLikeRepository.save(commentLike);

			// when
			var found = commentLikeRepository.findByUserAndComment(user, comment);
			// then
			assertThat(found).isPresent();
			assertThat(found).get().isEqualTo(saved);
		}

		@Test
		public void fail() {
			// given
			commentLikeRepository.save(commentLike);
			
			// when
			Comment tmp = Comment.builder().build();
			commentRepository.save(tmp);
			var found = commentLikeRepository.findByUserAndComment(user, tmp);

			// then
			assertThat(found).isNotPresent();
		}
	}

	@Nested
	class Delete {
		@Test
		public void success() {
			// given
			CommentLike saved = commentLikeRepository.save(commentLike);

			// when
			commentLikeRepository.delete(saved);

			// then
			var found = commentLikeRepository.findAll();
			assertThat(found).isNotNull();
			assertThat(found).isEmpty();
		}
	}
}
