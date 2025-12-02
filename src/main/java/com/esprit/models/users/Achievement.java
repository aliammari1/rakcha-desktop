package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class representing user achievements/badges.
 * Used for gamification and rewarding user engagement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    private Long id;

    // Achievement definition
    private String name;
    private String description;
    private String icon; // Emoji or icon identifier
    private String category; // "watching", "reviewing", "social", "shopping", etc.

    // Requirements
    private int requiredCount; // Number of actions required
    private String requirementType; // "films_watched", "reviews_written", etc.

    // Points/rewards
    @Builder.Default
    private int points = 0;

    // Rarity
    @Builder.Default
    private AchievementRarity rarity = AchievementRarity.COMMON;

    // User-specific unlock status
    @Builder.Default
    private boolean unlocked = false;
    private LocalDateTime unlockedAt;

    // Progress towards unlocking (0.0 to 1.0)
    @Builder.Default
    private double progress = 0.0;
    private int currentCount;

    // Predefined achievements
    public static Achievement firstFilmWatched() {
        return Achievement.builder()
            .name("First Steps")
            .description("Watch your first film")
            .icon("🎬")
            .category("watching")
            .requiredCount(1)
            .requirementType("films_watched")
            .points(10)
            .rarity(AchievementRarity.COMMON)
            .build();
    }

    public static Achievement filmMarathon() {
        return Achievement.builder()
            .name("Film Marathon")
            .description("Watch 50 films")
            .icon("🎥")
            .category("watching")
            .requiredCount(50)
            .requirementType("films_watched")
            .points(100)
            .rarity(AchievementRarity.RARE)
            .build();
    }

    public static Achievement firstReview() {
        return Achievement.builder()
            .name("Critic Begins")
            .description("Write your first review")
            .icon("✍️")
            .category("reviewing")
            .requiredCount(1)
            .requirementType("reviews_written")
            .points(15)
            .rarity(AchievementRarity.COMMON)
            .build();
    }

    public static Achievement socialButterfly() {
        return Achievement.builder()
            .name("Social Butterfly")
            .description("Add 10 friends")
            .icon("👥")
            .category("social")
            .requiredCount(10)
            .requirementType("friends_added")
            .points(50)
            .rarity(AchievementRarity.UNCOMMON)
            .build();
    }

    public static Achievement bingeWatcher() {
        return Achievement.builder()
            .name("Binge Watcher")
            .description("Watch 10 episodes in one day")
            .icon("📺")
            .category("watching")
            .requiredCount(10)
            .requirementType("episodes_per_day")
            .points(75)
            .rarity(AchievementRarity.EPIC)
            .build();
    }

    /**
     * Checks if the achievement is unlocked.
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Updates progress and unlocks if requirements are met.
     */
    public void updateProgress(int newCount) {
        this.currentCount = newCount;
        if (requiredCount > 0) {
            this.progress = Math.min(1.0, (double) newCount / requiredCount);
        }
        if (newCount >= requiredCount && !unlocked) {
            unlock();
        }
    }

    /**
     * Unlocks the achievement.
     */
    public void unlock() {
        this.unlocked = true;
        this.unlockedAt = LocalDateTime.now();
        this.progress = 1.0;
    }

    /**
     * Enum representing achievement rarity levels.
     */
    public enum AchievementRarity {
        COMMON("Common", "#808080"),
        UNCOMMON("Uncommon", "#00FF00"),
        RARE("Rare", "#0080FF"),
        EPIC("Epic", "#8000FF"),
        LEGENDARY("Legendary", "#FFD700");

        private final String displayName;
        private final String color;

        AchievementRarity(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColor() {
            return color;
        }
    }
}
