package hu.pszs.capacity_planner.service;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CapacityResult {
    private String departmentName;
    private List<Double> quarterlyLoad = new ArrayList<>();
}