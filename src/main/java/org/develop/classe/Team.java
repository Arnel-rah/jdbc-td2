package org.develop.classe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {
    private final Integer id;
    private final String name;
    private final ContinentEnum continent;
    private final List<Player> players = new ArrayList<>();

    public Team(Integer id, String name, ContinentEnum continent) {
        this.id = id;
        this.name = name;
        this.continent = continent;
    }

    public Integer getId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public ContinentEnum getContinent() { 
        return continent; 
    }
    
    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

    public int getTotalGoals() {
        int total = 0;
        for (Player p : players) {
            if (p.getGoalNb() != null) {
                total = total + p.getGoalNb();
            }
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TEAM: ").append(name).append(" [").append(continent).append("]\n");
        sb.append("Total buts équipe: ").append(getTotalGoals()).append("\n");
        sb.append("Joueurs:\n");
        
        for (Player p : players) {
            int buts = 0;
            if (p.getGoalNb() != null) {
                buts = p.getGoalNb();
            }
            sb.append("  - ").append(p.getName())
              .append(" (").append(p.getPosition()).append(") ")
              .append("Buts: ").append(buts).append("\n");
        }
        return sb.toString();
    }
}