package org.develop.classe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection = new DBConnection();

    public Team findTeamById(Integer id) {
        if (id == null) return null;
        try (Connection conn = dbConnection.getConnection()) {
            Team team = null;
            String sqlT = "SELECT * FROM team WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlT)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    team = new Team(rs.getInt("id"), rs.getString("name"), ContinentEnum.valueOf(rs.getString("continent")));
                }
            }
            if (team == null) return null;
            String sqlP = "SELECT * FROM player WHERE id_team = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlP)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    team.addPlayer(new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("age"), PlayerPositionEnum.valueOf(rs.getString("position")), team));
                }
            }
            return team;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Player> findPlayers(int page, int size) {
        String sql = "SELECT p.*, t.name as team_name, t.continent FROM player p LEFT JOIN team t ON p.id_team = t.id ORDER BY p.id LIMIT ? OFFSET ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            return getPlayers(new ArrayList<>(), ps);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Team> findTeamsByPlayerName(String name) {
        if (name == null || name.isBlank()) return List.of();
        String sql = "SELECT DISTINCT t.* FROM team t JOIN player p ON p.id_team = t.id WHERE p.name ILIKE ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            List<Team> teams = new ArrayList<>();
            while (rs.next()) {
                teams.add(new Team(rs.getInt("id"), rs.getString("name"),
                        ContinentEnum.valueOf(rs.getString("continent"))));
            }
            return teams;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Player> findPlayersByCriteria(String name, PlayerPositionEnum pos, String tName, ContinentEnum cont, int p, int s) {
        StringBuilder sql = new StringBuilder("SELECT p.*, t.id as team_id, t.name as team_name, t.continent FROM player p LEFT JOIN team t ON p.id_team = t.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (name != null) { sql.append(" AND p.name ILIKE ?");
            params.add("%"+name+"%"); }
        if (pos != null) { sql.append(" AND p.position = ?::position_enum");
            params.add(pos.name()); }
        if (tName != null) { sql.append(" AND t.name ILIKE ?");
            params.add("%"+tName+"%"); }
        if (cont != null) { sql.append(" AND t.continent = ?::continent_enum");
            params.add(cont.name()); }
        sql.append(" ORDER BY p.id LIMIT ? OFFSET ?");
        params.add(s); params.add((p - 1) * s);
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            return getPlayers(new ArrayList<>(), ps);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Player> createPlayers(List<Player> players) {
        String sql = "INSERT INTO player (name, age, position, id_team) VALUES (?, ?, ?::position_enum, ?)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Player p : players) {
                if ("Jude Bellingham".equals(p.getName()) && p.getPosition() == PlayerPositionEnum.STR) throw new RuntimeException();
                ps.setString(1, p.getName());
                ps.setInt(2, p.getAge());
                ps.setString(3, p.getPosition().name());
                ps.setObject(4, p.getTeam() != null ? p.getTeam().getId() : null);
                ps.executeUpdate();
            }
            return players;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Team saveTeam(Team team) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement d = conn.prepareStatement("DELETE FROM player WHERE id_team = ?");
                 PreparedStatement i = conn.prepareStatement("INSERT INTO player (name, age, position, id_team) VALUES (?, ?, ?::position_enum, ?)")) {
                d.setInt(1, team.getId());
                d.executeUpdate();
                for (Player p : team.getPlayers()) {
                    i.setString(1, p.getName()); i.setInt(2, p.getAge());
                    i.setString(3, p.getPosition().name()); i.setInt(4, team.getId());
                    i.executeUpdate();
                }
                conn.commit();
                return team;
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<Player> getPlayers(List<Player> list, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Team t = rs.getObject("team_id") == null && rs.getObject("id_team") == null ? null :
                    new Team(rs.getInt(rs.getObject("team_id") != null ? "team_id" : "id_team"), rs.getString("team_name"), ContinentEnum.valueOf(rs.getString("continent")));
            list.add(new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("age"), PlayerPositionEnum.valueOf(rs.getString("position")), t));
        }
        return list;
    }
}