package hu.pszs.capacity_planner.service;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private static final int QUARTER_DAYS = 91;
    private static final int MAX_PHASES = 25; // Eddig ez volt a MAX_DEPARTMENTS, de most fázisokként kezeljük
    private static final int MAX_QUARTERS = 34;
    private static final int TOTAL_DEPARTMENTS = 25;

    // --- 1. LÉPÉS: AZ EREDETI ALGORITMUSOD (IDŐRENDI ELOSZLÁS) ---
    // Csak a neve változott, a logika ugyanaz!
    public double[][] calculatePhaseDistribution(int[] phaseDurations, int count) {
        double[][] phaseMap = new double[MAX_PHASES][MAX_QUARTERS];
        if (count == 0) return phaseMap;

        for (int u = 0; u < count; u++) {
            double carry = 0;
            int currentQuarter = 0;
            for (int i = 0; i < phaseDurations.length; i++) {
                double workAmount = phaseDurations[i];
                double totalWork = carry + workAmount;

                if (totalWork > QUARTER_DAYS) {
                    double modu = totalWork % QUARTER_DAYS;
                    int times = (int) (totalWork / QUARTER_DAYS);
                    for (int g = 0; g < times; g++) {
                        if (currentQuarter < MAX_QUARTERS) {
                            phaseMap[i][currentQuarter] += (QUARTER_DAYS - carry);
                            currentQuarter++;
                            carry = 0;
                        }
                    }
                    if (currentQuarter < MAX_QUARTERS) {
                        phaseMap[i][currentQuarter] += modu;
                        carry = modu;
                    }
                } else {
                    if (currentQuarter < MAX_QUARTERS) {
                        phaseMap[i][currentQuarter] += workAmount;
                        carry += workAmount;
                    }
                }
            }
        }
        return phaseMap;
    }

    // --- 2. LÉPÉS: A VÉGSŐ SZÁMÍTÁS (SZORZÁS A RÉSZLEGEKKEL) ---
    public double[][] calculateFinalLoad(int[] phaseDurations, int count, double[][] deptRequirementsByPhase) {
        // Először megkapjuk, melyik fázis melyik negyedévben mennyi napot "eszik"
        double[][] phaseMap = calculatePhaseDistribution(phaseDurations, count);
        
        // Eredmény tömb: [részleg][negyedév]
        double[][] finalLoad = new double[TOTAL_DEPARTMENTS][MAX_QUARTERS];

        // Végigmegyünk minden negyedéven
        for (int q = 0; q < MAX_QUARTERS; q++) {
            // Minden fázison
            for (int p = 0; p < phaseDurations.length; p++) {
                double daysInPhaseThisQuarter = phaseMap[p][q];
                
                if (daysInPhaseThisQuarter > 0 && phaseDurations[p] > 0) {
    for (int d = 0; d < TOTAL_DEPARTMENTS; d++) {
        // Kiszámoljuk a súlyt: ha a fázis 20 napos és 20 nap esik a Q1-be, a súly 1.0
        double weight = (double) daysInPhaseThisQuarter / phaseDurations[p];
        double requirement = deptRequirementsByPhase[p][d];
        
        if (requirement > 0) {
            finalLoad[d][q] += weight * requirement;
        }
    }
}
            }
        }
        return finalLoad;
    }
}