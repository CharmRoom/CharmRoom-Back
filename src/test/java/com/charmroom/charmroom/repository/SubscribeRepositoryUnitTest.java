package com.charmroom.charmroom.repository;

import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("SubscribeRepository Unit Test")
public class SubscribeRepositoryUnitTest {
    @Autowired
    SubscribeRepository subscribeRepository;
    @Autowired
    UserRepository userRepository;

    private Subscribe subscribe;
    private User subscriber;

    private User createSubscriber() {
        return User.builder()
                .username("1")
                .email("subscriber@charmroom.com")
                .nickname("subscriber")
                .password(".")
                .withdraw(false)
                .build();
    }

    private User createTarget() {
        return User.builder()
                .username("2")
                .email("target@charmroom.com")
                .nickname("target")
                .password(".")
                .withdraw(false)
                .build();
    }

    private Subscribe createSubscribe(User subscriber) {
        User target = createTarget();
        userRepository.save(target);

        return Subscribe.builder()

                .subscriber(subscriber)
                .target(target)
                .build();
    }

    @BeforeEach
    void setUp() {
        subscriber = createSubscriber();
        userRepository.save(subscriber);

        subscribe = createSubscribe(subscriber);
    }

    @Nested
    @DisplayName("Create Subscribe")
    class createTestSubscribe {
        @Test
        void success() {
            // given
            // when
            Subscribe saved = subscribeRepository.save(subscribe);
            // then
            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(subscribe.getId());
        }
    }

    @Nested
    @DisplayName("Read Subscribe")
    class readTestSubscribe {
        @Test
        void success() {
            // given
            // when
            Subscribe saved = subscribeRepository.save(subscribe);
            // then
            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(subscribe.getId());
        }

        @Test
        void fail_ReadSubscribeWithDifferentTarget() {
            // given
            Subscribe saved = subscribeRepository.save(subscribe);
            User different = User.builder()
                    .username("3")
                    .email("")
                    .nickname("")
                    .password(".")
                    .withdraw(false)
                    .build();
            userRepository.save(different);

            // when
            Optional<Subscribe> found = subscribeRepository.findBySubscriberAndTarget(subscriber, different);

            // then
            assertThat(found).isNotPresent();
        }
    }

    @Nested
    @DisplayName("Delete Subscribe")
    class deleteTestSubscribe {
        @Test
        void success() {
            // given
            Subscribe saved = subscribeRepository.save(subscribe);
            // when
            subscribeRepository.deleteById(saved.getId());
            List<Subscribe> found = subscribeRepository.findAll();
            // then
            assertThat(found).isNotNull();
            assertThat(found).isEmpty();
        }
    }
}
