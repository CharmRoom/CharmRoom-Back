package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.ClubRegister;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.embid.ClubRegisterId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRegisterRepository extends JpaRepository<ClubRegister, ClubRegisterId> {
    Optional<ClubRegister> findByUserAndClub(User user, Club club);
}
