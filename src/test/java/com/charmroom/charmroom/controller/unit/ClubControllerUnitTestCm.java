package com.charmroom.charmroom.controller.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.ClubController;
import com.charmroom.charmroom.dto.business.ClubDto;
import com.charmroom.charmroom.dto.business.ClubRegisterDto;
import com.charmroom.charmroom.dto.business.ImageDto;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubUpdateRequestDto;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.ClubRegisterService;
import com.charmroom.charmroom.service.ClubService;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class ClubControllerUnitTestCm {
    @Mock
    ClubService clubService;
    @Mock
    ClubRegisterService clubRegisterService;

    @InjectMocks
    ClubController clubController;

    MockMvc mockMvc;
    ClubDto mockedClubDto;
    ImageDto mockedImageDto;
    ClubRegisterDto registerDto;
    UserDto mockedUserDto;
    UserDto owner;
    Gson gson;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(clubController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mock(Validator.class))
                .build();

        mockedImageDto = ImageDto.builder()
                .id(1)
                .path("")
                .originalName("")
                .build();

        owner = UserDto.builder()
                .username("owner")
                .level(UserLevel.ROLE_BASIC)
                .build();

        mockedClubDto = ClubDto.builder()
                .id(1)
                .name("clubname")
                .description("")
                .contact("")
                .image(mockedImageDto)
                .owner(owner)
                .build();

        mockedUserDto = UserDto.builder()
                .username("test")
                .password("password")
                .email("test@test.com")
                .nickname("nickname")
                .level(UserLevel.ROLE_BASIC)
                .build();

        registerDto = ClubRegisterDto.builder()
                .club(mockedClubDto)
                .user(mockedUserDto)
                .build();

        gson = new Gson();
    }

    @Nested
    class Create {
        @Test
        void successWithoutImage() throws Exception {
            // given
            ClubCreateRequestDto request = ClubCreateRequestDto.builder()
                    .name("")
                    .description("")
                    .contact("")
                    .build();
            doReturn(mockedClubDto).when(clubService).createClub(any(), any(ClubDto.class));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/club/")
                    .content(gson.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.data.name").value(mockedClubDto.getName())
            );
        }

        @Test
        void successWithImage() throws Exception {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

            doReturn(mockedClubDto).when(clubService).createClub(any(), any(ClubDto.class), eq(imageFile));

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/club/")
                    .file(imageFile)
                    .param("name", mockedClubDto.getName())
                    .param("description", mockedClubDto.getDescription())
                    .param("contact", mockedClubDto.getContact())
            );

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.code").value("CREATED"),
                    jsonPath("$.data.name").value(mockedClubDto.getName())
            );
        }

        @Test
        void failDuplicatedClubName() throws Exception {
            // given
            doThrow(new BusinessLogicException(BusinessLogicError.DUPLICATED_CLUBNAME))
                    .when(clubService)
                    .createClub(any(), any(ClubDto.class));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/club/")
                    .param("name", mockedClubDto.getName())
                    .param("description", mockedClubDto.getDescription())
                    .param("contact", mockedClubDto.getContact())
            );

            // then
            resultActions.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.code").value("05000")
            );
        }
    }

    @Nested
    class GetClub {
        @Test
        void success() throws Exception {
            // given
            doReturn(mockedClubDto).when(clubService).getClub(mockedClubDto.getId());

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/club/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
        }

        @Test
        void fail() throws Exception {
            // given
            doThrow(new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB))
                    .when(clubService)
                    .getClub(mockedClubDto.getId());

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/club/1"));

            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("05100")
            );
        }
    }

    @Nested
    class GetClubList {
        @Test
        void success() throws Exception {
            // given
            List<ClubDto> dtoList = List.of(mockedClubDto, mockedClubDto, mockedClubDto);
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
            PageImpl<ClubDto> dtoPage = new PageImpl<>(dtoList, pageRequest, 3);

            doReturn(dtoPage).when(clubService).getAllClubsByPageable(pageRequest);

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/club/list"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.totalElements").value(3),
                    jsonPath("$.data.content").isArray(),
                    jsonPath("$.data.content.size()").value(3),
                    jsonPath("$.data.content[0].name").value(mockedClubDto.getName())
            );
        }
    }

    @Nested
    class Update {
        @Test
        void success() throws Exception {
            // given
            ClubUpdateRequestDto request = ClubUpdateRequestDto.builder()
                    .name(mockedClubDto.getName())
                    .description(mockedClubDto.getDescription())
                    .contact(mockedClubDto.getContact())
                    .build();
            doReturn(mockedClubDto).when(clubService).update(eq(1), any(ClubDto.class), any());

            // when
            ResultActions resultActions = mockMvc.perform(patch("/api/club/1")
                    .content(gson.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.name").value(mockedClubDto.getName())
            );
        }
    }

    @Nested
    class SetImage {
        @Test
        void setImage() throws Exception {
            // given
            MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());

            doReturn(mockedClubDto).when(clubService).setImage(eq(1), eq(imageFile), any());

            // when
            ResultActions resultActions = mockMvc.perform(multipart("/api/club/image/1")
                    .file(imageFile)
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
        }
    }


    @Nested
    class ChangeOwner {
        @Test
        void success() throws Exception {
            // given
            doReturn(mockedClubDto).when(clubService).changeOwner(eq(1), any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(patch("/api/club/owner/1")
                    .param("username", owner.getUsername())
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK"),
                    jsonPath("$.data.owner.username").value(owner.getUsername())
            );
        }
    }

    @Nested
    class Delete {
        @Test
        void success() throws Exception {
            // given
            doNothing().when(clubService).deleteClub(eq(mockedClubDto.getId()), any());

            // when
            ResultActions resultActions = mockMvc.perform(delete("/api/club/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
        }
    }

    @Nested
    class Register {
        @Test
        void success() throws Exception {
            // given
            doReturn(registerDto).when(clubRegisterService).register(any(), eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/club/register/1"));

            // then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.code").value("CREATED"),
                    jsonPath("$.data.user.username").value(mockedUserDto.getUsername())
            );
        }

        @Test
        void failNotFoundClub() throws Exception {
            // given
            doThrow(new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB))
                    .when(clubRegisterService)
                    .register(any(), eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/club/register/1"));

            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("05100")
            );
        }
    }

    @Nested
    class getRegistersByClub {
        @Test
        void success() throws Exception {
            // given
            List<UserDto> users = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                users.add(UserDto.builder()
                        .id(i)
                        .username("test" + i)
                        .level(UserLevel.ROLE_BASIC)
                        .build());
            }

            List<ClubRegisterDto> dtoList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                dtoList.add(ClubRegisterDto.builder()
                        .club(mockedClubDto)
                        .user(users.get(i))
                        .build());
            }

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("userId").descending());
            PageImpl<ClubRegisterDto> dtoPage = new PageImpl<>(dtoList, pageRequest, 3);

            doReturn(dtoPage).when(clubRegisterService).getClubRegistersByClub(eq(1), eq(pageRequest), any());

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/club/register/1"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(3)
            );
        }
    }

    @Nested
    class Approve {
        @Test
        void success() throws Exception {
            // given
            doNothing().when(clubRegisterService).approveClubRegister(any(), eq(mockedUserDto.getUsername()), eq(1));

            // when
            ResultActions resultActions = mockMvc.perform(patch("/api/club/register/1")
                    .param("username", mockedUserDto.getUsername())
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("code").value("OK")
            );
        }
    }

    @Nested
    class deleteRegister {
        @Test
        void success() throws Exception {
            // given
            doNothing().when(clubRegisterService).deleteClubRegister(any(), eq(1), any());

            // when
            ResultActions resultActions = mockMvc.perform(delete("/api/club/register/1")
                    .param("username", mockedUserDto.getUsername()));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("OK")
            );
            verify(clubRegisterService).deleteClubRegister(any(), eq(1), any());
        }

        @Test
        void failNotFoundUser() throws Exception {
            // given
            doThrow(new BusinessLogicException(BusinessLogicError.NOTFOUND_USER))
                    .when(clubRegisterService)
                    .deleteClubRegister(any(), eq(1), any());

            // when
            ResultActions resultActions = mockMvc.perform(delete("/api/club/register/1")
                    .param("username", mockedUserDto.getUsername()));

            // then
            resultActions.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("12100")
            );
        }
    }
}
