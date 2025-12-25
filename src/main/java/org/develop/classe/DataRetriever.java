package org.develop.classe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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

        try (Connection connection = dbConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

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
        if (newPlayers == null || newPlayers.isEmpty()) {
            return List.of();
        }

        String query = """
        INSERT INTO player (name, age, position, id_team)
        VALUES (?, ?, ?, ?)
    """;

        List<Player> createdPlayers = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            for (Player player : newPlayers) {
                stmt.setString(1, player.getName());
                stmt.setInt(2, player.getAge());
                stmt.setString(3, player.getPosition().name());

                if (player.getTeam() != null) {
                    stmt.setInt(4, player.getTeam().getId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                stmt.addBatch();
            }

            stmt.executeBatch();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                int index = 0;
                while (rs.next()) {
                    Player original = newPlayers.get(index);

                    Player created = new Player(
                            rs.getInt(1),
                            original.getName(),
                            original.getAge(),
                            original.getPosition(),
                            original.getTeam()
                    );

                    createdPlayers.add(created);
                    index++;
                }
            }

            conn.commit();
            return createdPlayers;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur" + " " + e);
        }
    }

    public Team saveTeam(Team teamToSave) {
        if (teamToSave == null) {
            return null;
        }

        String query = """
        INSERT INTO team (name, continent)
        VALUES (?, ?)
    """;

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, teamToSave.getName());
            stmt.setString(2, teamToSave.getContinent().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Team(
                            rs.getInt(1),
                            teamToSave.getName(),
                            teamToSave.getContinent()
                    );
                }
            }

            throw new RuntimeException("Impossible de récupérer l'id de l'equipe créer");

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur  : " + teamToSave.getName(), e
            );
        }
    }

    public List<Team> findTeamsByPlayerName(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return List.of();
        }

        String query = """
        SELECT DISTINCT t.id, t.name, t.continent
        FROM team t
        JOIN player p ON p.id_team = t.id
        WHERE p.name ILIKE ?
    """;

        List<Team> teams = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + playerName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teams.add(new Team(
                            rs.getInt("id"),
                            rs.getString("name"),
                            ContinentEnum.valueOf(rs.getString("continent"))
                    ));
                }
            }

            return teams;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur : " + playerName, e
            );
        }
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
