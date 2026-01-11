package org.develop;

import org.develop.classe.ContinentEnum;
import org.develop.classe.DataRetriever;
import org.develop.classe.Player;
import org.develop.classe.PlayerPositionEnum;
import org.develop.classe.Team;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataTest = new DataRetriever();
        System.out.println("--- Chargement de l'équipe ID 2 ---");
        Team team2 = dataTest.findTeamById(2);
        if (team2 != null) {
            System.out.println(team2);
        } else {
            System.out.println("Équipe 2 non trouvée.");
        }

        System.out.println("Création equipe ");
        Team barea = new Team(10, "Barea", ContinentEnum.AFRICA);
              
        barea.addPlayer(new Player(null, "Rayan Raveloson", 27, PlayerPositionEnum.MIDF, barea, 3));
        barea.addPlayer(new Player(null, "Loïc Lapoussin", 28, PlayerPositionEnum.STR, barea, 1));

        dataTest.saveTeam(barea);
        System.out.println("Teste  :");
        System.out.println(dataTest.findTeamById(10));
    }
}