package edu.uob;

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
import java.util.List;
import java.util.NoSuchElementException;

public class EntityParser {
    private List<Location> gameLocationList;
    private String filename;

    public EntityParser(String filename) throws NoSuchElementException {
        this.filename = filename;
        gameLocationList = new ArrayList<>();

        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader("config" + File.separator + filename);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            addLocationsToGameLocationList(sections);
            addConnectedLocationsToGameLocations(sections);
            System.out.println(gameLocationList);

        } catch (FileNotFoundException fnfe) {
        } catch (ParseException pe) {
        }
    }

    private void addLocationsToGameLocationList(ArrayList<Graph> sections) {
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        for (int i = 0; i < locations.size(); i++) {
            Location location = getLocation(locations, i);
            gameLocationList.add(location);
        }
    }

    private Location getLocation(ArrayList<Graph> locations, int i) {
        Graph locationElement = locations.get(i);
        ArrayList<Graph> locationSubgraphs = locationElement.getSubgraphs();
        Node locationDetails = locationElement.getNodes(false).get(0);
        // Yes, you do need to get the ID twice !
        String locationName = locationDetails.getId().getId();
        String locationDescription = locationDetails.getAttribute("description");
        // the first location is the start.
        Boolean isStartLocation = (i == 0);

        ArrayList<Item> artifacts = getArtifacts(locationSubgraphs);
        ArrayList<Item> furniture = getFurniture(locationSubgraphs);
        ArrayList<Character> characters = getCharacters(locationSubgraphs);

        return new Location(locationName, locationDescription, isStartLocation,
                artifacts, furniture, characters);
    }

    // todo: the getArtfacts getFurnitue and getCharacters methods are very similar and use a repeated loop
    // fiure out how to optimize.
    private ArrayList<Item> getArtifacts(ArrayList<Graph> locationSubgraphs) {
        ArrayList<Item> artifacts = new ArrayList<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if (locationSubgraph.getId().getId().equals("artefacts")) {
                ArrayList<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId();
                    String description = node.getAttribute("description");
                    Item item = new Item(name, description, true);
                    artifacts.add(item);
                }
            }
        }
        return artifacts;
    }

    private ArrayList<Item> getFurniture(ArrayList<Graph> locationSubgraphs) {
        ArrayList<Item> furniture = new ArrayList<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if (locationSubgraph.getId().getId().equals("furniture")) {
                ArrayList<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId();
                    String description = node.getAttribute("description");
                    Item item = new Item(name, description, false);
                    furniture.add(item);
                }
            }
        }
        return furniture;
    }

    private ArrayList<Character> getCharacters(ArrayList<Graph> locationSubgraphs) {
        ArrayList<Character> characters = new ArrayList<>();
        for (Graph locationSubgraph : locationSubgraphs) {
            if (locationSubgraph.getId().getId().equals("furniture")) {
                ArrayList<Node> nodeList = locationSubgraph.getNodes(false);
                for (Node node : nodeList) {
                    String name = node.getId().getId();
                    String description = node.getAttribute("description");
                    Character character = new Character(name, description);
                    characters.add(character);
                }
            }
        }
        return characters;
    }

    private Location getLocationByName(String name) throws NoSuchElementException {
        for (Location location : gameLocationList) {
            if (location.getName().equals(name)) {
                return location;
            }
        }
        throw new NoSuchElementException("location name " + name + " not found");
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
