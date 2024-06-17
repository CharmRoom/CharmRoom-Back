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
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.AttachmentType;
import com.charmroom.charmroom.entity.enums.BoardType;

@DataJpaTest
@TestPropertySource(properties = { "spring.config.location = classpath:application-test.yml" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Attachment Repository 단위 테스트")
public class AttachmentRepositoryUnitTest {
	@Autowired
	private AttachmentRepository attachmentRepository;
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
	
	private Article article;
	private Attachment attachment;
	
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

		return User.builder().username("test").password("test").email("test@test.com").nickname("test").image(image)
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
	
	private Attachment buildAttachment(Article article) {
		return Attachment.builder()
				.article(article)
				.path("/example/path")
				.type(AttachmentType.IMAGE)
				.build();
	}
	
	@BeforeEach
	void setup() {
		article = buildArticle();
		articleRepository.save(article);
		attachment = buildAttachment(article);
	}
	
	@Nested
	class Create {
		@Test
		public void success() {
			// given
			// when
			Attachment saved = attachmentRepository.save(attachment);
			// then
			assertThat(saved).isNotNull();
			assertThat(saved).isEqualTo(attachment);
		}
	}

	@Nested
	class Read {
		@Test
		public void success() {
			// given
			Attachment saved = attachmentRepository.save(attachment);

			// when
			var found = attachmentRepository.findById(attachment.getId());
			// then
			assertThat(found).isPresent();
			assertThat(found).get().isEqualTo(saved);
		}

		@Test
		public void fail() {
			// given
			attachmentRepository.save(attachment);

			// when
			var found = attachmentRepository.findById(12345);

			// then
			assertThat(found).isNotPresent();
		}
	}

	@Nested
	class Delete {
		@Test
		public void success() {
			// given
			Attachment saved = attachmentRepository.save(attachment);

			// when
			attachmentRepository.delete(saved);

			// then
			var found = attachmentRepository.findAll();
			assertThat(found).isNotNull();
			assertThat(found).isEmpty();
		}
	}
}
