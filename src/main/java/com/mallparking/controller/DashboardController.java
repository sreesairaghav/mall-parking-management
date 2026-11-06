package com.mallparking.controller;

import com.mallparking.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.getSummary());
        model.addAttribute("floors", dashboardService.getFloorStats());
        model.addAttribute("activities", dashboardService.getRecentActivity());
        // Chart history is still placeholder data in this phase; live summary cards,
        // floor overview and recent activity are database-backed.
        model.addAttribute("hourlyRevenue", List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        model.addAttribute("hourlyOccupancy", List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("currentPage", "dashboard");
        return "dashboard";
    }
}
