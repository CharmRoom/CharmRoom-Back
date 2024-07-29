package com.charmroom.charmroom.controller.api;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.charmroom.charmroom.dto.business.ClubDto;
import com.charmroom.charmroom.dto.business.ClubMapper;
import com.charmroom.charmroom.dto.business.ClubRegisterDto;
import com.charmroom.charmroom.dto.business.ClubRegisterMapper;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubCreateRequestDto;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubResponseDto;
import com.charmroom.charmroom.dto.presentation.ClubDto.ClubUpdateRequestDto;
import com.charmroom.charmroom.dto.presentation.ClubRegisterDto.ClubRegisterResponseDto;
import com.charmroom.charmroom.dto.presentation.CommonResponseDto;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.service.ClubRegisterService;
import com.charmroom.charmroom.service.ClubService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;
    private final ClubRegisterService clubRegisterService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/")
    public ResponseEntity<?> createClub(
            @ModelAttribute @Valid ClubCreateRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        ClubDto club;

        ClubDto clubDto = ClubDto.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .contact(requestDto.getContact())
                .build();

        if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
            club = clubService.createClub(user.getUsername(), clubDto, requestDto.getImage());
        } else {
            club = clubService.createClub(user.getUsername(), clubDto);
        }

        ClubResponseDto response = ClubMapper.toResponse(club);
        return CommonResponseDto.created(response).toResponseEntity();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{clubId}")
    public ResponseEntity<?> getClub(
            @PathVariable("clubId") Integer clubId
    ) {
        ClubDto club = clubService.getClub(clubId);
        ClubResponseDto response = ClubMapper.toResponse(club);

        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/list")
    public ResponseEntity<?> getClubList(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ClubDto> dtos = clubService.getAllClubsByPageable(pageable);
        Page<ClubResponseDto> responseDtos = dtos.map(dto -> ClubMapper.toResponse(dto));

        return CommonResponseDto.ok(responseDtos).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{clubId}")
    public ResponseEntity<?> updateClub(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ClubUpdateRequestDto request
    ) {
        ClubDto clubDto = ClubDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .contact(request.getContact())
                .build();

        ClubDto dto = clubService.update(clubId, clubDto, user.getUsername());

        ClubResponseDto response = ClubMapper.toResponse(dto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/owner/{clubId}")
    public ResponseEntity<?> updateClubOwner(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user,
            @RequestParam("username") String updatedName
    ) {
        ClubDto clubDto = clubService.changeOwner(clubId, updatedName, user.getUsername());
        ClubResponseDto response = ClubMapper.toResponse(clubDto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/{clubId}")
    public ResponseEntity<?> updateClubImage(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user,
            @RequestPart("image") MultipartFile image
    ) {
        ClubDto dto = clubService.setImage(clubId, image, user.getUsername());
        ClubResponseDto response = ClubMapper.toResponse(dto);
        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{clubId}")
    public ResponseEntity<?> deleteClub(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user
    ) {
        clubService.deleteClub(clubId, user.getUsername());
        return CommonResponseDto.ok().toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register/{clubId}")
    public ResponseEntity<?> register(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User user
    ) {
        ClubRegisterDto dto = clubRegisterService.register(user.getUsername(), clubId);
        ClubRegisterResponseDto response = ClubRegisterMapper.toResponse(dto);
        return CommonResponseDto.created(response).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/register/{clubId}")
    public ResponseEntity<?> getRegistersByClub(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User owner,
            @PageableDefault(size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ClubRegisterDto> dtos = clubRegisterService.getClubRegistersByClub(clubId, pageable, owner.getUsername());
        Page<ClubRegisterResponseDto> response = dtos.map(ClubRegisterMapper::toResponse);

        return CommonResponseDto.ok(response).toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/register/{clubId}")
    public ResponseEntity<?> approve(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User owner,
            @RequestParam("username") String username
    ) {
        clubRegisterService.approveClubRegister(owner.getUsername(), username, clubId);
        return CommonResponseDto.ok().toResponseEntity();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/register/{clubId}")
    public ResponseEntity<?> deleteRegistersByClub(
            @PathVariable("clubId") Integer clubId,
            @AuthenticationPrincipal User owner,
            @RequestParam("username") String username
    ) {
        clubRegisterService.deleteClubRegister(username, clubId, owner.getUsername());
        return CommonResponseDto.ok().toResponseEntity();
    }
}
