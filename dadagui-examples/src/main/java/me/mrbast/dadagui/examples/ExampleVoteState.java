package me.mrbast.dadagui.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Shared mutable state for the vote-board example.
 */
public final class ExampleVoteState {
    private final Map<UUID, String> votes = new HashMap<>();

    public void vote(UUID playerId, String option) {
        votes.put(playerId, option);
    }

    public int count(String option) {
        int count = 0;
        for (String value : votes.values()) {
            if (option.equals(value)) {
                count++;
            }
        }
        return count;
    }

    public int total() {
        return votes.size();
    }
}
