package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.AdminStudentResponse;
import com.neurotutor.user_service.dto.AdminSummaryResponse;
import com.neurotutor.user_service.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private static final String ADMIN_PROXY_HEADER = "X-ADMIN-PROXY-KEY";
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final String proxyKey;

    public AdminController(AdminService adminService,
                           @Value("${admin.proxy-key:}") String proxyKey) {
        this.adminService = adminService;
        this.proxyKey = proxyKey;
        LOGGER.info("Spring admin.proxy-key exists: {}", proxyKey != null && !proxyKey.isBlank());
        LOGGER.info("Spring admin.proxy-key length: {}", proxyKey == null ? 0 : proxyKey.length());
    }

    @GetMapping("/summary")
    public AdminSummaryResponse getSummary(@RequestHeader(value = ADMIN_PROXY_HEADER, required = false) String requestKey) {
        validateProxyKey(requestKey);
        return adminService.getSummary();
    }

    @GetMapping("/students")
    public List<AdminStudentResponse> getStudents(@RequestHeader(value = ADMIN_PROXY_HEADER, required = false) String requestKey) {
        validateProxyKey(requestKey);
        return adminService.getStudents();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id,
                                        @RequestHeader(value = ADMIN_PROXY_HEADER, required = false) String requestKey) {
        logReceivedProxyKey(requestKey);
        if (!isProxyKeyValid(requestKey)) {
            return ResponseEntity.status(403).body(Map.of("detail", "Invalid admin proxy key."));
        }
        try {
            return ResponseEntity.ok(adminService.getStudent(id));
        } catch (AdminService.StudentNotFoundException exception) {
            return ResponseEntity.status(404)
                    .body(Map.of("detail", "Student not found."));
        }
    }

    private void validateProxyKey(String requestKey) {
        logReceivedProxyKey(requestKey);
        if (!isProxyKeyValid(requestKey)) {
            throw new InvalidAdminProxyKeyException();
        }
    }

    private boolean isProxyKeyValid(String requestKey) {
        return proxyKey != null && !proxyKey.isBlank() && proxyKey.equals(requestKey);
    }

    private void logReceivedProxyKey(String requestKey) {
        LOGGER.info("Spring received X-ADMIN-PROXY-KEY: {}", requestKey != null && !requestKey.isBlank());
        LOGGER.info("Spring received key length: {}", requestKey == null ? 0 : requestKey.length());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidAdminProxyKeyException.class)
    public ResponseEntity<Map<String, String>> handleInvalidProxyKey() {
        return ResponseEntity.status(403).body(Map.of("detail", "Invalid admin proxy key."));
    }

    private static class InvalidAdminProxyKeyException extends RuntimeException {
    }
}
