package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Market;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.Wish;
import com.charmroom.charmroom.entity.embid.WishId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, WishId> {
    Optional<Wish> findByUserAndMarket(User user, Market market);
}
