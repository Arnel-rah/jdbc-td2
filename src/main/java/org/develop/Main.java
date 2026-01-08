package org.develop;

import org.develop.classe.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataTest = new DataRetriever();
        System.out.println(dataTest.findTeamById(2));
        Team barea = new Team(10, "Barea", ContinentEnum.AFRICA);
        dataTest.saveTeam(barea);


    }
}