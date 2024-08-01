package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubUpdateRequestDto;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.ClubRegister;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.repository.ClubRegisterRepository;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

@WithCharmroomUserDetails
public class ClubControllerIntegrationTestCm extends IntegrationTestBase {
    @Autowired
    ClubRepository clubRepository;
    @Autowired
    ClubRegisterRepository clubRegisterRepository;
    @Autowired
    ImageRepository imageRepository;

    private final String urlPrefix = "/api/club/";
    private final MockMultipartFile file = new MockMultipartFile("image", "profile.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

    Club buildClub(String prefix) {
        Image image = imageRepository.save(Image.builder()
                .originalName(prefix)
                .path("/resource/image/123.png")
                .build());

        return Club.builder()
                .name(prefix)
                .description(prefix + "description")
                .contact(prefix + "contact")
                .owner(charmroomUser)
                .image(image)
                .build();
    }

    @Nested
    class Create {
        @Test
        void success() throws Exception {
            // given
            // when
            ResultActions resultActions = mockMvc.perform(multipart(urlPrefix)
                    .file(file)
                    .param("name", "test")
                    .param("description", "test")
                    .param("contact", "test")
            );

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.name").value("test")
            );
        }
    }

    @Nested
    class GetClub {
        @Test
        void success() throws Exception {
            // given
            Club club = clubRepository.save(buildClub("test"));

            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + club.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.name").value("test")
            );
        }
    }

    @Nested
    class GetClubList {
        @Autowired
        UserRepository userRepository;
        @Autowired
        CharmroomUtil.Upload charmroomUtil;

        @Test
        void success() throws Exception {
            // given
            for (int i = 1; i <= 3; i++) {
                User user = userRepository.save(User.builder()
                        .username("example" + i)
                        .password("example" + i)
                        .email("example" + i)
                        .nickname("example" + i)
                        .level(UserLevel.ROLE_BASIC)
                        .build());

                Image image = imageRepository.save(charmroomUtil.buildImage(file));

                clubRepository.save(Club.builder()
                        .name("example" + i)
                        .owner(user)
                        .image(image)
                        .description("example" + i)
                        .contact("example" + i)
                        .build());
            }

            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + "list"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content[0].name").value("example3")
            );
        }
    }

    @Nested
    class Update {
        @Test
        void success() throws Exception {
            // given
            Club club = clubRepository.save(buildClub("test"));

            ClubUpdateRequestDto dto = ClubUpdateRequestDto.builder()
                    .name("updated")
                    .description("updated")
                    .contact("updated")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(patch(urlPrefix + club.getId())
                    .content(gson.toJson(dto))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.name").value("updated")
            );
        }
    }

    @Nested
    class UpdateImage {
        @Autowired
        CharmroomUtil.Upload charmroomUtil;
        @Autowired
        ImageRepository imageRepository;

        @Test
        void success() throws Exception {
            // given
            Image image = imageRepository.save(charmroomUtil.buildImage(file));

            Club club = clubRepository.save(Club.builder()
                    .name("example")
                    .owner(charmroomUser)
                    .image(image)
                    .description("example")
                    .contact("example")
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(multipart(urlPrefix + "image/" + club.getId())
                    .file(file)
            );

            // then
            resultActions.andExpectAll(
                    status().isOk()
            );
        }
    }

    @Nested
    class Delete {
        @Test
        void success() throws Exception {
            // given
            Club club = clubRepository.save(buildClub("test"));

            // when
            ResultActions resultActions = mockMvc.perform(delete(urlPrefix + club.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk()
            );
        }
    }

    @Nested
    class Register {
        @Test
        void success() throws Exception {
            // given
            Club club = clubRepository.save(buildClub("test"));

            // when
            ResultActions resultActions = mockMvc.perform(post(urlPrefix + "register/" + club.getId()));

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.club.name").value(club.getName())
            );
        }
    }

    @Nested
    class GetRegisters {
        @Autowired
        UserRepository userRepository;

        @Test
        void success() throws Exception {
            // given
            Club club = clubRepository.save(buildClub("test"));

            List<ClubRegister> registerList = new ArrayList<>();
            for (int i = 1; i <= 3 ; i++) {
                User user = userRepository.save(User.builder()
                        .username("test" + i)
                        .password("password" + i)
                        .email("test" + i)
                        .nickname("nickname" + i)
                        .level(UserLevel.ROLE_BASIC)
                        .build());

                registerList.add(clubRegisterRepository.save(ClubRegister.builder()
                        .club(club)
                        .user(user)
                        .build()));
            }

            // when
            ResultActions resultActions = mockMvc.perform(get(urlPrefix + "register/" + club.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(3)
            );
        }
    }

    @Nested
    class Approve {
        @Autowired
        UserRepository userRepository;

        @Test
        void success() throws Exception {
            // given
            Club club = clubRepository.save(buildClub("test"));
            club.updateOwner(charmroomUser);
            User user = userRepository.save(User.builder()
                    .username("example")
                    .password("example")
                    .email("example")
                    .nickname("example")
                    .level(UserLevel.ROLE_BASIC)
                    .build());

            clubRegisterRepository.save(ClubRegister.builder()
                    .club(club)
                    .user(user)
                    .build());

            // when
            ResultActions resultActions = mockMvc.perform(patch(urlPrefix + "register/" + club.getId())
                    .param("username", user.getUsername())
            );

            // then
            resultActions.andExpectAll(
                    status().isOk()
            );
        }
    }

    @Nested
    class DeleteRegister {
        @Test
        void success() throws Exception {
            // given
            Club club = clubRepository.save(buildClub("test"));

            // when
            ResultActions resultActions = mockMvc.perform(delete(urlPrefix + club.getId()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
        }
    }
}
