package org.develop.classe;

import java.util.List;

public class DataRetriever {
    Team findTeamById(Integer id){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    List<Player> findPlayers(int page, int size){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    List<Player> createPlayers(List<Player> newPlayers){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Team saveTeam(Team teamToSave){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    List<Team> findTeamsByPlayerName(String playerName){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    List<Player> findPlayersByCriteria(String playerName,
                                       PlayerPositionEnum position, String teamName, ContinentEnum continent, int page, int size){
            throw new UnsupportedOperationException("Not supported yet.");
    }



}
