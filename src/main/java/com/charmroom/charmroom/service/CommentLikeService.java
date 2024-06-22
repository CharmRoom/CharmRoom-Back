package com.charmroom.charmroom.service;

import org.springframework.stereotype.Service;

import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.CommentLike;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.CommentLikeRepository;
import com.charmroom.charmroom.repository.CommentRepository;
import com.charmroom.charmroom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	
	public CommentLike likeOrDislike(String username, Integer commentId, Boolean type) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_COMMENT, "id: " + commentId));
		
		var findCommentLike = commentLikeRepository.findByUserAndComment(user, comment);
		
		if (findCommentLike.isPresent()) {
			CommentLike commentLike = findCommentLike.get();
			if (commentLike.getType() == type) {
				commentLikeRepository.delete(commentLike);
				return null;
			}
			
			commentLike.updateType(type);
			return commentLike;	
		}
		
		CommentLike commentLike = CommentLike.builder()
				.user(user)
				.comment(comment)
				.type(type)
				.build();
		return commentLikeRepository.save(commentLike);
	}
	
	public CommentLike like(String username, Integer commentId) {
		return likeOrDislike(username, commentId, true);
	}
	
	public CommentLike dislike(String username, Integer commentId) {
		return likeOrDislike(username, commentId, false);
	}
	
}
