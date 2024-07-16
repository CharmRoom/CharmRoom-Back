package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.WishDto;
import com.charmroom.charmroom.dto.business.WishMapper;
import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.Wish;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.MarketRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;

    public WishDto wishOrCancel(String username, Integer marketId) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER));
        Market market = marketRepository.findById(marketId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_ARTICLE));

        Optional<Wish> found = wishRepository.findByUserAndMarket(user, market);

        if (found.isPresent()) {
            Wish wish = found.get();
            wishRepository.delete(wish);
            return null; // 찜하기 취소
        } else {
            Wish wish = Wish.builder()
                    .user(user)
                    .market(market)
                    .build();
            Wish saved = wishRepository.save(wish);
            return WishMapper.doDto(saved);
        }
    }

    public Page<WishDto> getWishesByUserName(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));

        Page<Wish> wishes = wishRepository.findAllByUser(user, pageable);
        return wishes.map(WishMapper::doDto);
    }
}
