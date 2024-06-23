package com.charmroom.charmroom.service;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.enums.MarketArticleState;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final MarketRepository marketRepository;
    private final ArticleRepository articleRepository;

    public Market create(Article article, Integer price, String tag, MarketArticleState state) {
        Market market = Market.builder()
                .article(article)
                .price(price)
                .tag(tag)
                .state(state)
                .build();

        return marketRepository.save(market);
    }

    public Page<Market> getAllMarketsByPageable(Pageable pageable) {
        return marketRepository.findAll(pageable);
    }

    public Market getMarket(Integer marketId) {
        return marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));
    }

    @Transactional
    public Market updatePrice(Integer marketId, int price) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.updatePrice(price);
        return market;
    }

    @Transactional
    public Market updateTag(Integer marketId, String newTag) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.updateTag(newTag);
        return market;
    }

    @Transactional
    public Market updateState(Integer marketId, MarketArticleState newState) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        market.updateState(newState);
        return market;
    }

    public void delete(Integer marketId) {
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE, "marketId: " + marketId));

        Article article = market.getArticle();
        articleRepository.delete(article);
        marketRepository.delete(market);
    }
}
