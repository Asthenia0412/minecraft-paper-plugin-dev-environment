package dev.minecraft.rpg.skill;

import dev.minecraft.rpg.common.TraceId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillServiceTest {
    @Test
    void enforcesCooldownBetweenCasts() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        SkillService service = new SkillService(clock);
        SkillDefinition fireball = new SkillDefinition("fireball", 30, 5);

        CastResult first = service.cast(fireball, new TraceId("trace-1"));
        CastResult blocked = service.cast(fireball, new TraceId("trace-2"));
        clock.advanceSeconds(30);
        CastResult afterCooldown = service.cast(fireball, new TraceId("trace-3"));

        assertTrue(first.success());
        assertFalse(blocked.success());
        assertEquals(30, blocked.remainingCooldownSeconds());
        assertTrue(afterCooldown.success());
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) { this.instant = instant; }
        private void advanceSeconds(long seconds) { instant = instant.plusSeconds(seconds); }
        @Override public ZoneOffset getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId zone) { return this; }
        @Override public Instant instant() { return instant; }
    }
}

