package hu.pszs.capacity_planner.controller;

import hu.pszs.capacity_planner.model.LeadTime;
import hu.pszs.capacity_planner.repository.LeadTimeRepository;
import hu.pszs.capacity_planner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LeadTimeRepository leadTimeRepository;

    @GetMapping("/")
    public String index(Model model) {
        // 1. LeadTime adatok betöltése az adatbázisból
        List<LeadTime> leadTimes = leadTimeRepository.findAll();
        
        // Ha üres az adatbázis (első indítás), feltöltjük alapértelmezett 20 napokkal
        if (leadTimes.isEmpty()) {
            for (int i = 0; i < 25; i++) {
                leadTimeRepository.save(new LeadTime(i, 20));
            }
            leadTimes = leadTimeRepository.findAll();
        }

        // Listát rendezzük index szerint, hogy ne legyen keveredés
        leadTimes.sort(Comparator.comparing(LeadTime::getPhaseIndex));

        // Átalakítjuk a listát int tömbbé az algoritmusod számára
        int[] phaseDurations = leadTimes.stream()
                                        .mapToInt(LeadTime::getDays)
                                        .toArray();

        // 2. Részleg-igények táblázat (Fix tesztadatok, amíg nincs DB-ben)
        double[][] deptRequirementsByPhase = new double[25][25];
        
        // Példa adatok:
        deptRequirementsByPhase[0][0] = 50.0;  // 1. fázis, 1. részleg
        deptRequirementsByPhase[5][10] = 100.0; // 6. fázis, 11. részleg

        // 3. SZÁMÍTÁS
        // Jelenleg 1 projekttel számolunk
        double[][] results = projectService.calculateFinalLoad(phaseDurations, 1, deptRequirementsByPhase);
        
        // Adatok átadása a HTML-nek
        model.addAttribute("leadTimes", leadTimes);
        model.addAttribute("results", results);
        
        return "index";
    }

    @PostMapping("/update-leadtimes")
    public String updateLeadTimes(@RequestParam("phase") List<Integer> phases, 
                                  @RequestParam("days") List<Integer> days) {
        // Végigmegyünk a beküldött listákon és frissítjük az adatbázist
        for (int i = 0; i < phases.size(); i++) {
            LeadTime lt = new LeadTime(phases.get(i), days.get(i));
            leadTimeRepository.save(lt);
        }
        // Mentés után visszairányítunk a főoldalra, ami így már az új adatokkal frissül
        return "redirect:/";
    }
}