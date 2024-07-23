package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.ClubRegisterDto;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.ClubRegister;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRegisterRepository;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ClubRegisterServiceUnitTest {
    @Mock
    ClubRepository clubRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ClubRegisterRepository clubRegisterRepository;
    @InjectMocks
    ClubRegisterService clubRegisterService;

    private User user;
    private User owner;
    private Club club;
    private ClubRegister clubRegister;

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
                .id(1)
                .name(clubname)
                .contact("")
                .description("")
                .owner(owner)
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
        owner = createTestUser("owner");
        user = createTestUser("test");
        club = createTestClub("test");
        clubRegister = createClubRegister(club, user);
    }

    @Nested
    class Register {
        @Test
        void success() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doReturn(clubRegister).when(clubRegisterRepository).save(any(ClubRegister.class));

            // when
            ClubRegisterDto created = clubRegisterService.register(user.getUsername(), club.getId());

            // then
            assertThat(created.getClub().getName()).isEqualTo(clubRegister.getClub().getName());
            assertThat(created.getUser().getUsername()).isEqualTo(clubRegister.getUser().getUsername());
        }
    }

    @Nested
    class GetRegisters {
        @Test
        void success() {
            // given
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                users.add(createTestUser("test" + i));
            }

            List<ClubRegister> registerList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                registerList.add(ClubRegister.builder()
                        .club(club)
                        .user(users.get(0))
                        .build());
            }

            PageRequest pageRequest = PageRequest.of(0, 3);
            PageImpl<ClubRegister> registerPage = new PageImpl<>(registerList);

            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doReturn(registerPage).when(clubRegisterRepository).findAllByClub(club, pageRequest);

            // when
            Page<ClubRegisterDto> clubRegisters = clubRegisterService.getClubRegistersByClub(club.getId(), pageRequest, owner.getUsername());

            // then
            assertThat(clubRegisters).hasSize(3);
        }
    }

    @Nested
    class Delete {
        @Test
        void success() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doReturn(Optional.of(clubRegister)).when(clubRegisterRepository).findByUserAndClub(user, club);
            // when
            clubRegisterService.deleteClubRegister(user.getUsername(), club.getId(), owner.getUsername());
            // then
            verify(clubRegisterRepository).findByUserAndClub(user, club);
            verify(clubRegisterRepository).delete(clubRegister);
        }
    }

    @Nested
    class Approve {
        @Test
        void success() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doReturn(Optional.of(clubRegister)).when(clubRegisterRepository).findByUserAndClub(user, club);

            // when
            clubRegisterService.approveClubRegister(owner.getUsername(), user.getUsername(), club.getId());

            // then
            verify(clubRegisterRepository).delete(clubRegister);
        }

        @Test
        void failUnauthorizedClub() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doReturn(Optional.of(clubRegister)).when(clubRegisterRepository).findByUserAndClub(user, club);

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    clubRegisterService.approveClubRegister("12345", user.getUsername(), club.getId()));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.UNAUTHORIZED_CLUB);
        }

        @Test
        void failNotFoundClubRegister() {
            // given
            doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(
                    BusinessLogicException.class, () ->
                            clubRegisterService.approveClubRegister(owner.getUsername(), user.getUsername(), club.getId()
                            )
            );

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUBREGISTER);
        }
    }
}
