package edu.uob.parsers;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.game_entities.Character;
import edu.uob.game_entities.Item;
import edu.uob.game_entities.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class EntityParser {

    private final Map<String, Location> gameLocations;


    private final Map<String, Item> gameArtifacts;
    private final Map<String, Character> gameCharacters;
    private final Map<String, Item> gameFurniture;

    private Location startLocation;

    public EntityParser(File file) throws NoSuchElementException {
        gameLocations = new HashMap<>();
        gameArtifacts = new HashMap<>();
        gameCharacters = new HashMap<>();
        gameFurniture = new HashMap<>();

        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(file);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            List<Graph> sections = wholeDocument.getSubgraphs();
            addLocationsToGameLocationList(sections);
            addConnectedLocationsToGameLocations(sections);
        } catch (FileNotFoundException | ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    public Map<String, Item> getGameArtifacts() {
        return gameArtifacts;
    }

    public Map<String, Character> getGameCharacters() {
        return gameCharacters;
    }

    public Map<String, Item> getGameFurniture() {
        return gameFurniture;
    }

    public Map<String, Location> getGameLocations() {
        return gameLocations;
    }

    public boolean isEntity(String word) {
        if (getGameArtifacts().containsKey(word)) {
            return true;
        }
        if (getGameCharacters().containsKey(word)) {
            return true;
        }
        if (getGameFurniture().containsKey(word)) {
            return true;
        }
        return getGameLocations().containsKey(word);
    }

    private void addLocationsToGameLocationList(List<Graph> sections) {
        List<Graph> locations = sections.get(0).getSubgraphs();
        for (int i = 0; i < locations.size(); i++) {
            Location location = getLocation(locations, i);
            gameLocations.put(location.getName(), location);
        }
    }

    public Location getStartLocation() {
        return startLocation;
    }

    private Location getLocation(List<Graph> locations, int i) {
        Graph locationElement = locations.get(i);
        List<Graph> locationSubgraphs = locationElement.getSubgraphs();
        Node locationDetails = locationElement.getNodes(false).get(0);
        // Yes, you do need to get the ID twice !
        String locationName = locationDetails.getId().getId();
        String locationDescription = locationDetails.getAttribute(
                "description");
        // the first location is the start.
        boolean isStartLocation = i == 0;

        Map<String, Item> artifacts = getArtifacts(locationSubgraphs);
        Map<String, Item> furniture = getFurniture(locationSubgraphs);
        Map<String, Character> characters = getCharacters(locationSubgraphs);
        addAllEntitesToMainEntiesList(artifacts, furniture, characters);
        Location location = new Location(locationName, locationDescription,
                artifacts, furniture, characters);
        if (isStartLocation) {
            startLocation = location;
        }
        return location;
    }

    private void addAllEntitesToMainEntiesList(Map<String, Item> artifacts,
                                               Map<String, Item> furniture,
                                               Map<String, Character> characters) {
        this.gameCharacters.putAll(characters);
        this.gameFurniture.putAll(furniture);
        this.gameArtifacts.putAll(artifacts);
    }


    // todo: the getArtfacts getFurnitue and getCharacters methods are very similar and use a repeated loop
    // fiure out how to optimize.
    private Map<String, Item> getArtifacts(List<Graph> locationSubgraphs) {
        Map<String, Item> artifacts = new HashMap<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if ("artefacts".equals(locationSubgraph.getId().getId())) {
                List<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId().toLowerCase();
                    String description = node.getAttribute("description");
                    Item item = new Item(name, description, true);
                    artifacts.put(item.getName(), item);
                }
            }
        }
        return artifacts;
    }

    private Map<String, Item> getFurniture(List<Graph> locationSubgraphs) {
        Map<String, Item> furniture = new HashMap<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if ("furniture".equals(locationSubgraph.getId().getId())) {
                List<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId().toLowerCase();
                    String description = node.getAttribute("description");
                    Item item = new Item(name, description, false);
                    furniture.put(item.getName(), item);
                }
            }
        }
        return furniture;
    }

    private Map<String, Character> getCharacters(
            List<Graph> locationSubgraphs) {
        Map<String, Character> characters = new HashMap<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if ("characters".equals(locationSubgraph.getId().getId())) {
                List<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId().toLowerCase();
                    String description = node.getAttribute("description");
                    Character character = new Character(name, description);
                    characters.put(character.getName(), character);
                }
            }
        }
        return characters;
    }

    private Location getLocationByName(String name) throws
            NoSuchElementException {
        Location location = gameLocations.get(name);
        if (location == null) {
            throw new NoSuchElementException(
                    "location name " + name + " not found");
        }
        return location;
    }

    private void addConnectedLocationsToGameLocations(
            List<Graph> sections) throws NoSuchElementException {
        List<Edge> paths = sections.get(1).getEdges();
        for (Edge path : paths) {
            String fromName = path.getSource().getNode().getId().getId();
            String toName = path.getTarget().getNode().getId().getId();
            Location fromLocation = getLocationByName(fromName);
            Location toLocation = getLocationByName(toName);
            fromLocation.addAccessibleLocation(toLocation);
        }
    }
}
