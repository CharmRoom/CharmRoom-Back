package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.embid.SubscribeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, SubscribeId> {
    Optional<Subscribe> findBySubscriberAndTarget(User subscriber, User target);

    Page<Subscribe> findAllByUser(User subscriber, Pageable pageable);
}
