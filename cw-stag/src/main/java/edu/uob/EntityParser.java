package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.GameEntities.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class EntityParser {
    private List<Location> gameLocationList;
    private String filename;
    public EntityParser(String filename){
        this.filename=filename;
        gameLocationList = new ArrayList<>();

        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader("config" + File.separator +filename);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

//            subgraph cluster001 {
//                node [shape = "none"];
//                cabin [description = "A log cabin in the woods"];
//                subgraph artefacts {
//                    node [shape = "diamond"];
//                    axe [description = "A razor sharp axe"];
//                    potion [description = "Magic potion"];
//                }
//                subgraph furniture {
//                    node [shape = "hexagon"];
//                    trapdoor [description = "Wooden trapdoor"];
//                }
//            }
            // The locations will always be in the first subgraph
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            // todo: loop through the locations.
            Graph locationElement = locations.get(0);
            Node locationDetails = locationElement.getNodes(false).get(0);
            // Yes, you do need to get the ID twice !
            String locationName = locationDetails.getId().getId();
            String locationDescription = locationDetails.getAttribute("description");
            Boolean isStartLocation = true; // todo: add logic.
            Location location = new Location(locationName,locationDescription);
            //  artefacts
            // furniture.

//            subgraph paths {
//                cabin -> forest;
//                forest -> cabin;
//                cellar -> cabin;
//            }

            // The paths will always be in the second subgraph
            ArrayList<Edge> paths = sections.get(1).getEdges();
            // todo: loop through the paths
            Edge path = paths.get(0);
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId();
            System.out.println("path "+fromName + ">"+toName);

        } catch (FileNotFoundException fnfe) {
        } catch (ParseException pe) {
        }
    }

}
