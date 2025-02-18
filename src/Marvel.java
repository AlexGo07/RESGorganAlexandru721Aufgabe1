

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


// Clasa pentru un eveniment
//@Root(name = "log")
class Game {

    public int Id;

    public String Held;

    public String Antagonist;

    public String Ort;

    public String Datum;

    public String Konfrontationstyp;

    public double GlobalerEinfluss;
}

public class Marvel{
    public static List<Game> parseJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<List<Game>>() {});
    }
    /*
        TODO daca vreau sa fac o scriere in json
    public static void writeJson(String filePath, List<Game> games) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filePath), games);
    }
     */


    /*
    //TODO daca vreau sa fac o scriere in XML
    public static void writeXml(String filePath, List<Game> games) throws Exception {
        Persister serializer = new Persister();
        GamesWrapper wrapper = new GamesWrapper();
        wrapper.Games = games;
        serializer.write(wrapper, new File(filePath));
    }

     */


    public static void filterGame(List<Game> games, int kapazitat) {
        games.stream().filter(g -> g.GlobalerEinfluss >= kapazitat).map(g -> g.Held)
                .distinct().forEach(g -> System.out.println(g));
    }




    public static void displayMunichGames(List<Game> Games) {
        Games.stream()
                .filter(e -> e.Konfrontationstyp.equals("Galaktisch"))
                .sorted(Comparator.comparing(g -> g.Datum))
                .forEach(e -> System.out.println(e.Datum + ": " + e.Held + " vs " + e.Antagonist));
    }

    public static void writeGameCounts(String filename, List<Game> Games) {
        Map<String, Integer> GameCounts = new HashMap<>();

        // Numărăm evenimentele pentru fiecare casă
        for (Game Game : Games) {
            GameCounts.merge(Game.Konfrontationstyp, 1, Integer::sum);
        }

        // Sortare: întâi descrescător după număr, apoi alfabetic dacă sunt egale
        List<Map.Entry<String, Integer>> sortedHouseGames = new ArrayList<>(GameCounts.entrySet());
        sortedHouseGames.sort(Comparator
                .<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                .thenComparing(e -> e.getKey()));

        // Scrierea în fișier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : sortedHouseGames) {
                writer.write(entry.getKey() + "&" + entry.getValue() + "$" + GameCounts.get(entry.getKey()) + "\n");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String filePath = "marvel_konfrontationen.json"; // Schimbă în .tsv sau .xml dacă e cazul
        List<Game> Games;
        Scanner scanner = new Scanner(System.in);
        if (filePath.endsWith(".json")) Games = parseJson(filePath);
            //else if (filePath.endsWith(".tsv")) Games = parseTsv(filePath);
            //else if (filePath.endsWith(".xml")) Games = parseXml(filePath);
        else throw new IllegalArgumentException("Format necunoscut");

        System.out.println("Mindeste Kapazitat");
        int kapazitat = scanner.nextInt();
        System.out.println("sub valoarea data " + kapazitat + ":");
        filterGame(Games,kapazitat);

        System.out.println("\n:Galaktische anzeigen:");
        displayMunichGames(Games);

        writeGameCounts("ergebnis.txt",Games);
    }
}


