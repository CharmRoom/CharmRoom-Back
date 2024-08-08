package com.charmroom.charmroom.repository;

import org.springframework.data.repository.CrudRepository;

import com.charmroom.charmroom.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String>{

}
