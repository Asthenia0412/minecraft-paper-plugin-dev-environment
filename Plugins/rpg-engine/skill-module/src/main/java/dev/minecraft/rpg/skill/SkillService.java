package dev.minecraft.rpg.skill;

import dev.minecraft.rpg.common.TraceId;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

public final class SkillService {
    private final Clock clock;
    private final Map<String, Long> cooldowns = new HashMap<>();

    public SkillService(Clock clock) {
        this.clock = clock;
    }

    public CastResult cast(SkillDefinition skill, TraceId traceId) {
        long now = clock.instant().getEpochSecond();
        long readyAt = cooldowns.getOrDefault(skill.id(), 0L);
        if (readyAt > now) {
            return new CastResult(false, readyAt - now, traceId);
        }
        cooldowns.put(skill.id(), now + skill.cooldownSeconds());
        return new CastResult(true, 0, traceId);
    }
}

