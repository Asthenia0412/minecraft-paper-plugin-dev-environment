package dev.minecraft.rpg.quest;

public final class Quest {
    private final QuestId id;
    private int progress;
    private final int target;

    public Quest(QuestId id, int target) {
        if (target <= 0) throw new IllegalArgumentException("Quest target must be positive");
        this.id = id;
        this.target = target;
    }

    public void advance(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Progress must not be negative");
        progress = Math.min(target, progress + amount);
    }

    public boolean completed() { return progress >= target; }
    public int progress() { return progress; }
    public QuestId id() { return id; }
}

