package core_package;
import core_package.QueryGeneration.DB2PrologLoader;
import core_package.QueryGeneration.Query;
import core_package.QueryGeneration.QueryBuilder;
import core_package.Schema.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.time.Period;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import core_package.SchemaMapper.DatabaseConnection;
import core_package.SchemaMapper.SchemaBuilder;
import org.jpl7.*;

//Created by Jackson Hoagland, Gayatri Krishnan, and Michele Samorani, during academic research with Santa Clara University on 9/29/2017
 
public class Main {
	static ArrayList<Table> tables = new ArrayList<>();
	static ArrayList<Relationship> relationships = new ArrayList<>();
	
	public static void main (String [] args) throws Exception {

        Schema sc = new SchemaBuilder(DatabaseConnection.MICROSOFT_SQL_SERVER).buildSchema().getSchema();
		loadTables();
		JPL.init();
		
		DB2PrologLoader.LoadDB(
				"prolog/functions.pl",
				tables, relationships);
		ArrayList<Query> queries= QueryBuilder.buildQueries("Purchases", 
		"prolog/query templates/to1toN.txt");
		System.out.println("RESULT:");
		for (Query q : queries)
			System.out.println(q.getSQL());
		
		return;
	}

	private static void loadTables() throws Exception {
		Table purchases = new Table("Purchases");
		purchases.addAttribute(new IDAttribute("Purchase_ID"));
		purchases.setPrimaryKey(purchases.getAttributeByName("Purchase_ID"));
		purchases.addAttribute(new IDAttribute("Client_ID"));
		purchases.addAttribute(new IDAttribute("Product_ID"));
		ArrayList<Period> periods = new ArrayList<>();
		periods.add(Period.ofMonths(1));
		periods.add(Period.ofMonths(2));
		periods.add(Period.ofMonths(12));
		purchases.addAttribute(new TimeStampAttribute("date", periods));
		purchases.addAttribute(new ZeroOneAttribute("return"));
		purchases.addAttribute(new ZeroOneAttribute("online"));
		
		Table clients = new Table("Clients");
		clients.addAttribute(new IDAttribute("Client_ID"));
		clients.setPrimaryKey(clients.getAttributeByName("Client_ID"));
		ArrayList<Double> binsAge = new ArrayList<>();
		binsAge.add(0.0); binsAge.add(20.0); binsAge.add(30.0); binsAge.add(40.0); binsAge.add(50.0);
		binsAge.add(60.0); binsAge.add(100000.0); 
		clients.addAttribute(new NumericAttribute("age", "years", binsAge));
		ArrayList<String> gvalues = new ArrayList<>(); gvalues.add("M");gvalues.add("F");
		clients.addAttribute(new NominalAttribute("gender", "gender", gvalues));
		
		Table products = new Table("Products");
		products.addAttribute(new IDAttribute("Product_ID"));
		products.setPrimaryKey(products.getAttributeByName("Product_ID"));
		ArrayList<Double> binsPrice = new ArrayList<>();
		binsPrice.add(0.0); binsPrice.add(20.0); binsPrice.add(100.0); binsPrice.add(200.0);binsPrice.add(1000.0); binsPrice.add(1000000.0);
		products.addAttribute(new NumericAttribute("price", "dollars", binsPrice));

		purchases.addRelationship(new Relationship(purchases, clients, 
				(IDAttribute)purchases.getAttributeByName("Client_ID"), 
				(IDAttribute)clients.getAttributeByName("Client_ID"),RelationshipType.To1));
		clients.addRelationship(new Relationship(clients, purchases, 
				(IDAttribute)clients.getAttributeByName("Client_ID"),
				(IDAttribute)purchases.getAttributeByName("Client_ID"), RelationshipType.ToN));
		purchases.addRelationship(new Relationship(purchases, products, 
				(IDAttribute)purchases.getAttributeByName("Product_ID"), 
				(IDAttribute)products.getAttributeByName("Product_ID"),RelationshipType.To1));
		products.addRelationship(new Relationship(products, purchases, 
				(IDAttribute)products.getAttributeByName("Product_ID"),
				(IDAttribute)purchases.getAttributeByName("Product_ID"),RelationshipType.ToN));
		
		tables.add(purchases);
		tables.add(clients);
		tables.add(products);
		for (Table t : tables) 
			for (Relationship r : t.getRelationships())
				relationships.add(r);
	}

}
