package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.dto.AdminStatsResponse;
import gr.hua.dit.greenride.dto.AdminUserResponse;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final AdminService adminService;

    public AdminRestController(
            AdminService adminService) {

        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public AdminStatsResponse getStatistics() {

        return adminService.getStatistics();
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getUsers() {

        return adminService
                .getAllUsers()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    @PutMapping("/users/{id}/block")
    public AdminUserResponse blockUser(
            @PathVariable Long id) {

        User user =
                adminService.blockUser(id);

        return toUserResponse(user);
    }

    @PutMapping("/users/{id}/unblock")
    public AdminUserResponse unblockUser(
            @PathVariable Long id) {

        User user =
                adminService.unblockUser(id);

        return toUserResponse(user);
    }

    private AdminUserResponse toUserResponse(
            User user) {

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isBlocked()
        );
    }
}