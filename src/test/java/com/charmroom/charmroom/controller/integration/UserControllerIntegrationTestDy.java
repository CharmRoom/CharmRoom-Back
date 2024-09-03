package com.charmroom.charmroom.controller.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.charmroom.charmroom.controller.integration.IntegrationTestBase.WithCharmroomUserDetails;
import com.charmroom.charmroom.dto.presentation.UserDto.UserUpdateRequest;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Comment;
import com.charmroom.charmroom.entity.Point;
import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.entity.enums.PointType;
import com.charmroom.charmroom.repository.ArticleRepository;
import com.charmroom.charmroom.repository.BoardRepository;
import com.charmroom.charmroom.repository.CommentRepository;
import com.charmroom.charmroom.repository.PointRepository;
import com.charmroom.charmroom.repository.SubscribeRepository;

public class UserControllerIntegrationTestDy extends IntegrationTestBase {
	
	@Nested
	class GetMyInfo{
		MockHttpServletRequestBuilder request = get("/api/user");
		@Test
		@WithCharmroomUserDetails
		void success() throws Exception {
			mockMvc.perform(request)
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.username").value(charmroomUser.getUsername())
					,jsonPath("$.data.email").value(charmroomUser.getEmail())
					,jsonPath("$.data.nickname").value(charmroomUser.getNickname())
					,jsonPath("$.data.withdraw").value(charmroomUser.isWithdraw())
					,jsonPath("$.data.level").value(charmroomUser.getLevel().toString())
					,jsonPath("$.data.image.path").value(charmroomUser.getImage().getPath())
					);
		}
	}
	
	@Nested
	class UpdateMyInfo{
		MockHttpServletRequestBuilder request = patch("/api/user");
		@Test
		@WithCharmroomUserDetails
		void success() throws Exception {
			// given
			var dto = UserUpdateRequest.builder()
					.nickname("test2")
					.build();
			
			mockMvc.perform(request
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.nickname").value("test2")
					)
			;
		}
		
		@Test
		@WithCharmroomUserDetails
		void failByDuplicatedNickname() throws Exception{
			// given
			var dto = UserUpdateRequest.builder()
					.nickname(charmroomAdmin.getNickname())
					.build();
			mockMvc.perform(request
					.content(gson.toJson(dto))
					.contentType(MediaType.APPLICATION_JSON)
					)
			.andExpectAll(
					status().isNotAcceptable()
					,jsonPath("$.data.nickname").exists()
					)
			;
		}
	}
	@Nested
	class Withdraw{
		MockHttpServletRequestBuilder request = patch("/api/user/withdraw");
		@Test
		@WithCharmroomUserDetails
		void success() throws Exception{
			// given
			
			// when
			mockMvc.perform(request)
			
			// then
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.withdraw").value(true)
					)
			;
		}
	}
	@Nested
	class GetMyPoints{
		MockHttpServletRequestBuilder request = get("/api/user/point");
		@Autowired
		PointRepository pointRepository;
		
		private Point buildPoint() {
			return Point.builder()
					.user(charmroomUser)
					.type(PointType.EARN)
					.diff(300)
					.build();
		}
	
		@Test
		@WithCharmroomUserDetails
		void success() throws Exception{
			// given			
			List<Point> points = new ArrayList<>();
			for(var i = 0; i < 10; i++) 
				points.add(buildPoint());
			for(var p : points) 
				pointRepository.save(p);
			
			// when
			mockMvc.perform(request)
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.totalElements").value(10)
					,jsonPath("$.data.content").isArray()
					,jsonPath("$.data.content.size()").value(10)
					,jsonPath("$.data.content[0].username").value(charmroomUser.getUsername())
					);
		}
	}
	
	@Nested
	class GetMyComments{
		MockHttpServletRequestBuilder request = get("/api/user/comment");
		@Autowired
		BoardRepository boardRepository;
		@Autowired
		ArticleRepository articleRepository;
		@Autowired
		CommentRepository commentRepository;
		
		
		
		@Test
		@WithCharmroomUserDetails
		void success() throws Exception {
			// given
			Board board = boardRepository.save(Board.builder()
					.name("board")
					.type(BoardType.LIST)
					.build());
			Article article = articleRepository.save(Article.builder()
					.user(charmroomUser)
					.board(board)
					.title("title")
					.body("body")
					.build());
			
			List<Comment> parents = new ArrayList<>();
			for(var i = 0; i < 3; i++) 
				parents.add(
						commentRepository.save(Comment.builder()
								.user(charmroomUser)
								.article(article)
								.body(Integer.toString(i))
								.build())
						);
			for(var parent : parents) {
				for (var j = 0; j < 2; j++) {
					var child = commentRepository.save(Comment.builder()
							.user(charmroomUser)
							.article(article)
							.parent(parent)
							.body(Integer.toString(j))
							.build());
					parent.getChildList().add(child);
				}
			}
			
			// when
			mockMvc.perform(request)
			.andExpectAll(
					status().isOk()
					,jsonPath("$.data.totalElements").value(9)
					,jsonPath("$.data.content").isArray()
					,jsonPath("$.data.content.size()").value(9)
					,jsonPath("$.data.content[*].parent").exists()
					,jsonPath("$.data.content[*].childList[?(@.size() > 0)]").exists()
					)
			;
		}
	}
	@Nested
	class GetFeeds {
		@Autowired
		SubscribeRepository subsRepo;
		@Autowired
		BoardRepository boardRepo;
		@Autowired
		ArticleRepository articleRepo;
		
		@Test
		@WithCharmroomUserDetails
		void success() throws Exception{
			subsRepo.save(Subscribe.builder()
					.subscriber(charmroomUser)
					.target(charmroomAdmin)
					.build());
			Board board = boardRepo.save(Board.builder()
					.name("")
					.type(BoardType.LIST)
					.build());
			for(int i = 0; i < 3; i++) {
				articleRepo.save(Article.builder()
						.board(board)
						.user(charmroomAdmin)
						.title("")
						.body("")
						.build());
			}
			
			// when
			mockMvc.perform(get("/api/user/feed"))
			
			// then
			.andExpectAll(status().isOk()
					,jsonPath("$.data.totalElements").value(3)
					,jsonPath("$.data.content").isArray());
		}
	}
}
