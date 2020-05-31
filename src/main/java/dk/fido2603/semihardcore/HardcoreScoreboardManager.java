package dk.fido2603.semihardcore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class HardcoreScoreboardManager {
    private ScoreboardManager manager;
    private Scoreboard leaderboard;

    HardcoreScoreboardManager()
    {
        manager = Bukkit.getScoreboardManager();
    }

    public void InitializeScoreboard()
    {
        leaderboard = manager.getNewScoreboard();

        Objective objective = leaderboard.registerNewObjective("HardcoreLeaderboard", "dummy", ChatColor.DARK_RED + "Hardcore Players");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Get stats
        Score score =
    }

    public void updatePlayer(Player p, Integer time)
    {

    }




}
