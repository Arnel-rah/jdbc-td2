package org.develop.classe;

import java.util.Objects;

public class Player {

    private final int id;
    private final String name;
    private final PlayerPositionEnum position;
    private final Team team;

    public Player(int id, String name, int age, PlayerPositionEnum position, Team team) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PlayerPositionEnum getPosition() {
        return position;
    }

    public Team getTeam() {
        return team;
    }

    public String getTeamName() {
        return team != null ? team.getName() : "Aucune équipe";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return id == player.id &&
                Objects.equals(name, player.name) &&
                position == player.position &&
                Objects.equals(team, player.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, position, team);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", team=" + (team != null ? team.getName() : "aucune") +
                '}';
    }
}