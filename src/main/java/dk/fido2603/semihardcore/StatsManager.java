package dk.fido2603.semihardcore;

import java.util.HashMap;
import java.util.UUID;

public class StatsManager {
    private HashMap<UUID, Integer> joinTime = new HashMap<UUID, Integer>();
    private HashMap<UUID, Integer> timeAlive = new HashMap<UUID, Integer>();
    private HashMap<UUID, Integer> longestTimeAlive = new HashMap<UUID, Integer>();
}
