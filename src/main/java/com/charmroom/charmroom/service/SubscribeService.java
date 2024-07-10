package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.SubscribeDto;
import com.charmroom.charmroom.dto.business.SubscribeMapper;
import com.charmroom.charmroom.entity.Subscribe;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.SubscribeRepository;
import com.charmroom.charmroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;

    public SubscribeDto subscribeOrCancel(String subscriberName, String targetName) {
        User subscriber = userRepository.findByUsername(subscriberName).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "subscriberName: " + subscriberName));
        User target = userRepository.findByUsername(targetName).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "targetName: " + targetName));

        Optional<Subscribe> found = subscribeRepository.findBySubscriberAndTarget(subscriber, target);

        if (found.isPresent()) {
            Subscribe subscribe = found.get();
            subscribeRepository.delete(subscribe);
            return null; // 구독 취소
        } else {
            Subscribe subscribe = Subscribe.builder()
                    .subscriber(subscriber)
                    .target(target)
                    .build();
            Subscribe saved = subscribeRepository.save(subscribe);
            return SubscribeMapper.toDto(saved);
        }
    }

    public Page<SubscribeDto> getSubscribesBySubscriber(String subscriberName, Pageable pageable) {
        User subscriber = userRepository.findByUsername(subscriberName).orElseThrow(
                () -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "subscriberName: " + subscriberName)
        );

        Page<Subscribe> subscribes = subscribeRepository.findAllBySubscriber(subscriber, pageable);

        return subscribes.map(SubscribeMapper::toDto);
    }
}
