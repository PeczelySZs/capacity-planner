package hu.pszs.capacity_planner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "lead_times")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadTime {
    @Id
    private Integer phaseIndex; // 0-24-ig a fázis sorszáma
    private Integer days;       // Hány napos az adott fázis
}