package capi.funding.api.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ContributionTest {

    @Test
    @DisplayName("contribution - no args constructor")
    void testNoArgsConstructor() {
        assertDoesNotThrow(() -> new Contribution());
    }

    @Test
    @DisplayName("contribution - all args constructor")
    void testAllArgsConstructor() {
        assertDoesNotThrow(() -> new Contribution(
                1L,
                1,
                1,
                BigDecimal.valueOf(20),
                LocalDateTime.now()
        ));
    }

    @Test
    @DisplayName("contribution - getters and setters")
    void testGetters() {
        final LocalDateTime now = LocalDateTime.now();
        final Contribution contribution = new Contribution();

        assertDoesNotThrow(() -> {
            contribution.setId(1L);
            contribution.setUser_id(92);
            contribution.setProject_id(8420);
            contribution.setValue(BigDecimal.valueOf(200));
            contribution.setDate(now);
        });

        assertAll("test getters",
                () -> assertEquals(1L, contribution.getId()),
                () -> assertEquals(92, contribution.getUser_id()),
                () -> assertEquals(8420, contribution.getProject_id()),
                () -> assertEquals(BigDecimal.valueOf(200), contribution.getValue()),
                () -> assertEquals(now, contribution.getDate())
        );
    }
}
