package com.charmroom.charmroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.TestPropertySource;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("User Repository 단위 테스트")
public class UserRepositoryUnitTest {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClubRepository clubRepository;
	@Autowired
	private ImageRepository imageRepository;
	
	private User user;
	
	private Image buildImage() {
		return Image.builder()
				.path("/example/example")
				.build();
	}
	
	private Club buildClub() {
		Image clubImage = buildImage();
		imageRepository.save(clubImage);
		
		return Club.builder()
				.name("")
				.contact("")
				.description("")
				.image(clubImage)
				.build();
	}
	
	private User buildUser(String username) {
		Image image = buildImage();
		imageRepository.save(image);
		
		Club club = buildClub();
		clubRepository.save(club);
		
		return User.builder()
				.username(username)
				.password(username)
				.email(username + "@test.com")
				.nickname(username)
				.image(image)
				.club(club)
				.build();
	}
	
	@BeforeEach
	void setup() {
		user = buildUser("1");
	}
	
	@Nested
	@DisplayName("CREATE")
	class CreateTest {
		@Test
		public void success() {
			// given
			// when
			User savedUser = userRepository.save(user);
			
			// then
			assertThat(savedUser).isNotNull();
			assertThat(savedUser.getId()).isEqualTo(user.getId());
		}
	}
	
	@Nested
	@DisplayName("READ")
	class ReadTest {
		@Test
		public void success() {
			// given
			User savedUser = userRepository.save(user);
			
			// when
			var result = userRepository.findById(user.getId());
			
			// then
			assertThat(result).isPresent();
			assertThat(result).get().isNotNull();
			assertThat(result).get().isEqualTo(savedUser);
		}
		
		@Test
		public void fail() {
			// given
			userRepository.save(user);
			
			// when
			var result = userRepository.findById(12345);
			
			// then
			assertThat(result).isNotPresent();
		}
	}
	
	@Nested
	@DisplayName("UPDATE")
	class UpdateTest {
		@Test
		public void success() {
			// given
			User savedUser = userRepository.save(user);
			
			// when
			String newNickname = "new_nickname";
			String newPassword = "new_password";
			Boolean newWithdraw = true;
			UserLevel newLevel = UserLevel.ROLE_ADMIN;
			Image newImage = buildImage();
			Club newClub = buildClub();
			imageRepository.save(newImage);
			clubRepository.save(newClub);
			
			savedUser.updateNickname(newNickname);
			savedUser.updatePassword(newPassword);
			savedUser.updateWithdraw(newWithdraw);
			savedUser.updateLevel(newLevel);
			savedUser.updateImage(newImage);
			savedUser.updateClub(newClub);
			
			// then
			User foundUser = userRepository.findById(savedUser.getId()).get();
			assertThat(foundUser).isNotNull();
			assertThat(foundUser.getNickname()).isEqualTo(newNickname);
			assertThat(foundUser.getPassword()).isEqualTo(newPassword);
			assertThat(foundUser.getWithdraw()).isEqualTo(newWithdraw);
			assertThat(foundUser.getLevel()).isEqualTo(newLevel);
			assertThat(foundUser.getImage()).isEqualTo(newImage);
			assertThat(foundUser.getClub()).isEqualTo(newClub);
		}
	}
	
	@Nested
	@DisplayName("DELETE")
	class DeleteTest {
		@Test
		public void success() {
			userRepository.save(user);
			
			// when
			userRepository.delete(user);
			
			// then
			var userList = userRepository.findAll();
			assertThat(userList).isNotNull();
			assertThat(userList).isEmpty();
		}
	}
	
	@Nested
	class PageTest{
		@Test
		public void success() {
			// given
			User u1 = userRepository.save(buildUser("1"));
			User u2 = userRepository.save(buildUser("2"));
			User u3 = userRepository.save(buildUser("3"));
			User u4 = userRepository.save(buildUser("4"));
			User u5 = userRepository.save(buildUser("5"));
			
			var pageRequest1 = PageRequest.of(0, 3, Direction.ASC, "username");
			var pageRequest2 = PageRequest.of(1, 3, Direction.ASC, "username");
			
			// when
			var result1 = userRepository.findAll(pageRequest1);
			var result2 = userRepository.findAll(pageRequest2);
			
			assertThat(result1).hasSize(3);
			assertThat(result1).hasSameElementsAs(List.of(u1, u2, u3));
			assertThat(result2).hasSize(2);
			assertThat(result2).hasSameElementsAs(List.of(u4, u5));
		}
	}
}
