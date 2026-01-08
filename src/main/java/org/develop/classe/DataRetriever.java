package org.develop.classe;

import java.sql.*;

public class DataRetriever {
    private final DBConnection dbConnection = new DBConnection();
    public Team findTeamById(Integer id) {
        if (id == null) return null;
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
            if (team == null) return null;
            String sqlPlayer = "SELECT id, name, age, position, goal_nb FROM player WHERE id_team = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlPlayer)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Integer goals = (Integer)
                            rs.getObject("goal_nb");
                    team.addPlayer(new Player(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            PlayerPositionEnum.valueOf(rs.getString("position")),
                            team,
                            goals
                    ));
                }
            }
            return team;
        } catch (SQLException e) {
            throw new RuntimeException(e); }
    }

    public Team saveTeam(Team teamToSave) {
        String upsertSql = "INSERT INTO player (id, name, age, position, goal_nb, id_team) " +
                "VALUES (?, ?, ?, ?::position_enum, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, goal_nb=EXCLUDED.goal_nb";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(upsertSql)) {

                for (Player p : teamToSave.getPlayers()) {
                    if (p.getId() == null) {
                        ps.setNull(1, Types.INTEGER);
                    } else {
                        ps.setInt(1, p.getId());
                    }

                    ps.setString(2, p.getName());
                    ps.setInt(3, p.getAge());
                    ps.setString(4, p.getPosition().name());

                    if (p.getGoalNb() != null) ps.setInt(5, p.getGoalNb());
                    else ps.setNull(5, Types.INTEGER);

                    ps.setInt(6, teamToSave.getId());
                    ps.executeUpdate();
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


}