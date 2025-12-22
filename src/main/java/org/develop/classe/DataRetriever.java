package org.develop.classe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    public Team findTeamById(Integer id) {
        if (id == null) {
            return null;
        }

        Team team = null;

        String teamQuery = "SELECT id, name, continent FROM team WHERE id = ?";
        String playersQuery = "SELECT id, name, age, position FROM player WHERE id_team = ?";

        try (Connection conn = dbConnection.getConnection()) {

            try (PreparedStatement psTeam = conn.prepareStatement(teamQuery)) {
                psTeam.setInt(1, id);
                try (ResultSet rs = psTeam.executeQuery()) {
                    if (rs.next()) {
                        team = new Team(
                                rs.getInt("id"),
                                rs.getString("name"),
                                ContinentEnum.valueOf(rs.getString("continent"))
                        );
                    } else {
                        return null;
                    }
                }
            }

            try (PreparedStatement psPlayers = conn.prepareStatement(playersQuery)) {
                psPlayers.setInt(1, id);
                try (ResultSet rs = psPlayers.executeQuery()) {
                    while (rs.next()) {
                        Player player = new Player(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getInt("age"),
                                PlayerPositionEnum.valueOf(rs.getString("position")),
                                team
                        );
                        team.addPlayer(player);
                    }
                }
            }

            return team;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur avec la base de données pour l'équipe id = " + id, e);
        }
    }

    public List<Player> findPlayers(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Player> createPlayers(List<Player> newPlayers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Team saveTeam(Team teamToSave) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Team> findTeamsByPlayerName(String playerName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Player> findPlayersByCriteria(String playerName,
                                              PlayerPositionEnum position,
                                              String teamName,
                                              ContinentEnum continent,
                                              int page,
                                              int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}