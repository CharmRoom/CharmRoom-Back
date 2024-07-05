package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.charmroom.charmroom.entity.Point;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.PointType;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.PointRepository;
import com.charmroom.charmroom.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class PointServiceUnitTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private PointRepository pointRepository;
	@InjectMocks
	private PointService pointService;
	
	Point point;
	User user;
	
	Point buildPoint(Integer id) {
		return Point.builder()
				.id(id)
				.user(user)
				.type(PointType.EARN)
				.diff(10)
				.build();
	}
	
	User buildUser() {
		return User.builder()
				.username("username")
				.build();
	}
	
	@BeforeEach
	void setup() {
		user = buildUser();
		point = buildPoint(1);
	}
	
	@Nested
	class Create{
		@Test
		void success() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(point).when(pointRepository).save(any(Point.class));
			// when
			var saved = pointService.create(user.getUsername(), point.getType().toString(), point.getDiff());
			// then
			assertThat(saved).isNotNull();
		}
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(user.getUsername());
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				pointService.create(user.getUsername(), point.getType().toString(), point.getDiff());
			});
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + user.getUsername());
		}
	}
	
	@Nested
	class PointsByUsername {
		@Test
		void success() {
			// given
			var listPoint = List.of(buildPoint(2), buildPoint(3));
			var pageRequest = PageRequest.of(0, 2);
			var pagePoint = new PageImpl<>(listPoint);
			
			doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
			doReturn(pagePoint).when(pointRepository).findAllByUser(user, pageRequest);
			
			// when
			var result = pointService.pointsByUsername(user.getUsername(), pageRequest);
			
			// then
			assertThat(result).hasSize(2);
		}
		
	}
	
	@Nested
	class Delete{
		@Test
		void success() {
			// given
			doReturn(Optional.of(point)).when(pointRepository).findById(point.getId());
			
			// when
			pointService.delete(point.getId());
			
			// then
			verify(pointRepository).delete(point);
		}
		
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(pointRepository).findById(point.getId());
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				pointService.delete(point.getId());
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_POINT);
			assertThat(thrown.getMessage()).isEqualTo("id: " + point.getId());
		}
	}
}
