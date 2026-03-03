package hu.pszs.capacity_planner.controller;

import hu.pszs.capacity_planner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/")
    public String index(Model model) {
        // Teszt adatok az algoritmusnak (25 részleg, 1 termék)
        int[] testDurations = new int[25];
        for(int i=0; i<25; i++) testDurations[i] = 10 + i; // Csak hogy legyen benne valami

        double[][] results = projectService.calculateCapacity(testDurations, 1);
        
        model.addAttribute("results", results);
        return "index";
    }
}