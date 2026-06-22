package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.AdminStudentResponse;
import com.neurotutor.user_service.dto.AdminSummaryResponse;
import com.neurotutor.user_service.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/summary")
    public AdminSummaryResponse getSummary() {
        return adminService.getSummary();
    }

    @GetMapping("/students")
    public List<AdminStudentResponse> getStudents() {
        return adminService.getStudents();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.getStudent(id));
        } catch (AdminService.StudentNotFoundException exception) {
            return ResponseEntity.status(404)
                    .body(Map.of("detail", "Student not found."));
        }
    }
}
