package com.charmroom.charmroom.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.embid.SubscribeId;

public interface SubscribeRepository extends JpaRepository<Subscribe, SubscribeId> {
    Optional<Subscribe> findBySubscriberAndTarget(User subscriber, User target);
    Page<Subscribe> findAllBySubscriber(User subscriber, Pageable pageable);
    
    @Query(
    		value = "select a from Article a, Subscribe s "
    				+ "where s.subscriber = :subscriber and a.user = s.target",
    		countQuery = "select count(a) from Article a, Subscribe s "
    				+ "where s.subscriber = :subscriber and a.user = s.target"
    				)
    Page<Article> findArticlesBySubscriber(@Param("subscriber") User subscriber, Pageable pageable);
}
