package fjdb.familyfortunes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Rounds {

    public static List<Round> loadAllRounds() {
        ArrayList<Round> rounds = new ArrayList<>();
        rounds.add(getSoccer1979());
        rounds.add(getUndress1979());
        rounds.add(getHoppingAnimal1979());
        rounds.add(getDrink1983());
        rounds.add(getOccupation1983());
        rounds.add(getSwitchOn1983());
        rounds.add(getImpressions1995());
        rounds.add(getRumbles1995());
        rounds.add(getLabels1995());
        rounds.add(getBoxer2000());
        rounds.add(getRacecource2000());
        rounds.add(getShoesOff2000());
        rounds.add(getFlower2012());
        rounds.add(getGreen2012());
        rounds.add(getHusband2012());
        rounds.add(getEngineTravel2012());
        rounds.add(getDance2020());
        rounds.add(getSpeech2020());
        return rounds;
    }

    public static List<Round> loadRounds() {
        ArrayList<Round> rounds = new ArrayList<>();
        rounds.add(getSoccer1979());
// //       rounds.add(getUndress1979());
//        rounds.add(getHoppingAnimal1979());
//        rounds.add(getDrink1983());
        rounds.add(getOccupation1983());
//        rounds.add(getSwitchOn1983());
//        rounds.add(getImpressions1995());
        rounds.add(getRumbles1995());
//        rounds.add(getLabels1995());
//        rounds.add(getBoxer2000());
        rounds.add(getRacecource2000());
//        rounds.add(getShoesOff2000());
//        rounds.add(getFlower2012());
        rounds.add(getGreen2012());
//        rounds.add(getHusband2012());
//        rounds.add(getEngineTravel2012());
//        rounds.add(getDance2020());
        rounds.add(getSpeech2020());
        return rounds;
    }

    public static List<Round> loadRounds2() {
        ArrayList<Round> rounds = new ArrayList<>();
// //       rounds.add(getUndress1979());
        rounds.add(getHoppingAnimal1979());
//        rounds.add(getDrink1983());
        rounds.add(getSwitchOn1983());
//        rounds.add(getImpressions1995());
        rounds.add(getLabels1995());
        rounds.add(getBoxer2000());
//        rounds.add(getShoesOff2000());
//        rounds.add(getFlower2012());
        rounds.add(getHusband2012());
        rounds.add(getEngineTravel2012());
        rounds.add(getDance2020());
        return rounds;
    }

    public static List<Round> loadRoundsMonday() {
        ArrayList<Round> rounds = new ArrayList<>();
        rounds.add(getSoccer1979());
        rounds.add(getUndress1979());
//        rounds.add(getHoppingAnimal1979());
//        rounds.add(getDrink1983());
        rounds.add(getOccupation1983());
        rounds.add(getSwitchOn1983());
        rounds.add(getRumbles1995());
        rounds.add(getImpressions1995());
//        rounds.add(getLabels1995());
//        rounds.add(getRacecource2000());
        rounds.add(getShoesOff2000());
        rounds.add(getBoxer2000());
//        rounds.add(getFlower2012());
        rounds.add(getEngineTravel2012());
        rounds.add(getGreen2012());
//        rounds.add(getHusband2012());
        rounds.add(getSpeech2020());
        rounds.add(getDance2020());
        return rounds;
    }

    private static Round getSoccer1979() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("WHISTLE", 71);
        answers.put("NOTEBOOK", 9);
        answers.put("STOPWATCH", 7);
        answers.put("COIN", 3);
        answers.put("BALL", 3);
        return new Round(1979, "Name something a soccer referee takes onto the pitch", answers);
    }

    private static Round getHoppingAnimal1979() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("KANGAROO", 73);
        answers.put("RABBIT", 11);
        answers.put("FROG", 4);
        answers.put("FLEA", 3);
        return new Round(1979, "Name an animal that hops", answers);
    }

    private static Round getUndress1979() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("BED", 43);
        answers.put("B/SHOWER", 31);
        answers.put("SWIM", 14);
        answers.put("SEX", 9);
        return new Round(1979, "Name a reason for which people undress", answers);
    }

    private static Round getOccupation1983() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("COAL MINER", 20);
        answers.put("LOO ATTENDANT", 13);
        answers.put("DUSTMAN", 10);
        answers.put("DEEP SEA DIVER", 7);
        answers.put("STEEPLE JACK", 7);
        answers.put("SEWAGE WORKER", 6);
        answers.put("CALL GIRL", 5);
        return new Round(1983, "Name an occupation you would refuse to do", answers);
    }

    private static Round getDrink1983() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("TEA", 24);
        answers.put("GIN", 12);
        answers.put("LAGER/BEER", 12);
        answers.put("COFFEE", 10);
        answers.put("WHISKEY", 9);
        answers.put("FRUIT JUICE", 4);
        answers.put("WINE", 4);
        return new Round(1983, "name your favourite drink", answers);
    }

    private static Round getSwitchOn1983() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("LIGHT", 24);
        answers.put("T.V.", 19);
        answers.put("RADIO", 13);
        answers.put("VACUUM CLEANER", 8);
        answers.put("CAR", 7);
        answers.put("ELECTRIC FIRE", 5);
        answers.put("MIXER/BLENDER", 4);
        answers.put("WASHING MACHINE", 3);
        return new Round(1983, "name something you switch on", answers);
    }

    private static Round getRacecource2000() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("JOCKEY", 49);
        answers.put("BOOKIE", 29);
        answers.put("TIC TAC MAN", 13);
        answers.put("PUNTER", 4);
        answers.put("TIPSTER", 3);
        answers.put("TRAINER", 2);
        return new Round(2000, "Name someone you’d see at a racecourse", answers);
    }

    private static Round getShoesOff2000() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("PADDLE", 28);
        answers.put("SWIM", 21);
        answers.put("SLEEP", 18);
        answers.put("RELAX", 13);
        answers.put("SOAK FEET", 8);
        answers.put("CUT NAILS", 6);
        return new Round(2000, "Name something you’d do with your shoes off", answers);
    }

    private static Round getBoxer2000() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("CUT EYE", 41);
        answers.put("BROKEN NOSE", 40);
        answers.put("THICK LIP", 11);
        answers.put("CAULI EAR", 6);
        answers.put("BROKEN TEETH", 2);
        return new Round(2000, "Name a Facial injury sported by a boxer", answers);
    }

    private static Round getRumbles1995() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("STOMACH", 52);
        answers.put("THUNDER", 18);
        answers.put("VOLCANO", 12);
        answers.put("EARTHQUAKE", 8);
        answers.put("WASHING MACHINE", 5);
        answers.put("SPIN DRIER", 3);
        answers.put("TUBE TRAIN", 2);
        return new Round(1995, "Name something that rumbles", answers);
    }

    private static Round getImpressions1995() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("FRANK SPENCER", 19);
        answers.put("M. THATCHER", 17);
        answers.put("JOHN MAJOR", 15);
        answers.put("TOMMY COOPER", 10);
        answers.put("PRINCE CHARLES", 9);
        answers.put("BRUCE FORSYTH", 8);
        answers.put("CLIFF RICHARD", 6);
        answers.put("MAVIS", 6);
        return new Round(1995, "Name someone impressionists often impersonate", answers);
    }

    private static Round getLabels1995() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("LUGGAGE/CASES", 31);
        answers.put("PARCELS", 19);
        answers.put("JARS", 15);
        answers.put("TIN CANS", 10);
        answers.put("CAR WINDOWS", 8);
        answers.put("BOTTLES", 5);
        return new Round(1995, "Name something people stick labels on", answers);
    }

    private static Round getEngineTravel2012() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("BICYCLE", 42);
        answers.put("YACHT", 18);
        answers.put("RUN/WALK", 11);
        answers.put("GLIDER", 10);
        answers.put("HOT AIR BALLOON", 8);
        answers.put("HORSE & CART", 4);

        return new Round(2012, "Name a way of travelling a long distance without an engine", answers);
    }

    private static Round getHusband2012() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("HUBBY", 33);
        answers.put("OLD MAN", 18);
        answers.put("OTHER/BETTER HALF", 17);
        answers.put("SPOUSE", 14);
        answers.put("PARTNER", 5);
        answers.put("FELLA", 3);
        return new Round(2012, "Name another word for husband", answers);
    }

    private static Round getFlower2012() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("DAFFODIL", 48);
        answers.put("CROCUS", 12);
        answers.put("TULIP", 11);
        answers.put("BLUEBELL", 8);
        answers.put("SNOWDROP", 7);
        answers.put("DAISY", 6);

        return new Round(2012, "Name a flower you see in the spring", answers);
    }

    private static Round getGreen2012() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("INCREDIBLE HULK", 29);
        answers.put("JOLLY GREEN GIANT", 23);
        answers.put("KERMIT", 19);
        answers.put("SHREK", 10);
        answers.put("STATUE OF LIBERTY", 5);
        return new Round(2012, "Name a famous figure who is green", answers);
    }

    private static Round getSpeech2020() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("MEMORY LAPSE", 29);
        answers.put("BREAK WIND", 17);
        answers.put("BURP", 10);
        answers.put("STUTTER", 8);
        answers.put("COUGH/SNEEZING FIT", 7);
        answers.put("PANTS FALL/OPEN FLY", 6);
        answers.put("EXCESSIVE SWEATING", 4);
        answers.put("FAINT", 3);
        return new Round(2020, "Name something embarrassing that can happen to a person making a speech", answers);
    }

    private static Round getDance2020() {
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        answers.put("WALTZ", 28);
        answers.put("CHICKEN DANCE", 22);
        answers.put("MACARENA", 9);
        answers.put("SLOW DANCE", 7);
        answers.put("CONGA", 4);
        answers.put("POLKA", 3);
        answers.put("YMCA", 2);
        answers.put("LINE DANCE", 2);

        return new Round(2020, "Name a dance that everyone does at a Canadian wedding", answers);
    }

    public static void main(String[] args) {
        List<Round> rounds = loadRounds();
        for (Round round : rounds) {
            List<String> answers = round.getAnswers();
            for (String answer : answers) {
                if (answer.length() > 15) {
                    System.out.println(String.format("%s %s", round.getQuestion(), answer));
                }
            }
        }
    }

}
