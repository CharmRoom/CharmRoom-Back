package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.ClubRegister;
import com.charmroom.charmroom.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClubRegisterRepositoryUnitTest {
    @Autowired
    ClubRegisterRepository clubRegisterRepository;
    @Autowired
    ClubRepository clubRepository;
    @Autowired
    private UserRepository userRepository;

    private ClubRegister clubRegister;
    private User user;
    private Club club;

    User createTestUser(String username) {
        return User.builder()
                .username(username)
                .email(username + "@test.com")
                .nickname(username)
                .password("")
                .withdraw(false)
                .build();
    }

    Club createTestClub(String clubname) {
        return Club.builder()
                .name(clubname)
                .contact("")
                .description("")
                .build();
    }

    ClubRegister createClubRegister(Club club, User user) {
        return ClubRegister.builder()
                .club(club)
                .user(user)
                .build();
    }

    @BeforeEach
    void setup() {
        user = userRepository.save(createTestUser("test"));
        club = clubRepository.save(createTestClub("test"));
        clubRegister = createClubRegister(club, user);
    }

    @Nested
    class CreateClubRegister {
        @Test
        void success() {
            // given
            // when
            ClubRegister register = clubRegisterRepository.save(clubRegister);
            // then
            assertThat(register).isNotNull();
            assertThat(register.getId()).isEqualTo(clubRegister.getId());
            assertThat(register.getClub()).isEqualTo(club);
            assertThat(register.getUser()).isEqualTo(user);
        }
    }

    @Nested
    class Read {
        @Test
        void success() {
            // given
            ClubRegister saved = clubRegisterRepository.save(clubRegister);
            // when
            Optional<ClubRegister> found = clubRegisterRepository.findByUserAndClub(user, club);
            // then
            assertThat(found).isPresent();
            assertThat(found).get().isEqualTo(saved);
        }

        @Test
        void fail() {
            // given
            clubRegisterRepository.save(clubRegister);

            Club build = Club.builder()
                    .name("")
                    .description("")
                    .contact("")
                    .build();
            clubRepository.save(build);
            // when
            Optional<ClubRegister> found = clubRegisterRepository.findByUserAndClub(user, build);

            // then
            assertThat(found).isNotPresent();
        }
    }

    @Nested
    class ReadAll {
        @Test
        void success() {
            // given
            for (int i = 1; i <= 3; i++) {
                User user = userRepository.save(createTestUser(String.valueOf(i)));
                clubRegisterRepository.save(createClubRegister(club, user));
            }
            PageRequest pageRequest = PageRequest.of(0, 3);

            // when
            Page<ClubRegister> all = clubRegisterRepository.findAllByClub(club, pageRequest);

            // then
            assertThat(all).hasSize(3);
            assertThat(all.get().toList().get(0).getUser().getUsername()).isEqualTo("1");
        }
    }

    @Nested
    class Delete {
        @Test
        void success() {
            // given
            ClubRegister saved = clubRegisterRepository.save(clubRegister);
            // when
            clubRegisterRepository.delete(clubRegister);
            // then
            List<ClubRegister> all = clubRegisterRepository.findAll();
            assertThat(all).isEmpty();
        }
    }

}
