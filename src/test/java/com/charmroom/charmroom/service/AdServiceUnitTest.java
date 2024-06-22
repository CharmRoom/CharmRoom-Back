package com.charmroom.charmroom.service;

import com.charmroom.charmroom.entity.Ad;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.AdRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.util.CharmroomUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdServiceUnitTest {
    @Mock
    AdRepository adRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    private CharmroomUtil.Upload uploadUtil;
    @InjectMocks
    AdService adService;

    private Ad ad;
    private String title;
    private String link;
    private Image image;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int adId;

    Image createImage() {
        return Image.builder()
                .path("")
                .originalName("")
                .build();
    }

    Ad createAd(String prefix) {
        return Ad.builder()
                .id(adId)
                .title(prefix + title)
                .link(link)
                .image(image)
                .start(startTime)
                .end(endTime)
                .build();
    }

    @BeforeEach
    void setUp() {
        adId = 1;
        title = "ad title";
        link = "ad link";
        startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        endTime = LocalDateTime.of(2024, 1, 2, 0, 0);
        image = createImage();
        ad = createAd("");
    }

    @Nested
    @DisplayName("create Ad")
    class CreateAd {
        @Test
        void success() {
            // given
            MultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());

            doReturn(image).when(uploadUtil).buildImage(imageFile);
            doReturn(image).when(imageRepository).save(image);
            doReturn(ad).when(adRepository).save(any(Ad.class));

            // when
            Ad created = adService.create(title, link, imageFile, startTime, endTime);

            // then
            assertThat(created).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get Ad List")
    class GetAdList {
        @Test
        void success() {
            // given
            List<Ad> adList = List.of(createAd("1"), createAd("2"), createAd("3"));
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "startTime");
            PageImpl<Ad> adPage = new PageImpl<>(adList);

            doReturn(adPage).when(adRepository).findAll(pageRequest);

            // when
            Page<Ad> allAdsByPageable = adService.getAllAdsByPageable(pageRequest);

            // then
            assertThat(allAdsByPageable).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Update title")
    class UpdateTitle {
        @Test
        void success() {
            // given
            doReturn(Optional.of(ad)).when(adRepository).findById(ad.getId());

            // when
            Ad updated = adService.updateTitle(ad.getId(), "new ad title");

            // then
            assertThat(updated.getTitle()).isEqualTo("new ad title");
        }

        @Test
        void fail_AdNotFound() {
            // given
            doReturn(Optional.empty()).when(adRepository).findById(adId);

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    adService.updateTitle(adId, "new ad title"));

            // then
            assertThat(thrown).isInstanceOf(BusinessLogicException.class);
            assertThat(thrown.getMessage()).isEqualTo("adId: " + adId);
        }
    }

    @Nested
    @DisplayName("Update link")
    class UpdateLink {
        @Test
        void success() {
            // given
            doReturn(Optional.of(ad)).when(adRepository).findById(adId);

            // when
            Ad updated = adService.updateLink(adId, "new ad link");

            // then
            assertThat(updated.getLink()).isEqualTo("new ad link");
        }

        @Test
        void fail_AdNotFound() {
            // given
            doReturn(Optional.empty()).when(adRepository).findById(adId);

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    adService.updateLink(adId, "new ad link"));

            // then
            assertThat(thrown).isInstanceOf(BusinessLogicException.class);
            assertThat(thrown.getMessage()).isEqualTo("adId: " + adId);
        }
    }

    @Nested
    @DisplayName("Update Image")
    class UpdateImage {
        @Test
        void success() {
            // given
            MultipartFile imageFile = new MockMultipartFile("new file", "test.png", "image/png", "test".getBytes());
            Image image = Image.builder().build();

            doReturn(image).when(uploadUtil).buildImage(imageFile);
            doReturn(image).when(imageRepository).save(image);
            doReturn(Optional.of(ad)).when(adRepository).findById(adId);

            // when
            Ad updated = adService.updateImage(adId, imageFile);

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getImage()).isEqualTo(image);
        }

        @Test
        void fail_AdNotFound() {
            // given
            MultipartFile imageFile = new MockMultipartFile("new file", "test.png", "image/png", "test".getBytes());

            doReturn(Optional.empty()).when(adRepository).findById(adId);

            // when
            BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () ->
                    adService.updateImage(adId, imageFile));

            // then
            assertThat(thrown).isInstanceOf(BusinessLogicException.class);
            assertThat(thrown.getMessage()).isEqualTo("adId: " + adId);
        }
    }

    @Nested
    @DisplayName("Update Time")
    class UpdateTime {
        @Test
        void success_updateStartTime() {
            // given
            doReturn(Optional.of(ad)).when(adRepository).findById(adId);
            LocalDateTime newStartTime = LocalDateTime.of(2024, 1, 6, 0, 0);

            // when
            Ad updated = adService.updateStartTime(adId, newStartTime);

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getStart()).isEqualTo(newStartTime);
        }

        @Test
        void success_updateEndTime() {
            // given
            doReturn(Optional.of(ad)).when(adRepository).findById(adId);
            LocalDateTime newEndTime = LocalDateTime.of(2024, 12, 1, 0, 0);

            // when
            Ad updated = adService.updateEndTime(adId, newEndTime);

            // then
            assertThat(updated).isNotNull();
            assertThat(updated.getEnd()).isEqualTo(newEndTime);
        }
    }

    @Nested
    @DisplayName("Delete Ad")
    class DeleteAd {
        @Test
        void success() {
            // given
            doReturn(Optional.of(ad)).when(adRepository).findById(adId);

            // when
            adService.deleteAd(adId);

            // then
            verify(adRepository).delete(ad);
        }
    }
}
