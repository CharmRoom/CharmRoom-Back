package com.charmroom.charmroom.controller.api;

import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.ArticleMapper;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleResponseDto;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/{boardId}")
    public ResponseEntity<?> addArticle(
            @PathVariable(value = "boardId") Integer boardId,
            @ModelAttribute ArticleCreateRequestDto requestDto,
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

    @GetMapping("/{articleId}")
    public ResponseEntity<?> getArticle(
            @PathVariable("articleId") Integer articleId) {
        ArticleDto article = articleService.getOneArticle(articleId);
        ArticleResponseDto responseDto = ArticleMapper.toResponse(article);
        return CommonResponseDto.ok(responseDto).toResponseEntity();
    }

    @GetMapping("/{boardId}/articles")
    public ResponseEntity<?> getArticleList(
            @PathVariable("boardId") Integer boardId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ArticleDto> dtos = articleService.getArticles(boardId, pageable);
        Page<ArticleResponseDto> responseDtos = dtos.map(dto -> ArticleMapper.toResponse(dto));

        return CommonResponseDto.ok(responseDtos).toResponseEntity();
    }
}
