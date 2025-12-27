
import org.develop.classe.ContinentEnum;
import org.develop.classe.DataRetriever;
import org.develop.classe.Player;
import org.develop.classe.PlayerPositionEnum;
import org.develop.classe.Team;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataRetrieverTest {

    private DataRetriever dataRetriever;

    @BeforeEach
    void setUp() {
        dataRetriever = new DataRetriever();
    }

    @Test
    void testFindTeamById_ok() {
        Team team = dataRetriever.findTeamById(1);

        assertNotNull(team);
        assertEquals(1, team.getId());
        assertNotNull(team.getName());
        assertTrue(team.getPlayerCount() >= 0);
    }

    @Test
    void testFindTeamById_null() {
        Team team = dataRetriever.findTeamById(null);
        assertNull(team);
    }

    @Test
    void testFindPlayers_pagination() {
        List<Player> players = dataRetriever.findPlayers(0, 5);

        assertNotNull(players);
        assertTrue(players.size() <= 5);
    }

    @Test
    void testSaveTeam() {
        Team team = new Team(
                0,
                "Test JUnit FC",
                ContinentEnum.EUROPA
        );

        Team savedTeam = dataRetriever.saveTeam(team);

        assertNotNull(savedTeam);
        assertTrue(savedTeam.getId() > 0);
        assertEquals("Test JUnit FC", savedTeam.getName());
    }

    @Test
    void testCreatePlayers() {
        Team team = dataRetriever.findTeamById(1);
        assertNotNull(team);

        Player player = new Player(
                0,
                "JUnit Player",
                22,
                PlayerPositionEnum.MIDF,
                team
        );

        List<Player> created = dataRetriever.createPlayers(List.of(player));

        assertEquals(1, created.size());
        assertTrue(created.get(0).getId() > 0);
    }

    @Test
    void testFindTeamsByPlayerName() {
        List<Team> teams = dataRetriever.findTeamsByPlayerName("Jude");

        assertNotNull(teams);
        assertFalse(teams.isEmpty());
    }

    @Test
    void testFindPlayersByCriteria() {
        List<Player> players = dataRetriever.findPlayersByCriteria(
                "Jude",
                PlayerPositionEnum.MIDF,
                null,
                null,
                0,
                10
        );

        assertNotNull(players);
        assertFalse(players.isEmpty());
    }
}
