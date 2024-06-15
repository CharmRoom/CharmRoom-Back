package com.charmroom.charmroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.Point;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.PointType;

@DataJpaTest
@TestPropertySource(properties = { "spring.config.location = classpath:application-test.yml" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Point Repository 단위 테스트")
public class PointRepositoryUnitTest {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClubRepository clubRepository;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private PointRepository pointRepository;

	private User user;
	private Point point;

	private Image buildImage() {
		return Image.builder().path("/example/example").build();
	}

	private Club buildClub() {
		Image clubImage = buildImage();
		imageRepository.save(clubImage);

		return Club.builder().name("").contact("").description("").image(clubImage).build();
	}

	private User buildUser() {
		Image image = buildImage();
		imageRepository.save(image);

		Club club = buildClub();
		clubRepository.save(club);

		return User.builder().id("test").password("test").email("test@test.com").nickname("test").image(image)
				.club(club).build();
	}

	private Point buildPoint(User user) {
		return Point.builder().user(user).type(PointType.EARN).diff(100).build();
	}

	@BeforeEach
	void setup() {
		user = buildUser();
		userRepository.save(user);
		point = buildPoint(user);
	}

	@Nested
	@DisplayName("CREATE")
	class CreateTest {
		@Test
		public void success() {
			// given
			// when
			Point savedPoint = pointRepository.save(point);
			// then
			assertThat(savedPoint).isNotNull();
			assertThat(savedPoint.getId()).isEqualTo(point.getId());
			assertThat(savedPoint.getUpdatedAt()).isEqualTo(savedPoint.getUpdatedAt());
		}
	}

	@Nested
	@DisplayName("READ")
	class ReadTest {
		@Test
		public void readOneSuccess() {
			// given
			Point savedPoint = pointRepository.save(point);
			// when
			var foundPoint = pointRepository.findById(savedPoint.getId());
			// then
			assertThat(foundPoint).isPresent();
			assertThat(foundPoint).get().isNotNull();
			assertThat(foundPoint).get().isEqualTo(savedPoint);
		}
		
		@Test
		public void readManySuccess() {
			// given
			pointRepository.save(point);
			pointRepository.save(buildPoint(user));
			pointRepository.save(buildPoint(user));
			pointRepository.save(buildPoint(user));
			
			var foundPoints = pointRepository.findAll();
			
			assertThat(foundPoints).size().isEqualTo(4);
			assertThat(foundPoints).allSatisfy(point -> {
				assertThat(point.getUser()).isEqualTo(user);
			});
		}

		@Test
		public void fail() {
			// given
			pointRepository.save(point);
			// when
			var foundPoint = pointRepository.findById(12345);
			
			assertThat(foundPoint).isNotPresent();
		}
	}
	
	@Nested
	@DisplayName("DELETE")
	class DeleteTest {
		@Test
		public void success() {
			// given
			Point savedPoint = pointRepository.save(point);
			
			// when
			pointRepository.delete(savedPoint);
			
			// then
			var pointList = pointRepository.findAll();
			assertThat(pointList).isNotNull();
			assertThat(pointList).isEmpty();
		}
	}
}












