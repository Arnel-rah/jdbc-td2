package org.develop;

import org.develop.classe.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("TEST findTeamById");
        Team team = dataRetriever.findTeamById(1);
        if (team != null) {
            System.out.println(team);
            team.getPlayers().forEach(System.out::println);
        } else {
            System.out.println("Aucune équipe trouvée");
        }

        System.out.println("TEST findPlayers");
        List<Player> players = dataRetriever.findPlayers(0, 5);
        for (Player p : players) {
            System.out.println(p);
        }

        System.out.println("TEST saveTeam");
        Team newTeam = new Team(0, "Beginner FC", ContinentEnum.AFRICA);
        Team savedTeam = dataRetriever.saveTeam(newTeam);
        System.out.println("Équipe créée : " + savedTeam);

        System.out.println("TEST createPlayers");
        Player p1 = new Player(0, "John Doe", 22, PlayerPositionEnum.DEF, savedTeam);
        Player p2 = new Player(0, "Alex Smith", 25, PlayerPositionEnum.MIDF, savedTeam);

        List<Player> createdPlayers = dataRetriever.createPlayers(List.of(p1, p2));
        createdPlayers.forEach(System.out::println);

        System.out.println("findTeamsByPlayerName");
        List<Team> teams = dataRetriever.findTeamsByPlayerName("John");
        teams.forEach(System.out::println);

        System.out.println("findPlayersByCriteria");
        List<Player> filteredPlayers = dataRetriever.findPlayersByCriteria(
                "John",
                PlayerPositionEnum.DEF,
                "Beginner",
                ContinentEnum.AFRICA,
                0,
                10
        );

        filteredPlayers.forEach(System.out::println);

        System.out.println("\n===== FIN DES TESTS =====");
    }
}
