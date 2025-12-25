package org.develop.classe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    public Team findTeamById(Integer id) {
        if (id == null) return null;

        String teamSql = "SELECT id, name, continent FROM team WHERE id = ?";
        String playerSql = "SELECT id, name, age, position FROM player WHERE id_team = ?";

        try (Connection conn = dbConnection.getConnection()) {

            Team team;

            try (PreparedStatement ps = conn.prepareStatement(teamSql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) return null;

                team = new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        ContinentEnum.valueOf(rs.getString("continent"))
                );
            }

            try (PreparedStatement ps = conn.prepareStatement(playerSql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

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

            return team;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Player> createPlayers(List<Player> players) {

        String sql = "INSERT INTO player(name, age, position, id_team) VALUES (?, ?, ?, ?)";
        List<Player> result = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Player p : players) {
                ps.setString(1, p.getName());
                ps.setInt(2, p.getAge());
                ps.setString(3, p.getPosition().name());
                ps.setInt(4, p.getTeam().getId());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    result.add(new Player(
                            rs.getInt(1),
                            p.getName(),
                            p.getAge(),
                            p.getPosition(),
                            p.getTeam()
                    ));
                }
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Team saveTeam(Team teamToSave) {

        if (teamToSave == null) {
            return null;
        }

        String query = """
        INSERT INTO team (name, continent)
        VALUES (?, ?::continent_enum)
    """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
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

            throw new RuntimeException("Impossible de récupérer l'id de l'équipe");

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'équipe", e);
        }
    }


    public List<Player> findPlayers(int page, int size) {

        String sql = """
        SELECT p.id, p.name, p.age, p.position,
               t.id AS team_id, t.name AS team_name, t.continent
        FROM player p
        LEFT JOIN team t ON p.id_team = t.id
        ORDER BY p.id
        LIMIT ? OFFSET ?
    """;

        int offset = page * size;
        List<Player> players = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            return getPlayers(players, ps);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Team> findTeamsByPlayerName(String playerName) {

        if (playerName == null || playerName.isBlank()) {
            return List.of();
        }

        String sql = """
        SELECT DISTINCT t.id, t.name, t.continent
        FROM team t
        JOIN player p ON p.id_team = t.id
        WHERE p.name ILIKE ?
    """;

        List<Team> teams = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + playerName + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                teams.add(new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        ContinentEnum.valueOf(rs.getString("continent"))
                ));
            }

            return teams;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Player> findPlayersByCriteria(
            String playerName,
            PlayerPositionEnum position,
            String teamName,
            ContinentEnum continent,
            int page,
            int size) {

        StringBuilder sql = new StringBuilder("""
        SELECT p.id, p.name, p.age, p.position,
               t.id AS team_id, t.name AS team_name, t.continent
        FROM player p
        LEFT JOIN team t ON p.id_team = t.id
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (playerName != null && !playerName.isBlank()) {
            sql.append(" AND p.name ILIKE ?");
            params.add("%" + playerName + "%");
        }

        if (position != null) {
            sql.append(" AND p.position = ?");
            params.add(position.name());
        }

        if (teamName != null && !teamName.isBlank()) {
            sql.append(" AND t.name ILIKE ?");
            params.add("%" + teamName + "%");
        }

        if (continent != null) {
            sql.append(" AND t.continent = ?");
            params.add(continent.name());
        }

        sql.append(" ORDER BY p.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        List<Player> players = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            return getPlayers(players, ps);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Player> getPlayers(List<Player> players, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Team team = null;
            if (rs.getObject("team_id") != null) {
                team = new Team(
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        ContinentEnum.valueOf(rs.getString("continent"))
                );
            }

            Player player = new Player(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    PlayerPositionEnum.valueOf(rs.getString("position")),
                    team
            );

            players.add(player);
        }

        return players;
    }

}
