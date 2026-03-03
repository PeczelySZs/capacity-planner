package hu.pszs.capacity_planner.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private static final int QUARTER_DAYS = 91;
    private static final int MAX_DEPARTMENTS = 25;
    private static final int MAX_QUARTERS = 34;

    public double[][] calculateCapacity(int[] durations, int count) {
        // A VBA myArray megfelelője (2D tömb)
        double[][] loadArray = new double[MAX_DEPARTMENTS][MAX_QUARTERS];

        if (count == 0) return loadArray;

        for (int u = 0; u < count; u++) {
            double carry = 0;
            int currentQuarter = 0;

            for (int i = 0; i < durations.length; i++) {
                double workAmount = durations[i];
                double totalWork = carry + workAmount;

                if (totalWork > QUARTER_DAYS) {
                    double modu = totalWork % QUARTER_DAYS;
                    int times = (int) (totalWork / QUARTER_DAYS);

                    for (int g = 0; g < times; g++) {
                        if (currentQuarter < MAX_QUARTERS) {
                            loadArray[i][currentQuarter] += (QUARTER_DAYS - carry);
                            currentQuarter++;
                            carry = 0;
                        }
                    }
                    if (currentQuarter < MAX_QUARTERS) {
                        loadArray[i][currentQuarter] += modu;
                        carry = modu;
                    }
                } else {
                    if (currentQuarter < MAX_QUARTERS) {
                        loadArray[i][currentQuarter] += workAmount;
                        carry += workAmount;
                    }
                }
            }
        }

        // Átlagolás (az utolsó For ciklusod az Excelben)
        for (int i = 0; i < durations.length; i++) {
            if (durations[i] != 0) {
                for (int j = 0; j < MAX_QUARTERS; j++) {
                    loadArray[i][j] /= durations[i];
                }
            }
        }

        return loadArray;
    }
}