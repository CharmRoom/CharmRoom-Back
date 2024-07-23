package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;

import com.charmroom.charmroom.dto.business.ClubDto;
import com.charmroom.charmroom.dto.business.ClubMapper;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

@ExtendWith(MockitoExtension.class)
public class ClubServiceUnitTest {
    @Mock
    ClubRepository clubRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    private CharmroomUtil.Upload uploadUtil;
    @InjectMocks
    ClubService clubService;

    private Club club;
    private ClubDto clubDto;
    private String clubName;
    private String description;
    private String contact;
    private int clubId;
    private User owner;

    Club createClub(String prefix) {
        return Club.builder()
                .id(clubId)
                .name(prefix + clubName)
                .description(description)
                .owner(owner)
                .contact(contact)
                .build();
    }

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .username("owner")
                .email("")
                .level(UserLevel.ROLE_BASIC)
                .build();

        clubId = 1;
        clubName = "club name";
        description = "club description";
        contact = "club contact";
        club = createClub("");
        clubDto = ClubMapper.toDto(club);
    }

    @Nested
    @DisplayName("Create Club")
    class CreateClub {
        @Test
        void success_whenImageFileExists() {
            // given
            doReturn(Optional.of(owner)).when(userRepository).findByUsername(owner.getUsername());
            doReturn(club).when(clubRepository).save(any(Club.class));
            doReturn(false).when(clubRepository).existsByName(clubName);

            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

            Image image = Image.builder()
                    .path("")
                    .originalName("")
                    .build();

            doReturn(image).when(uploadUtil).buildImage(imageFile);

            // when
            ClubDto created = clubService.createClub(owner.getUsername(), clubDto, imageFile);

            // then
            verify(clubRepository).save(any(Club.class));
            assertThat(created).isNotNull();
        }

        @Test
        void success_whenImageFileNotExists() {
            // given
            doReturn(Optional.of(owner)).when(userRepository).findByUsername(owner.getUsername());
            doReturn(club).when(clubRepository).save(any(Club.class));
            doReturn(false).when(clubRepository).existsByName(clubName);

            // when
            ClubDto created = clubService.createClub(owner.getUsername(), clubDto);

            // then
            verify(clubRepository).save(any(Club.class));
            assertThat(created).isNotNull();
        }

        @Test
        void fail_ClubNameDuplicated() {
            // given
            doReturn(Optional.of(owner)).when(userRepository).findByUsername(owner.getUsername());
            doReturn(true).when(clubRepository).existsByName(clubName);

            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    clubService.createClub(owner.getUsername(), clubDto, imageFile));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.DUPLICATED_CLUBNAME);
        }
    }

    @Nested
    @DisplayName("Get Club List")
    class getClubList {
        @Test
        void success() {
            // given
            List<Club> clubList = List.of(createClub("1"), createClub("2"), createClub("3"));
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.ASC, "clubName");
            PageImpl<Club> clubPage = new PageImpl<>(clubList);

            doReturn(clubPage).when(clubRepository).findAll(pageRequest);

            // when
            Page<ClubDto> allClubsByPageable = clubService.getAllClubsByPageable(pageRequest);

            // then
            assertThat(allClubsByPageable).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Get Club")
    class getClub {
        @Test
        void success() {
            // given
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());

            // when
            ClubDto found = clubService.getClub(club.getId());

            // then
            assertThat(found).isNotNull();
        }

        @Test
        void fail_ClubNotFound() {
            // given
            doReturn(Optional.empty()).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> clubService.getClub(club.getId()));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUB);
            assertThat(thrown.getMessage()).isEqualTo("clubId: " + club.getId());
        }
    }

    @Nested
    @DisplayName("Update Club Name")
    class updateClubName {
        @Test
        void success() {
            // given
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());

            // when
            ClubDto updated = clubService.updateClubName(club.getId(), "new club name");

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getName()).isEqualTo("new club name");
        }

        @Test
        void fail_ClubNotFound() {
            // given
            doReturn(Optional.empty()).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> clubService.updateClubName(club.getId(), "new club name"));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUB);
            assertThat(thrown.getMessage()).isEqualTo("clubId: " + club.getId());
        }
    }

    @Nested
    @DisplayName("Update Description")
    class updateDescription {
        @Test
        void success() {
            // given
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());

            // when
            ClubDto updated = clubService.updateDescription(club.getId(), "new club description");

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getDescription()).isEqualTo("new club description");
        }

        @Test
        void fail_ClubNotFound() {
            // given
            doReturn(Optional.empty()).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> clubService.updateDescription(club.getId(), "new club description"));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUB);
            assertThat(thrown.getMessage()).isEqualTo("clubId: " + club.getId());
        }
    }

    @Nested
    @DisplayName("Update Contact")
    class updateContact {
        @Test
        void success() {
            // given
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());

            // when
            ClubDto updated = clubService.updateContact(club.getId(), "new club contact");

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getContact()).isEqualTo("new club contact");
        }

        @Test
        void fail_ClubNotFound() {
            // given
            doReturn(Optional.empty()).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> clubService.updateContact(club.getId(), "new club contact"));

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUB);
            assertThat(thrown.getMessage()).isEqualTo("clubId: " + club.getId());
        }
    }

    @Nested
    @DisplayName("Change Owner")
    class ChangeOwner {
        @Test
        void success() {
            // given
            User build = User.builder()
                    .username("updateName")
                    .email("")
                    .level(UserLevel.ROLE_BASIC)
                    .build();

            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doReturn(Optional.of(build)).when(userRepository).findByUsername(build.getUsername());

            // when
            ClubDto dto = clubService.changeOwner(club.getId(), build.getUsername(), owner.getUsername());

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.getOwner().getUsername()).isEqualTo(build.getUsername());
        }
    }

    @Nested
    @DisplayName("Delete Club")
    class deleteClub {
        @Test
        void success() {
            // given
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());

            // when
            clubService.deleteClub(club.getId(), owner.getUsername());

            // then
            verify(clubRepository).delete(any(Club.class));
        }

        @Test
        void fail_ClubNotFound() {
            // given
            doReturn(Optional.empty()).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> {
                clubService.deleteClub(club.getId(), owner.getUsername());
            });

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUB);
            assertThat(thrown.getMessage()).isEqualTo("clubId: " + club.getId());
        }
    }

    @Nested
    @DisplayName("Set Image")
    class setImage {
        @Test
        void success() {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
            Image image = Image.builder()
                    .path("")
                    .originalName("")
                    .build();

            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doReturn(image).when(uploadUtil).buildImage(imageFile);
            doReturn(image).when(imageRepository).save(image);

            // when
            ClubDto updated = clubService.setImage(club.getId(), imageFile, owner.getUsername());

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getImage().getPath()).isEqualTo(image.getPath());
        }

        @Test
        void fail_ClubNotFound() {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
            doReturn(Optional.empty()).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> {
                clubService.setImage(club.getId(), imageFile, owner.getUsername());
            });

            // then
            assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUB);
        }

        @Test
        void whenClubImageAlreadyExists() {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

            Image image = Image.builder()
                    .path("")
                    .originalName("")
                    .build();

            Club club = Club.builder()
                    .image(image)
                    .owner(owner)
                    .build();

            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());
            doNothing().when(uploadUtil).deleteFile(club.getImage());
            doReturn(image).when(uploadUtil).buildImage(imageFile);
            doReturn(image).when(imageRepository).save(image);

            // when
            ClubDto updated = clubService.setImage(club.getId(), imageFile, owner.getUsername());

            // then
            verify(uploadUtil).deleteFile(club.getImage());
            assertThat(updated.getImage().getPath()).isEqualTo(image.getPath());
        }
    }
}


