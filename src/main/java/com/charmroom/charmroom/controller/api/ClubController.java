package com.charmroom.charmroom.controller.api;

import com.charmroom.charmroom.dto.business.ClubDto;
import com.charmroom.charmroom.dto.business.ClubMapper;
import com.charmroom.charmroom.dto.business.ImageDto;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubResponseDto;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubUpdateRequestDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.ClubService;
import lombok.RequiredArgsConstructor;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubCreateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @PostMapping("/")
    public ResponseEntity<?> createClub(
            @ModelAttribute ClubCreateRequestDto requestDto
    ) {
        ClubDto club;

        ClubDto clubDto = ClubDto.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .contact(requestDto.getContact())
                .build();

        if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
            club = clubService.createClub(clubDto, requestDto.getImage());
        } else {
            club = clubService.createClub(clubDto);
        }

        ClubResponseDto response = ClubMapper.toResponse(club);
        return CommonResponseDto.created(response).toResponseEntity();
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<?> getClub(
            @PathVariable("clubId") Integer clubId
    ) {
        ClubDto club = clubService.getClub(clubId);
        ClubResponseDto response = ClubMapper.toResponse(club);

        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @GetMapping("/list")
    public ResponseEntity<?> getClubList(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ClubDto> dtos = clubService.getAllClubsByPageable(pageable);
        Page<ClubResponseDto> responseDtos = dtos.map(dto -> ClubMapper.toResponse(dto));

        return CommonResponseDto.ok(responseDtos).toResponseEntity();
    }

    @PatchMapping("/{clubId}")
    public ResponseEntity<?> updateClub(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user,
            @RequestBody ClubUpdateRequestDto request
    ) {
        ClubDto clubDto = ClubDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .contact(request.getContact())
                .build();

        ClubDto dto = clubService.update(clubId, clubDto);

        ClubResponseDto response = ClubMapper.toResponse(dto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @PostMapping("/image/{clubId}")
    public ResponseEntity<?> updateClubImage(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user,
            @RequestPart("image") MultipartFile image
    ) {
        ClubDto dto = clubService.setImage(clubId, image);
        ClubResponseDto response = ClubMapper.toResponse(dto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }


    @DeleteMapping("/{clubId}")
    public ResponseEntity<?> deleteClub(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user
    ) {
        clubService.deleteClub(clubId);
        return CommonResponseDto.ok().toResponseEntity();
    }
}
