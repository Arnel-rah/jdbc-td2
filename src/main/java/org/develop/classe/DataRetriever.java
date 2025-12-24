package org.develop.classe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        String query = """
        SELECT p.id, p.name, p.age, p.position,
               t.id AS team_id, t.name AS team_name, t.continent
        FROM player p
        LEFT JOIN team t ON p.id_team = t.id
        ORDER BY p.id
        LIMIT ? OFFSET ?
    """;

        int offset = page * size;
        List<Player> players = new ArrayList<>();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Team team = null;
                    if (rs.getObject("team_id") != null) {
                        team = new Team(
                                rs.getInt("team_id"),
                                rs.getString("team_name"),
                                ContinentEnum.valueOf(rs.getString("continent"))
                        );
                    }

                    players.add(new Player(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            PlayerPositionEnum.valueOf(rs.getString("position")),
                            team
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                     "Erreur" + e);
        }

        return players;
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