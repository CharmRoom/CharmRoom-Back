package com.charmroom.charmroom.controller.api;

import com.charmroom.charmroom.dto.business.ArticleDto;
import com.charmroom.charmroom.dto.business.MarketDto;
import com.charmroom.charmroom.dto.business.MarketMapper;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.MarketDto.MarketCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.MarketDto.MarketResponseDto;
import com.charmroom.charmroom.dto.presentation.MarketDto.MarketUpdateRequestDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.ArticleService;
import com.charmroom.charmroom.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.charmroom.charmroom.dto.presentation.ArticleDto.ArticleCreateRequestDto;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;
    private final ArticleService articleService;

    @PostMapping("/{boardId}")
    public ResponseEntity<?> addMarket(
            @PathVariable("boardId") Integer boardId,
            @ModelAttribute MarketCreateRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        MarketDto created;

        ArticleDto article = ArticleDto.builder()
                .title(requestDto.getArticle().getTitle())
                .body(requestDto.getArticle().getBody())
                .build();

        MarketDto marketDto = MarketDto.builder()
                .article(article)
                .state(requestDto.getState())
                .tag(requestDto.getTag())
                .price(requestDto.getPrice())
                .build();

        if (requestDto.getArticle().getFile() == null) {
            created = marketService.create(marketDto, user.getUsername(), boardId);
        } else {
            created = marketService.create(marketDto, user.getUsername(), boardId, requestDto.getArticle().getFile());
        }

        MarketResponseDto response = MarketMapper.toResponse(created);
        return CommonResponseDto.created(response).toResponseEntity();
    }

    @GetMapping("/{marketId}")
    public ResponseEntity<?> getMarket(
            @PathVariable("marketId") Integer marketId
    ) {
        MarketDto marketDto = marketService.getMarket(marketId);
        MarketResponseDto response = MarketMapper.toResponse(marketDto);

        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @GetMapping("/list/{boardId}")
    public ResponseEntity<?> getMarketList(
            @PathVariable("boardId") Integer boardId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MarketDto> dtos = marketService.getMarkets(boardId, pageable);
        Page<MarketResponseDto> responseDtos = dtos.map(dto -> MarketMapper.toResponse(dto));

        return CommonResponseDto.ok(responseDtos).toResponseEntity();
    }

    @PatchMapping("/{marketId}")
    public ResponseEntity<?> updateMarket(
            @PathVariable("marketId") Integer marketId,
            @AuthenticationPrincipal User user,
            @RequestBody MarketUpdateRequestDto request
    ) {
        ArticleDto articleDto = ArticleDto.builder()
                .title(request.getArticle().getTitle())
                .body(request.getArticle().getBody())
                .build();

        MarketDto marketDto = MarketDto.builder()
                .article(articleDto)
                .price(request.getPrice())
                .tag(request.getTag())
                .state(request.getState())
                .build();

        MarketDto dto = marketService.update(marketId, marketDto);

        MarketResponseDto response = MarketMapper.toResponse(dto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @DeleteMapping("/{marketId}")
    public ResponseEntity<?> deleteMarket(
            @PathVariable("marketId") Integer marketId,
            @AuthenticationPrincipal User user
    ) {
        marketService.delete(marketId);
        return CommonResponseDto.ok().toResponseEntity();
    }
}
