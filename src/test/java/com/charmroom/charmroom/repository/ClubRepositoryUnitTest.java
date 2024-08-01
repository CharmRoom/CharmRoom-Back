package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Club Unit Test")
public class ClubRepositoryUnitTest {
    @Autowired
    ClubRepository clubRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    UserRepository userRepository;

    private Club club;
    private Image image;

    private Image createTestImage() {
        return Image.builder()
                .path("/example/example")
                .build();
    }

    private User createTestUser(String username) {
        return User.builder().username(username).email(username + "@test.com").nickname(username).password("").withdraw(false).build();
    }


    private Club createTestClub() {
        imageRepository.save(createTestImage());
        userRepository.save(createTestUser("1"));
        userRepository.save(createTestUser("2"));

        return Club.builder()
                .name("Club Name")
                .description("Club Description")
                .contact("")
                .image(image)
                .userList(List.of(createTestUser("1"), createTestUser("2")))
                .build();
    }

    @BeforeEach
    void setUp() {
        club = createTestClub();
    }

    @Nested
    @DisplayName("Crete Club")
    class CreateTestClub {
        @Test
        void success() {
            // given
            // when
            Club saved = clubRepository.save(club);

            // then
            assertThat(saved).isNotNull();
            assertThat(saved).isEqualTo(club);
        }
    }

    @Nested
    @DisplayName("Read Club")
    class ReadTestClub {
        @Test
        void success() {
            // given
            Club saved = clubRepository.save(club);
            // when
            Club found = clubRepository.findById(saved.getId()).get();
            // then
            assertThat(found).isNotNull();
            assertThat(found).isEqualTo(saved);
        }

        @Test
        void failReadTestClubWithWongId() {
            // given
            clubRepository.save(club);
            Integer wrongId = 999;
            // when
            Optional<Club> found = clubRepository.findById(wrongId);
            // then
            assertThat(found).isNotNull();
            assertThat(found).isNotPresent();
        }
    }

    @Nested
    @DisplayName("Update Club")
    class UpdateTestClub {
        @Test
        void success() {
            // given
            Club saved = clubRepository.save(club);
            Image testImage = Image.builder()
                    .path("/test/example")
                    .build();
            imageRepository.save(testImage);

            // when
            saved.updateName("test name");
            saved.updateDescription("test description");
            saved.updateContact("test contact");
            saved.updateImage(testImage);
            Club found = clubRepository.findById(saved.getId()).get();

            // then
            assertThat(found).isNotNull();
            assertThat(found.getName()).isEqualTo("test name");
            assertThat(found.getDescription()).isEqualTo("test description");
            assertThat(found.getContact()).isEqualTo("test contact");
            assertThat(found.getImage()).isEqualTo(testImage);
        }
    }

    @Nested
    @DisplayName("Delete Club")
    class DeleteTestClub {
        @Test
        void success() {
            // given
            Club saved = clubRepository.save(club);

            // when
            clubRepository.delete(saved);
            List<Club> found = clubRepository.findAll();

            // then
            assertThat(found).isNotNull();
            assertThat(found).isEmpty();
        }
    }
}
