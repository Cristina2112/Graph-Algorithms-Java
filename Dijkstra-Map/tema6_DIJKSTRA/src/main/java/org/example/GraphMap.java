package org.example;

import java.io.File;
import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GraphMap {
    private Map<Integer, Node> nodes = new HashMap<>();
    private Map<Integer, List<Edge>> adjacencyList = new HashMap<>();

    //pt scalare harta
    private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
    private int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

    public void loadFromXML(String filename, int screenWidth, int screenHeight) {
        try {
            File inputFile = new File(filename);
            if(!inputFile.exists()) return;

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("node")) {
                        int id = Integer.parseInt(attributes.getValue("id"));
                        int lon = Integer.parseInt(attributes.getValue("longitude"));
                        int lat = Integer.parseInt(attributes.getValue("latitude"));

                        nodes.put(id, new Node(id, lon, lat));

                        if (lon < minX) minX = lon;
                        if (lon > maxX) maxX = lon;
                        if (lat < minY) minY = lat;
                        if (lat > maxY) maxY = lat;

                    } else if (qName.equalsIgnoreCase("arc")) {
                        int from = Integer.parseInt(attributes.getValue("from"));
                        int to = Integer.parseInt(attributes.getValue("to"));
                        int len = Integer.parseInt(attributes.getValue("length"));

                        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, len));
                    }
                }
            };

            saxParser.parse(inputFile, handler);
            scaleNodes(screenWidth, screenHeight);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scaleNodes(int width, int height) {
        int padding = 30; // margine

        // harta goala evitare impartire la 0
        if (maxX == minX || maxY == minY) return;

        double scaleX = (double)(width - 2 * padding) / (maxX - minX);
        double scaleY = (double)(height - 2 * padding) / (maxY - minY);

        for (Node n : nodes.values()) {
            n.setScreenX(padding + (int)((n.getX() - minX) * scaleX));
            n.setScreenY(height - padding - (int)((n.getY() - minY) * scaleY));
        }
    }

    public Node getNearestNode(int x, int y) {
        Node best = null;
        double minDst = Double.MAX_VALUE;
        for (Node n : nodes.values()) {
            double d = (x - n.getScreenX()) * (x - n.getScreenX()) + (y - n.getScreenY()) * (y - n.getScreenY());
            if (d < minDst) {
                minDst = d;
                best = n;
            }
        }
        //minDist=2500= 50 pixeli => click ignorat
        return (minDst < 2500) ? best : null;
    }

    public Map<Integer, Node> getNodes() { return nodes; }
    public Map<Integer, List<Edge>> getAdjacencyList() { return adjacencyList; }
}