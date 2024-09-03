package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.SubscribeDto;
import com.charmroom.charmroom.entity.Article;
import com.charmroom.charmroom.entity.Board;
import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.BoardType;
import com.charmroom.charmroom.repository.SubscribeRepository;
import com.charmroom.charmroom.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscribeServiceUnitTest {
    @Mock
    SubscribeRepository subscribeRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    SubscribeService subscribeService;

    private Subscribe subscribe;
    private User target;
    private User subscriber;

    User createUser(String prefix) {
        return User.builder()
                .username(prefix + "username")
                .nickname("nickname")
                .email("email@example.com")
                .build();
    }

    Subscribe createSubscribe() {
        return Subscribe.builder()
                .subscriber(subscriber)
                .target(target)
                .build();
    }

    @BeforeEach
    void setUp() {
        target = createUser("target");
        subscriber = createUser("subscriber");
        subscribe = createSubscribe();
    }

    @Nested
    @DisplayName("create Subscribe")
    class CreateSubscribe {
        @Test
        void success() {
            // given
            doReturn(Optional.of(target)).when(userRepository).findByUsername(target.getUsername());
            doReturn(Optional.of(subscriber)).when(userRepository).findByUsername(subscriber.getUsername());
            doReturn(Optional.empty()).when(subscribeRepository).findBySubscriberAndTarget(subscriber, target);
            doReturn(subscribe).when(subscribeRepository).save(any(Subscribe.class));

            // when
            SubscribeDto created = subscribeService.subscribeOrCancel(subscriber.getUsername(), target.getUsername());

            // then
            assertThat(created).isNotNull();
            assertThat(created.getSubscriber().getUsername()).isEqualTo(subscriber.getUsername());
            assertThat(created.getTarget().getUsername()).isEqualTo(target.getUsername());
        }

        @Test
        void whenExistingSubscribe() {
            // given
            doReturn(Optional.of(target)).when(userRepository).findByUsername(target.getUsername());
            doReturn(Optional.of(subscriber)).when(userRepository).findByUsername(subscriber.getUsername());
            doReturn(Optional.of(subscribe)).when(subscribeRepository).findBySubscriberAndTarget(subscriber, target);

            // when
            SubscribeDto result = subscribeService.subscribeOrCancel(subscriber.getUsername(), target.getUsername());

            // then
            assertNull(result);
            verify(subscribeRepository).delete(any(Subscribe.class));
        }
    }

    @Nested
    @DisplayName("get Subscribes")
    class GetSubscribes {

        private Subscribe getSubscribe(User subscriber, User target) {
            return Subscribe.builder()
                    .subscriber(subscriber)
                    .target(target)
                    .build();
        }

        @Test
        void success() {
            // given
            User target1 = createUser("target1");
            User target2 = createUser("target2");
            User target3 = createUser("target3");
            User subscriber = createUser("subscriber");

            Subscribe subscribe1 = getSubscribe(subscriber, target1);
            Subscribe subscribe2 = getSubscribe(subscriber, target2);
            Subscribe subscribe3 = getSubscribe(subscriber, target3);

            List<Subscribe> subscribeList = List.of(subscribe1, subscribe2, subscribe3);
            PageRequest pageRequest = PageRequest.of(0, 3);
            PageImpl<Subscribe> subscribePage = new PageImpl<>(subscribeList);

            doReturn(Optional.of(subscriber)).when(userRepository).findByUsername(subscriber.getUsername());
            doReturn(subscribePage).when(subscribeRepository).findAllBySubscriber(subscriber, pageRequest);

            // when
            Page<SubscribeDto> result = subscribeService.getSubscribesBySubscriber(subscriber.getUsername(), pageRequest);

            // then
            assertThat(result).hasSize(3);
        }
    }
    
    @Nested
    class GetArticlesBySubscriber{
    	@Test
    	void success() {
    		// given
    		User target = createUser("target1");
    		User subscriber = createUser("subscriber");
    		
    		List<Article> articles = new ArrayList<>();
    		
    		for(int i = 0; i < 3; i++) {
    			articles.add(Article.builder()
    					.board(Board.builder()
    							.name(Integer.toString(i))
    							.type(BoardType.LIST)
    							.build())
    					.user(target)
    					.title(Integer.toString(i))
    					.body("")
    					.build());
    		}
    		
    		var pageImpl = new PageImpl<>(articles);
    		var pr = PageRequest.of(0, 10);
    		
    		doReturn(Optional.of(subscriber)).when(userRepository).findByUsername(subscriber.getUsername());
    		doReturn(pageImpl).when(subscribeRepository).findArticlesBySubscriber(subscriber, pr);
    		
    		// when
    		var result = subscribeService.getArticlesBySubscriber(subscriber.getUsername(), pr);
    		
    		// then
    		assertThat(result).hasSize(3);
    	}
    }
}
