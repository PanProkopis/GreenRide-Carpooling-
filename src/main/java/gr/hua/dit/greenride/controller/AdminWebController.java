package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final AdminService adminService;

    public AdminWebController(
            AdminService adminService) {

        this.adminService = adminService;
    }

    @GetMapping
    public String adminDashboard(
            Model model) {

        model.addAttribute(
                "stats",
                adminService.getStatistics()
        );

        model.addAttribute(
                "users",
                adminService.getAllUsers()
        );

        return "admin";
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            adminService.blockUser(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "User blocked successfully."
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            adminService.unblockUser(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "User unblocked successfully."
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/admin";
    }
}