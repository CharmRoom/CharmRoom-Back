package com.charmroom.charmroom.controller.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.charmroom.charmroom.controller.api.AdminController;
import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.exception.ExceptionHandlerAdvice;
import com.charmroom.charmroom.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AdminControllerUnitTestDy {
	@Mock
	UserService userService;
	@InjectMocks
	AdminController adminController;
	
	MockMvc mockMvc;
	User mockedUser;
	UserDto mockedDto;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(adminController)
				.setControllerAdvice(new ExceptionHandlerAdvice())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setValidator(mock(Validator.class))
				.build();
		mockedUser = User.builder()
				.username("test")
				.password("password")
				.email("test@test.com")
				.nickname("nickname")
				.build();
		mockedDto = UserMapper.toDto(mockedUser);
	}
	
	@Nested
	class Users{
		@Test
		void success() throws Exception {
			// given
			var dtoList = List.of(mockedDto, mockedDto, mockedDto);
			var pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
			var dtoPage = new PageImpl<>(dtoList, pageRequest, 3);
			
			doReturn(dtoPage).when(userService).getAllUsersByPageable(pageRequest);
			
			// when
			mockMvc.perform(get("/api/admin/user"))
			
			// then
			.andExpectAll(status().isOk(),
					jsonPath("$.data.totalElements").value(3),
					jsonPath("$.data.content").isArray(),
					jsonPath("$.data.content[0].username").value(mockedDto.getUsername()),
					jsonPath("$.data.content[0].password").doesNotExist()
					)
			;
			
		}
	}
	
	@Nested
	class ChangeUserGrade{
		@Test
		void success() throws Exception{
			// given
			mockedDto.setLevel(UserLevel.ROLE_ADMIN);
			doReturn(mockedDto).when(userService).changeLevel(mockedDto.getUsername(), UserLevel.ROLE_ADMIN.getValue());
			
			// when
			mockMvc.perform(patch("/api/admin/user/grade")
					.param("username", mockedDto.getUsername())
					.param("grade", UserLevel.ROLE_ADMIN.getValue())
					)
			.andExpectAll(status().isOk(),
					jsonPath("$.data.level").value(UserLevel.ROLE_ADMIN.getValue()));
			;
		}
		@Test
		void fail() throws Exception{
			// given
			doThrow(new BusinessLogicException(BusinessLogicError.NOTFOUND_USER))
			.when(userService).changeLevel(any(), any());
			
			// when
			mockMvc.perform(patch("/api/admin/user/grade")
					.param("username", mockedDto.getUsername())
					.param("grade", UserLevel.ROLE_ADMIN.getValue())
					)
			.andExpect(status().isNotFound())
			;
		}
	}
	
	@Nested
	class ChangeUserWithdraw{
		@Test
		void success() throws Exception{
			// given
			mockedDto.setWithdraw(true);
			doReturn(mockedDto).when(userService).changeWithdraw(mockedDto.getUsername(), true);
			
			// when
			mockMvc.perform(patch("/api/admin/user/withdraw")
					.param("username", mockedDto.getUsername()))
			.andExpect(status().isOk());
		}
	}
	
}
