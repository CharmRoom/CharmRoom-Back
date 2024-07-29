package com.charmroom.charmroom.controller.api;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.ArticleLikeDto;
import com.charmroom.charmroom.dto.business.ArticleLikeMapper;
import com.charmroom.charmroom.dto.business.ArticleMapper;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleResponseDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleUpdateRequestDto;
import  com.charmroom.charmroom.dto.presentation.ArticleLikeDto.ArticleLikeResponseDto;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.ArticleLikeService;
import com.charmroom.charmroom.service.ArticleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{boardId}")
    public ResponseEntity<?> addArticle(
            @PathVariable(value = "boardId") Integer boardId,
            @ModelAttribute @Valid ArticleCreateRequestDto requestDto,
            @AuthenticationPrincipal User user) {

        ArticleDto article;
        if (requestDto.getFile() == null) {
            article = articleService.createArticle(user.getUsername(), boardId, requestDto.getTitle(), requestDto.getBody());
        } else {
            article = articleService.createArticle(user.getUsername(), boardId, requestDto.getTitle(), requestDto.getBody(), requestDto.getFile());
        }
        ArticleResponseDto responseDto = ArticleMapper.toResponse(article);
        return CommonResponseDto.created(responseDto).toResponseEntity();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{articleId}")
    public ResponseEntity<?> getArticle(
            @PathVariable("articleId") Integer articleId) {
        ArticleDto article = articleService.getOneArticle(articleId);
        ArticleResponseDto responseDto = ArticleMapper.toResponse(article);
        return CommonResponseDto.ok(responseDto).toResponseEntity();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{boardId}/articles")
    public ResponseEntity<?> getArticleList(
            @PathVariable("boardId") Integer boardId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ArticleDto> dtos = articleService.getArticles(boardId, pageable);
        Page<ArticleResponseDto> responseDtos = dtos.map(ArticleMapper::toResponse);
        return CommonResponseDto.ok(responseDtos).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{articleId}")
    public ResponseEntity<?> updateArticle(
            @PathVariable("articleId") Integer articleId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ArticleUpdateRequestDto request
    ) {
        ArticleDto articleDto = articleService.updateArticle(articleId, user.getUsername(), request.getTitle(), request.getBody());
        ArticleResponseDto responseDto = ArticleMapper.toResponse(articleDto);
        return CommonResponseDto.ok(responseDto).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable("articleId") Integer articleId,
            @AuthenticationPrincipal User user
    ) {
        articleService.deleteArticle(articleId, user.getUsername());
        return CommonResponseDto.ok().toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{articleId}")
    public ResponseEntity<?> likeArticle(
            @AuthenticationPrincipal User user,
            @PathVariable("articleId") Integer articleId
    ) {
        ArticleLikeDto likeDto = articleLikeService.like(user.getUsername(), articleId);
        ArticleLikeResponseDto response = ArticleLikeMapper.toResponse(likeDto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/dislike/{articleId}")
    public ResponseEntity<?> dislikeArticle(
            @AuthenticationPrincipal User user,
            @PathVariable("articleId") Integer articleId
    ) {
        ArticleLikeDto likeDto = articleLikeService.dislike(user.getUsername(), articleId);
        ArticleLikeResponseDto response = ArticleLikeMapper.toResponse(likeDto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }
}
