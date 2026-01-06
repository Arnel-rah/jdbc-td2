package org.develop.classe;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataRetrieverTest {

    private DataRetriever dataRetriever;

    @BeforeEach
    void setUp() {
        dataRetriever = new DataRetriever();
    }

    @Test
    void testA_findTeamById_RealMadrid() {
        Team team = dataRetriever.findTeamById(1);
        assertNotNull(team);
        assertEquals("Real Madrid CF", team.getName());
        assertEquals(3, team.getPlayers().size());
    }

    @Test
    void testC_findPlayers_Pagination() {
        List<Player> players = dataRetriever.findPlayers(1, 2);
        assertEquals(2, players.size());
        assertEquals("Thibaut Courtois", players.get(0).getName());
        assertEquals("Dani Carvajal", players.get(1).getName());
    }

    @Test
    void testE_findTeamsByPlayerName() {
        List<Team> teams = dataRetriever.findTeamsByPlayerName("an");
        assertFalse(teams.isEmpty());
        assertTrue(teams.stream().anyMatch(t -> t.getId() == 1));
    }

    @Test
    void testF_findPlayersByCriteria() {
        List<Player> players = dataRetriever.findPlayersByCriteria(
                "ud",
                PlayerPositionEnum.MIDF,
                "Madrid",
                ContinentEnum.EUROPA,
                1, 10
        );
        assertEquals(1, players.size());
        assertEquals("Jude Bellingham", players.get(0).getName());
    }

    @Test
    void testH_createPlayers_success() {
        Team team = dataRetriever.findTeamById(1);
        Player p = new Player(null, "Vinicius Jr", 24, PlayerPositionEnum.STR, team);
        List<Player> res = dataRetriever.createPlayers(List.of(p));
        assertEquals(1, res.size());
    }

    @Test
    void testJ_saveTeam_removeAllPlayers() {
        Team team = dataRetriever.findTeamById(2);
        team.setPlayers(new ArrayList<>());
        Team updated = dataRetriever.saveTeam(team);
        assertTrue(updated.getPlayers().isEmpty());
    }
}