package com.charmroom.charmroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.entity.Point;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.PointType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.PointRepository;
import com.charmroom.charmroom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {
	private final PointRepository pointRepository;
	private final UserRepository userRepository;
	public Point create(String username, PointType type, int diff) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Point point = Point.builder()
				.user(user)
				.type(type)
				.diff(diff)
				.build();
		return pointRepository.save(point);
	}
	
	public Page<Point> pointsByUsername(String username, Pageable pageable){
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		return pointRepository.findAllByUser(user, pageable);
	}
	
	public void delete(Integer id) {
		Point point = pointRepository.findById(id)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_POINT, "id: " + id));
		pointRepository.delete(point);
	}
}
