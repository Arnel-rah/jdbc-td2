import org.develop.classe.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataRetrieverTest {

    private DataRetriever dataRetriever;


    @BeforeEach
    void setUp() {
        dataRetriever = new DataRetriever();
    }

    // a) id = 1 → Real Madrid avec 3 joueurs
    @Test
    void testFindTeamById_realMadrid() {

    }

    // b) id = 5 → Inter Miami avec liste vide
    @Test
    void testFindTeamById_interMiamiNoPlayers() {
        Team team = dataRetriever.findTeamById(5);

        assertNotNull(team);
        assertEquals("Inter Miami", team.getName());
        assertTrue(team.getPlayers().isEmpty());
    }

    // c) page=1 size=2 → Courtois, Carvajal
    @Test
    void testFindPlayers_page1_size2() {
        List<Player> players = dataRetriever.findPlayers(1, 2);

        assertEquals(2, players.size());
        assertEquals("Thibaut Courtois", players.get(0).getName());
        assertEquals("Dani Carvajal", players.get(1).getName());
    }

    // d) page=3 size=5 → liste vide
    @Test
    void testFindPlayers_emptyPage() {
        List<Player> players = dataRetriever.findPlayers(3, 5);

        assertTrue(players.isEmpty());
    }

    // e) playerName="an" → Real Madrid, Atletico Madrid
    @Test
    void testFindTeamsByPlayerName() {
        List<Team> teams = dataRetriever.findTeamsByPlayerName("an");

        assertEquals(2, teams.size());

        List<String> names = teams.stream()
                .map(Team::getName)
                .toList();

        assertTrue(names.contains("Real Madrid CF"));
        assertTrue(names.contains("Atletico Madrid"));
    }

    // f) critères complexes → Jude Bellingham
    @Test
    void testFindPlayersByCriteria() {
        List<Player> players = dataRetriever.findPlayersByCriteria(
                "ud",
                PlayerPositionEnum.MIDF,
                "Madrid",
                ContinentEnum.EUROPA,
                1,
                10
        );

        assertEquals(1, players.size());
        assertEquals("Jude Bellingham", players.get(0).getName());
    }

    // g) createPlayers → exception (Jude + Pedri)
    @Test
    void testCreatePlayers_shouldFail() {
        Player p1 = new Player(null, "Jude Bellingham", 23,
                PlayerPositionEnum.STR, null);
        Player p2 = new Player(null, "Pedri", 24,
                PlayerPositionEnum.MIDF, null);

        assertThrows(RuntimeException.class, () ->
                dataRetriever.createPlayers(List.of(p1, p2))
        );
    }

    // h) createPlayers → succès (Vini + Pedri)
    @Test
    void testCreatePlayers_success() {
        Player p1 = new Player(null, "Vini", 25,
                PlayerPositionEnum.STR, null);
        Player p2 = new Player(null, "Pedri", 24,
                PlayerPositionEnum.MIDF, null);

        List<Player> players = dataRetriever.createPlayers(List.of(p1, p2));

        assertEquals(2, players.size());
        assertNotNull(players.get(0).getId());
        assertNotNull(players.get(1).getId());
    }

    // i) saveTeam id=1 → ajouter un joueur sans supprimer les anciens
    @Test
    void testSaveTeam_addPlayer() {
        Team team = dataRetriever.findTeamById(1);
        int initialCount = team.getPlayers().size();

        Player vini = new Player(null, "Vini", 25,
                PlayerPositionEnum.STR, team);

        team.addPlayer(vini);
        Team updatedTeam = dataRetriever.saveTeam(team);

        assertEquals(initialCount + 1, updatedTeam.getPlayers().size());
    }

    // j) saveTeam id=2 → supprimer tous les joueurs
    @Test
    void testSaveTeam_removeAllPlayers() {
        Team team = dataRetriever.findTeamById(2);

        team.getPlayers().clear();
        Team updatedTeam = dataRetriever.saveTeam(team);

        assertTrue(updatedTeam.getPlayers().isEmpty());
    }
}
