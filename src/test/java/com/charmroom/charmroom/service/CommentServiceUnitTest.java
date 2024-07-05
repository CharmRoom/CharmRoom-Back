package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.CommentRepository;
import com.charmroom.charmroom.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ArticleRepository articleRepository;
	
	@InjectMocks
	private CommentService commentService;
	
	private User user;
	private Article article;
	private Comment comment;
	private Comment childComment;
	
	@BeforeEach
	void setup() {
		user = User.builder()
				.username("username")
				.build();
		article = Article.builder()
				.id(1)
				.build();
		comment = Comment.builder()
				.id(1)
				.user(user)
				.article(article)
				.parent(null)
				.body("test")
				.build();
		childComment = Comment.builder()
				.id(2)
				.user(user)
				.article(article)
				.parent(comment)
				.body("child")
				.build();
	}
	
	@Nested
	class Create{
		@Test
		void success() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());
			doReturn(comment).when(commentRepository).save(any(Comment.class));
			// when
			var saved = commentService.create(article.getId(), user.getUsername(), "test");
			
			// then
			assertThat(saved).isNotNull();
		}
		
		@Test
		void failByNotfoundUser() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(any(String.class));
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> { 
				commentService.create(article.getId(), user.getUsername(), "test");
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + user.getUsername());
		}
		@Test
		void failByNotfoundArticle() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.empty()).when(articleRepository).findById(any(Integer.class));
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> { 
				commentService.create(article.getId(), user.getUsername(), "test");
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_ARTICLE);
			assertThat(thrown.getMessage()).isEqualTo("id: " + article.getId());
		}
	}
	
	@Nested
	class CreateChildComment{
		@Test
		void success() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			doReturn(childComment).when(commentRepository).save(any(Comment.class));
			// when
			var saved = commentService.create(article.getId(), user.getUsername(), "child", comment.getId());
			
			// then
			assertThat(saved).isNotNull();
		}
		@Test
		void failByNotfoundComment() {
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());
			doReturn(Optional.empty()).when(commentRepository).findById(comment.getId());
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, ()->{
				commentService.create(article.getId(), user.getUsername(), "child", comment.getId());
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_COMMENT);
			assertThat(thrown.getMessage()).isEqualTo("id: " + comment.getId());
		}
	}
	
	@Nested
	class UpdateComment{
		@Test
		void success() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			
			// when
			var updated = commentService.update(comment.getId(), user.getUsername(), "updated");
			
			// then
			assertThat(updated.getBody()).isEqualTo("updated");
		}
		@Test
		void failByNotfoundUser() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(user.getUsername());
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				commentService.update(comment.getId(), user.getUsername(), "updated");
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + user.getUsername());
		}
		
		@Test
		void failByNotfoundComment() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.empty()).when(commentRepository).findById(comment.getId());
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				commentService.update(comment.getId(), user.getUsername(), "updated");
			});
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_COMMENT);
			assertThat(thrown.getMessage()).isEqualTo("id: " + comment.getId());	
		}
		
		@Test
		void failByUnauthorizedComment() {
			// given
			User otheruser = User.builder()
					.username("otheruser")
					.build();
			doReturn(Optional.of(otheruser)).when(userRepository).findByUsername(otheruser.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				commentService.update(comment.getId(), otheruser.getUsername(), "updated");
			});
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.UNAUTHORIZED_COMMENT);
			assertThat(thrown.getMessage()).isEqualTo("username: " + otheruser.getUsername());
		}
	}
	
	@Nested
	class Delete{
		@Test
		void success() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			
			// when
			var disabled = commentService.disable(comment.getId(), user.getUsername());
			
			// then
			assertThat(disabled.isDisabled()).isTrue();
		}
		// 실패 상황은 Update와 동일
	}
}
