package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.ProfileResponse;
import com.neurotutor.user_service.dto.UpdateProfileRequest;
import com.neurotutor.user_service.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/students/{studentId}/profile")
    @PreAuthorize("#studentId.toString() == authentication.name")
    public ResponseEntity<?> getProfile(@PathVariable Long studentId) {
        try {
            ProfileResponse response = profileService.getProfile(studentId);
            return ResponseEntity.ok(response);
        } catch (ProfileService.StudentProfileNotFoundException exception) {
            return ResponseEntity.status(404).body(Map.of("detail", "Student not found."));
        }
    }

    @PutMapping("/students/{studentId}/profile")
    @PreAuthorize("#studentId.toString() == authentication.name")
    public ResponseEntity<?> updateProfile(@PathVariable Long studentId,
                                           @RequestBody UpdateProfileRequest request) {
        try {
            ProfileResponse response = profileService.updateProfile(studentId, request);
            return ResponseEntity.ok(response);
        } catch (ProfileService.StudentProfileNotFoundException exception) {
            return ResponseEntity.status(404).body(Map.of("detail", "Student not found."));
        }
    }
}
