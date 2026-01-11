package org.develop.classe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    public Team findTeamById(Integer id) {
        if (id == null) {
            return null;
        }
        try (Connection conn = dbConnection.getConnection()) {
            Team team = null;
            String sqlTeam = "SELECT id, name, continent FROM team WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTeam)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    team = new Team(rs.getInt("id"),
                            rs.getString("name"),
                            ContinentEnum.valueOf(rs.getString("continent")));
                }
            }

            if (team == null) {
                return null;
            }

            String sqlPlayer = "SELECT id, name, age, position, goal_nb FROM player WHERE id_team = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlPlayer)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    team.addPlayer(new Player(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            PlayerPositionEnum.valueOf(rs.getString("position")),
                            team,
                            (Integer) rs.getObject("goal_nb")
                    ));
                }
            }
            return team;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Team saveTeam(Team teamToSave) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sqlUpsertTeam = "INSERT INTO team (id, name, continent) VALUES (?, ?, ?::continent_enum) "
                + "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, continent = EXCLUDED.continent";

                try (PreparedStatement psT = conn.prepareStatement(sqlUpsertTeam)) {
                    psT.setInt(1, teamToSave.getId());
                    psT.setString(2, teamToSave.getName());
                    psT.setString(3, teamToSave.getContinent().name());
                    psT.executeUpdate();
                }

                for (Player p : teamToSave.getPlayers()) {
                    if (p.getId() != null) {
                        String sqlUpdate = "INSERT INTO player (id, name, age, position, goal_nb, id_team) VALUES (?, ?, ?, ?::position_enum, ?, ?) ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, goal_nb = EXCLUDED.goal_nb";
                        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                            ps.setInt(1, p.getId());
                            ps.setString(2, p.getName());
                            ps.setInt(3, p.getAge());
                            ps.setString(4, p.getPosition().name());
                            ps.setObject(5, p.getGoalNb());
                            ps.setInt(6, teamToSave.getId());
                            ps.executeUpdate();
                        }
                    } else {
                        String sqlInsert = "INSERT INTO player (name, age, position, goal_nb, id_team) VALUES (?, ?, ?::position_enum, ?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                            ps.setString(1, p.getName());
                            ps.setInt(2, p.getAge());
                            ps.setString(3, p.getPosition().name());
                            ps.setObject(4, p.getGoalNb());
                            ps.setInt(5, teamToSave.getId());
                            ps.executeUpdate();
                        }
                    }
                }

                String sqlUpdateGoals = "UPDATE team SET goal_nb = ? WHERE id = ?";
                try (PreparedStatement psG = conn.prepareStatement(sqlUpdateGoals)) {
                    psG.setInt(1, teamToSave.getTotalGoals());
                    psG.setInt(2, teamToSave.getId());
                    psG.executeUpdate();
                }

                conn.commit();
                return teamToSave;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getPlayerGoals(int teamId) {
        String sql = "SELECT SUM(goal_nb) AS total FROM player WHERE id_team = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
