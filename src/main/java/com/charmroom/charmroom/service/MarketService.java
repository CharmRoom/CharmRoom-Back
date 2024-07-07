package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.MarketDto;
import com.charmroom.charmroom.dto.business.MarketMapper;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Attachment;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.AttachmentRepository;
import com.charmroom.charmroom.repository.BoardRepository;
import com.charmroom.charmroom.repository.MarketRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final MarketRepository marketRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CharmroomUtil.Upload uploadUtils;
    private final AttachmentRepository attachmentRepository;

    public MarketDto create(MarketDto marketDto, String username, Integer boardId, List<MultipartFile> files) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER));

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_BOARD));

        Article article = Article.builder()
                .user(user)
                .board(board)
                .title(marketDto.getArticle().getTitle())
                .body(marketDto.getArticle().getBody())
                .build();

        for (MultipartFile file : files) {
            Attachment attachment = uploadUtils.buildAttachment(file, article);
            Attachment saved = attachmentRepository.save(attachment);
            article.getAttachmentList().add(saved);
        }

        Article savedArticle = articleRepository.save(article);

        Market market = Market.builder()
                .article(savedArticle)
                .price(marketDto.getPrice())
                .state(marketDto.getState())
                .tag(marketDto.getTag())
                .build();

        Market saved = marketRepository.save(market);
        return MarketMapper.toDto(saved);
    }

    public MarketDto create(MarketDto marketDto, String username, Integer boardId) {
        return create(marketDto, username, boardId, new ArrayList<>());
    }

    public Page<MarketDto> getMarkets(Integer boardId, Pageable pageable) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException
                        (BusinessLogicError.NOTFOUND_BOARD, "boardId: " + boardId));

        Page<Market> markets = marketRepository.findAllByBoard(board, pageable);

        return markets.map(market -> MarketMapper.toDto(market));
    }

    public MarketDto getMarket(Integer marketId) {
        Market found =  marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        return MarketMapper.toDto(found);
    }

    @Transactional
    public MarketDto updatePrice(Integer marketId, int price) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.updatePrice(price);
        return MarketMapper.toDto(market);
    }

    @Transactional
    public MarketDto updateTag(Integer marketId, String newTag) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.updateTag(newTag);
        return MarketMapper.toDto(market);
    }

    @Transactional
    public MarketDto updateState(Integer marketId, MarketArticleState newState) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.updateState(newState);
        return MarketMapper.toDto(market);
    }

    @Transactional
    public MarketDto updateArticle(Integer marketId, String title, String body) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.getArticle().updatedBody(body);
        market.getArticle().updateTitle(title);
        return MarketMapper.toDto(market);
    }

    public MarketDto update(Integer marketId, MarketDto marketDto) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.getArticle().updateTitle(marketDto.getArticle().getTitle());
        market.getArticle().updatedBody(marketDto.getArticle().getBody());
        market.updateState(marketDto.getState());
        market.updateTag(marketDto.getTag());
        market.updatePrice(marketDto.getPrice());

        return MarketMapper.toDto(market);
    }

    public void delete(Integer marketId) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        marketRepository.delete(market);
        articleRepository.delete(market.getArticle());
    }
}
