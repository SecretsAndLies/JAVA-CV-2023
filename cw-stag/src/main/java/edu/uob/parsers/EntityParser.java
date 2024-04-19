package edu.uob.parsers;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.GameEntities.Character;
import edu.uob.GameEntities.Item;
import edu.uob.GameEntities.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class EntityParser {

    private final HashMap<String, Location> gameLocations;

    private Location startLocation;

    public EntityParser(File file) throws NoSuchElementException {
        gameLocations = new HashMap<>();

        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(file);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            addLocationsToGameLocationList(sections);
            addConnectedLocationsToGameLocations(sections);
        } catch (FileNotFoundException | ParseException e) {
            // todo: does this belong here?
            System.err.println(e.getMessage());
        }
    }

    public HashMap<String, Location> getGameLocations() {
        return gameLocations;
    }

    private void addLocationsToGameLocationList(ArrayList<Graph> sections) {
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        for (int i = 0; i < locations.size(); i++) {
            Location location = getLocation(locations, i);
            gameLocations.put(location.getName(), location);
        }
    }

    public Location getStartLocation() {
        return startLocation;
    }

    private Location getLocation(ArrayList<Graph> locations, int i) {
        Graph locationElement = locations.get(i);
        ArrayList<Graph> locationSubgraphs = locationElement.getSubgraphs();
        Node locationDetails = locationElement.getNodes(false).get(0);
        // Yes, you do need to get the ID twice !
        String locationName = locationDetails.getId().getId();
        String locationDescription = locationDetails.getAttribute("description");
        // the first location is the start.
        boolean isStartLocation = (i == 0);

        HashMap<String, Item> artifacts = getArtifacts(locationSubgraphs);
        HashMap<String, Item> furniture = getFurniture(locationSubgraphs);
        HashMap<String, Character> characters = getCharacters(locationSubgraphs);
        Location location = new Location(locationName, locationDescription, isStartLocation,
                artifacts, furniture, characters);
        if (isStartLocation) {
            startLocation = location;
        }
        return location;
    }


    // todo: the getArtfacts getFurnitue and getCharacters methods are very similar and use a repeated loop
    // fiure out how to optimize.
    private HashMap<String, Item> getArtifacts(ArrayList<Graph> locationSubgraphs) {
        HashMap<String, Item> artifacts = new HashMap<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if (locationSubgraph.getId().getId().equals("artefacts")) {
                ArrayList<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId();
                    String description = node.getAttribute("description");
                    Item item = new Item(name, description, true);
                    artifacts.put(item.getName(), item);
                }
            }
        }
        return artifacts;
    }

    private HashMap<String, Item> getFurniture(ArrayList<Graph> locationSubgraphs) {
        HashMap<String, Item> furniture = new HashMap<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if (locationSubgraph.getId().getId().equals("furniture")) {
                ArrayList<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId();
                    String description = node.getAttribute("description");
                    Item item = new Item(name, description, false);
                    furniture.put(item.getName(), item);
                }
            }
        }
        return furniture;
    }

    private HashMap<String, Character> getCharacters(ArrayList<Graph> locationSubgraphs) {
        HashMap<String, Character> characters = new HashMap<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if (locationSubgraph.getId().getId().equals("characters")) {
                ArrayList<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId();
                    String description = node.getAttribute("description");
                    Character character = new Character(name, description);
                    characters.put(character.getName(), character);
                }
            }
        }
        return characters;
    }

    private Location getLocationByName(String name) throws NoSuchElementException {
        Location location = gameLocations.get(name);
        if (location == null) {
            throw new NoSuchElementException("location name " + name + " not found");
        }
        return location;
    }

    private void addConnectedLocationsToGameLocations(ArrayList<Graph> sections) throws NoSuchElementException {
        ArrayList<Edge> paths = sections.get(1).getEdges();
        for (Edge path : paths) {
            String fromName = path.getSource().getNode().getId().getId();
            String toName = path.getTarget().getNode().getId().getId();
            Location fromLocation = getLocationByName(fromName);
            Location toLocation = getLocationByName(toName);
            fromLocation.addAccessibleLocation(toLocation);
        }
    }
}
