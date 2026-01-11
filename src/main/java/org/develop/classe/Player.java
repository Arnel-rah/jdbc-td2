package org.develop.classe;

import java.util.Objects;

public class Player {

    private final Integer id;
    private final String name;
    private final int age;
    private final PlayerPositionEnum position;
    private Team team;
    private Integer goalNb;

    public Player(Integer id, String name, int age, PlayerPositionEnum position, Team team, Integer goalNb) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.position = position;
        this.team = team;
        this.goalNb = goalNb;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public PlayerPositionEnum getPosition() { return position; }
    public Team getTeam() { return team; }
    public Integer getGoalNb() { return goalNb; }

    public void setGoalNb(Integer goalNb) { this.goalNb = goalNb; }
    public void setTeam(Team team) { this.team = team; }

    public String getTeamName() {
        return team != null ? team.getName() : "aucune équipe";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) &&
                age == player.age &&
                Objects.equals(name, player.name) &&
                position == player.position &&
                Objects.equals(team, player.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, position, team);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", position=" + position +
                ", goals=" + (goalNb != null ? goalNb : 0) +
                ", team=" + (team != null ? team.getName() : "aucune") +
                '}';
    }
}