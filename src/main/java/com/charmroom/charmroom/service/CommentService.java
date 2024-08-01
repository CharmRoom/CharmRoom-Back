package com.charmroom.charmroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.dto.business.CommentDto;
import com.charmroom.charmroom.dto.business.CommentMapper;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.CommentRepository;
import com.charmroom.charmroom.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final UserRepository userRepository;
	private final ArticleRepository articleRepository;
	private final CommentRepository commentRepository;
	
	public CommentDto create(Integer articleId, String username, String body, Integer parentId) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "id: " + articleId));
		
		Comment parent = null;
		if (parentId != 0) {
			parent = commentRepository.findById(parentId)
					.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_COMMENT, "id: " + parentId));
		}
		
		Comment comment = Comment.builder()
				.user(user)
				.article(article)
				.parent(parent)
				.body(body)
				.build();
		
		Comment saved = commentRepository.save(comment);
		return CommentMapper.toDto(saved);
	}
	
	public CommentDto create(Integer articleId, String username, String body) {
		return create(articleId, username, body, 0);
	}

	public Page<CommentDto> getComments(Integer articleId, Pageable pageable){
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "id: " + articleId));
		Page<Comment> comments = commentRepository.findAllByArticle(article, pageable);
		return comments.map(CommentMapper::toDto);
	}

	public Page<CommentDto> getCommentsByUsername(String username, Pageable pageable) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Page<Comment> comments = commentRepository.findAllByUser(user, pageable);
		return comments.map(CommentMapper::toDto);
	}
	@Transactional
	public CommentDto update(Integer commentId, String username, String body) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_COMMENT, "id: " + commentId));
		if (!comment.getUser().equals(user))
			throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_COMMENT, "username: " + username);
		comment.updateBody(body);
		return CommentMapper.toDto(comment);
	}
	
	@Transactional
	public CommentDto setDisable(Integer commentId, String username, Boolean disabled) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_COMMENT, "id: " + commentId));
		if (!comment.getUser().equals(user))
			throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_COMMENT, "username: " + username);
		comment.updateBody("");
		comment.setDisabled(disabled);
		return CommentMapper.toDto(comment);
	}
	
	@Transactional
	public CommentDto disable(Integer commentId, String username) {
		return setDisable(commentId, username, true);
	}
	
	@Transactional
	public CommentDto enable(Integer commentId, String username) {
		return setDisable(commentId, username, false);
	}

}
