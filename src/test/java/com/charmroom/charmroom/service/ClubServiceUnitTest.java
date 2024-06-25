package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

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
    private String clubName;
    private String description;
    private String contact;
    private int clubId;

    private Club createClub(String prefix) {
        return Club.builder()
                .id(clubId)
                .name(prefix + clubName)
                .description(description)
                .contact(contact)
                .build();
    }

    @BeforeEach
    void setUp() {
        clubId = 1;
        clubName = "club name";
        description = "club description";
        contact = "club contact";
        club = createClub("");
    }

    @Nested
    @DisplayName("Create Club")
    class CreateClub {
        @Test
        void success_whenImageFileExists() {
            // given
            doReturn(club).when(clubRepository).save(any(Club.class));
            doReturn(false).when(clubRepository).existsByName(clubName);

            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

            Image image = Image.builder()
                    .path("")
                    .originalName("")
                    .build();

            doReturn(image).when(uploadUtil).buildImage(imageFile);

            // when
            Club created = clubService.createClub(clubName, description, contact, imageFile);

            // then
            verify(clubRepository).save(any(Club.class));
            assertThat(created).isNotNull();
        }

        @Test
        void success_whenImageFileNotExists() {
            // given
            doReturn(club).when(clubRepository).save(any(Club.class));
            doReturn(false).when(clubRepository).existsByName(clubName);

            Image image = Image.builder()
                    .path("")
                    .originalName("")
                    .build();

            // when
            Club created = clubService.createClub(clubName, description, contact);

            // then
            verify(clubRepository).save(any(Club.class));
            assertThat(created).isNotNull();
        }

        @Test
        void fail_ClubNameDuplicated() {
            // given
            doReturn(true).when(clubRepository).existsByName(clubName);

            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    clubService.createClub(clubName, description, contact, imageFile));

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
            Page<Club> allClubsByPageable = clubService.getAllClubsByPageable(pageRequest);

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
            Club found = clubService.getClub(club.getId());

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
            Club updated = clubService.updateClubName(club.getId(), "new club name");

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
            Club updated = clubService.updateDescription(club.getId(), "new club description");

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
            Club updated = clubService.updateContact(club.getId(), "new club contact");

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
    @DisplayName("Delete Club")
    class deleteClub {
        @Test
        void success() {
            // given
            doReturn(Optional.of(club)).when(clubRepository).findById(club.getId());

            // when
            clubService.deleteClub(club.getId());

            // then
            verify(clubRepository).delete(any(Club.class));
        }

        @Test
        void fail_ClubNotFound() {
            // given
            doReturn(Optional.empty()).when(clubRepository).findById(club.getId());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> {
                clubService.deleteClub(club.getId());
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

            doReturn(Optional.of(club)).when(clubRepository).findByName(club.getName());
            doReturn(image).when(uploadUtil).buildImage(imageFile);
            doReturn(image).when(imageRepository).save(image);

            // when
            Club updated = clubService.setImage(club.getName(), imageFile);

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getImage()).isEqualTo(image);
        }

        @Test
        void fail_ClubNotFound() {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
            doReturn(Optional.empty()).when(clubRepository).findByName(club.getName());

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> {
                clubService.setImage(club.getName(), imageFile);
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
                    .build();

            doReturn(Optional.of(club)).when(clubRepository).findByName(club.getName());
            doNothing().when(uploadUtil).deleteImageFile(club.getImage());
            doReturn(image).when(uploadUtil).buildImage(imageFile);
            doReturn(image).when(imageRepository).save(image);

            // when
            Club updated = clubService.setImage(club.getName(), imageFile);

            // then
            verify(uploadUtil).deleteImageFile(club.getImage());
            assertThat(updated.getImage()).isEqualTo(image);
        }
    }
}


