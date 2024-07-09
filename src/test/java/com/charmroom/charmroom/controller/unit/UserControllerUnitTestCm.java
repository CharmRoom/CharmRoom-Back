package com.charmroom.charmroom.controller.unit;

import com.charmroom.charmroom.controller.api.UserController;
import com.charmroom.charmroom.dto.business.SubscribeDto;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.SubscribeService;
import com.charmroom.charmroom.service.UserService;
import com.google.gson.Gson;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTestCm {
    @Mock
    UserService userService;
    @Mock
    SubscribeService subscribeService;

    @InjectMocks
    UserController userController;

    MockMvc mockMvc;
    User mockedSubscriber;
    UserDto mockedDto;

    Gson gson;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mock(Validator.class))
                .build();

        mockedSubscriber = User.builder()
                .username("test")
                .password("password")
                .email("test@test.com")
                .nickname("nickname")
                .build();

        mockedDto = UserMapper.toDto(mockedSubscriber);
        gson = new Gson();
    }

    @Nested
    class GetCSubscribe {
        @Test
        void success() throws Exception {
            // given
            UserDto target1 = UserDto.builder()
                    .id(1)
                    .username("")
                    .level(UserLevel.ROLE_BASIC)
                    .build();
            UserDto target2 = UserDto.builder()
                    .id(1)
                    .username("")
                    .level(UserLevel.ROLE_BASIC)
                    .build();
            UserDto target3 = UserDto.builder()
                    .id(1)
                    .username("")
                    .level(UserLevel.ROLE_BASIC)
                    .build();

            SubscribeDto subs1 = SubscribeDto.builder()
                    .id(1)
                    .subscriber(mockedDto)
                    .target(target1)
                    .build();

            SubscribeDto subs2 = SubscribeDto.builder()
                    .id(2)
                    .subscriber(mockedDto)
                    .target(target2)
                    .build();

            SubscribeDto subs3 = SubscribeDto.builder()
                    .id(3)
                    .subscriber(mockedDto)
                    .target(target3)
                    .build();

            List<SubscribeDto> dtoList = List.of(subs1, subs2, subs3);
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
            PageImpl<SubscribeDto> dtoPage = new PageImpl<>(dtoList, pageRequest, 3);

            doReturn(dtoPage).when(subscribeService).getSubscribesBySubscriber(any(), eq(pageRequest));

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/user/subscribe"));

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.totalElements").value(3),
                    jsonPath("$.data.content").isArray()
            );
        }
    }
}
