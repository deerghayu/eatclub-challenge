package com.eatclub.challenge.integration;

import com.eatclub.challenge.dto.PeakTimeResponse;
import com.eatclub.challenge.service.PeakTimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for peak time service with real data.
 */
@SpringBootTest
public class PeakTimeIntegrationTest {

    @Autowired
    private PeakTimeService peakTimeService;

    @Test
    void calculatePeakTime_shouldReturn_PeakTimesWithRealData() {
        // Calculate peak times with real data
        PeakTimeResponse response = peakTimeService.calculatePeakTime();

        // Log the results
        System.out.println("Peak Time: " + response.getPeakTimeStart() +
                " - " + response.getPeakTimeEnd());

        // Should find valid peak times
        assertThat(response.getPeakTimeStart()).isNotNull();
        assertThat(response.getPeakTimeEnd()).isNotNull();

        // Based on the known data, peak should be in the evening
        assertThat(response.getPeakTimeStart()).isEqualTo("6:00pm");
        assertThat(response.getPeakTimeEnd()).isEqualTo("9:00pm");
    }
}