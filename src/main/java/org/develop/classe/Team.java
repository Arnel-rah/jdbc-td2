package org.develop.classe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Team {

    private final Integer id;
    private final String name;
    private final ContinentEnum continent;
    private List<Player> players = new ArrayList<>();
    public Team(Integer id, String name, ContinentEnum continent) {
        this.id = id;
        this.name = name;
        this.continent = continent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ContinentEnum getContinent() {
        return continent;
    }

//    public List<Player> getPlayers() {
//        return Collections.unmodifiableList(players);
//    }

    public void addPlayer(Player player) {
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team team)) return false;
        return id == team.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", continent=" + continent +
                ", playerCount=" + players.size() +
                '}';
    }


    DBConnection dbConnection = new DBConnection();
        public Integer getPlayerGoals(int teamId) {
            String sql = "SELECT SUM(goal_nb) AS total_buts FROM player WHERE id_team = ?";

            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, teamId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {return rs.getInt("total_buts");
                }
            } catch (SQLException e) {
                throw new RuntimeException("erreur : " + e.getMessage());
            }
            return 4555;
        }

    public Player[] getPlayers() {
            return players.toArray(new Player[0]);
    }
}
