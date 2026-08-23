package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.service.RouteInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rides")
public class RouteInfoWebController {

    private final RouteInfoService routeInfoService;

    public RouteInfoWebController(
            RouteInfoService routeInfoService) {

        this.routeInfoService = routeInfoService;
    }

    @GetMapping("/{id}/route-info")
    public String getRouteInfo(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            model.addAttribute(
                    "routeInfo",
                    routeInfoService.getRouteInfo(id)
            );

            return "route-info";

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );

            return "redirect:/rides";
        }
    }
}