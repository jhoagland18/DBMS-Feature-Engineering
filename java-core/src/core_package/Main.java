package core_package;

import java.util.ArrayList;

//Created by Gayatri Krishnan and Jackson Hoagland on 9/29/2017

public class Main {
	
	public static void main(String args[]) {
		
		Table route = new Table("route"); 
		route.addAttribute("route_ID", true);
		Table flight = new Table ("flight");
		flight.addAttribute("flight_ID", true);
		Table pilot = new Table("pilot");
		pilot.addAttribute("pilot_ID", true);
		Table cabin = new Table("cabin");
		cabin.addAttribute("cabin_ID", true);
		
		
		//route.addRelationship(flight,"flight_ID");
		DBSchema sc = new DBSchema();
		sc.addTable(flight);
		sc.addTable(route);
		sc.addTable(pilot);
		sc.addTable(cabin);
		
		sc.createRelationship(route, flight, route.getAttribute("route_ID"), flight.addForeignKey("route_ID", route));
		sc.createRelationship(flight, pilot, flight.getAttribute("flight_ID"), pilot.addForeignKey("flight_ID", flight));
		sc.createRelationship(pilot, cabin, pilot.getAttribute("pilot_ID"), cabin.addForeignKey("pilot_ID", pilot));
		
		
		ArrayList<Relationship> routeRels = flight.getRelationships();
		for (Relationship r : routeRels ) {
			System.out.println("Route relationships: "+r.getTables()[0]+", "+r.getTables()[1]);
		}
		
		ArrayList<Path> paths = new ArrayList<Path>();
		
		sc.createPaths(null, paths, route, 3);
		AttributeGenerator ag = new AttributeGenerator();

		System.out.println("number of paths: " + paths.size());
		for (Path p : paths) {
			ArrayList<String> queries = ag.generate(p);
			System.out.println(p.toString());
			for (int i = 0; i < queries.size(); i++) {
				System.out.println("query: "+ queries.get(i));
			}
		}
		
		
		
		
		
		//System.out.println(sc.buildPaths(null, route, 3));
		//System.out.println(sc.createPaths(partial, toReturn, targetTable, max_length);
		//Table featureTable = attributeGenerate (sc, p, "target-attribute");
	}

}
