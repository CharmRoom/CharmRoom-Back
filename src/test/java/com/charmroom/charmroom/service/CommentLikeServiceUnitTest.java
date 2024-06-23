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

import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.CommentLike;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.CommentLikeRepository;
import com.charmroom.charmroom.repository.CommentRepository;
import com.charmroom.charmroom.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CommentLikeServiceUnitTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private CommentLikeRepository commentLikeRepository;
	
	@InjectMocks
	private CommentLikeService commentLikeService;
	
	private User user;
	private Comment comment;
	private CommentLike commentLike;
	private CommentLike commentDislike;
	
	@BeforeEach
	void setup() {
		user = User.builder()
				.username("username")
				.build();
		comment = Comment.builder()
				.id(1)
				.build();
		commentLike = CommentLike.builder()
				.user(user)
				.comment(comment)
				.type(true)
				.build();
		commentDislike = CommentLike.builder()
				.user(user)
				.comment(comment)
				.type(false)
				.build();
	}
	
	@Nested
	class LikeAndDislike {
		@Test
		void like() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			doReturn(Optional.empty()).when(commentLikeRepository).findByUserAndComment(user, comment);
			doReturn(commentLike).when(commentLikeRepository).save(any(CommentLike.class));
			
			// when
			var result = commentLikeService.like(user.getUsername(), comment.getId());
			
			// then
			assertThat(result).isEqualTo(commentLike);
		}
		
		@Test
		void dislike() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			doReturn(Optional.empty()).when(commentLikeRepository).findByUserAndComment(user, comment);
			doReturn(commentDislike).when(commentLikeRepository).save(any(CommentLike.class));
			
			// when
			var result = commentLikeService.dislike(user.getUsername(), comment.getId());
			
			// then
			assertThat(result).isEqualTo(commentDislike);
		}
		
		@Test
		void likeCancel() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			doReturn(Optional.of(commentLike)).when(commentLikeRepository).findByUserAndComment(user, comment);
			
			// when
			var result = commentLikeService.like(user.getUsername(), comment.getId());
			
			// then
			assertThat(result).isNull();
		}
		
		@Test
		void dislikeCancel() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			doReturn(Optional.of(commentDislike)).when(commentLikeRepository).findByUserAndComment(user, comment);
			
			// when
			var result = commentLikeService.dislike(user.getUsername(), comment.getId());
			
			// then
			assertThat(result).isNull();
		}
		
		@Test
		void likeToDislike() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			doReturn(Optional.of(commentLike)).when(commentLikeRepository).findByUserAndComment(user, comment);
			
			// when
			var result = commentLikeService.dislike(user.getUsername(), comment.getId());
			
			// then
			assertThat(result).isNotNull();
			assertThat(result.getType()).isFalse();
		}
		@Test
		void dislikeToLike() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(Optional.of(comment)).when(commentRepository).findById(comment.getId());
			doReturn(Optional.of(commentDislike)).when(commentLikeRepository).findByUserAndComment(user, comment);
			
			// when
			var result = commentLikeService.like(user.getUsername(), comment.getId());
			
			// then
			assertThat(result).isNotNull();
			assertThat(result.getType()).isTrue();
		}
		@Test
		void failByNotfoundUser() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(user.getUsername());
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, ()->{
				commentLikeService.like(user.getUsername(), comment.getId());
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
			var thrown = assertThrows(BusinessLogicException.class, ()->{
				commentLikeService.like(user.getUsername(), comment.getId());
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_COMMENT);
			assertThat(thrown.getMessage()).isEqualTo("id: " + comment.getId());
		}
	}
}
