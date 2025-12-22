package org.develop;


import org.develop.classe.DataRetriever;
import org.develop.classe.Player;
import org.develop.classe.Team;

public class Main {

    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();

        Integer teamId = 1;

        Team team = dataRetriever.findTeamById(teamId);

        if (team == null) {
            System.out.println("Aucune équipe trouvée avec l'ID " + teamId);
            return;
        }

        System.out.println("Équipe trouvée :");
        System.out.println("ID : " + team.getId());
        System.out.println("Nom : " + team.getName());
        System.out.println("Continent : " + team.getContinent());
        System.out.println("Nombre de joueurs : " + team.getPlayerCount());
        System.out.println();

        System.out.println("Joueurs de l'équipe :");
        for (Player player : team.getPlayers()) {
            System.out.println("- " + player.getName()
                    + ", Position: " + player.getPosition()
                    + ", Équipe: " + player.getTeamName() + ")");
        }
    }
}