package com.napier.groupF;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
import java.sql.*;
import java.util.*;

public class App {

    public static void main(String[] args) {
        // Create new Application
        com.napier.groupF.App a = new com.napier.groupF.App();
        // Connect to database
        a.connect("localhost:33060");
        // the following statements are to test the queries and should later be replaced with user input
        //Get country
        Country c = a.getCountry("France");
        ArrayList<Country> countries = a.listCountries("Europe");
        //Get top Countries
        ArrayList<Country> topCountriesWorld = a.topCountriesWorld();
        //Get Capital
        ArrayList<Capital> WorldCapitals = a.getWorldCapital();
        ArrayList<Capital> ContinentCapitals = a.getContinentCapital("Europe");
        ArrayList<Capital> RegionCapitals = a.getRegionCapital("Southern Europe");
        ArrayList<Capital> TopRegionCapitals = a.TopRegionCapital(3,"Southern Europe");
        ArrayList<Capital> TopContinentCapitals = a.TopContinentCapital(3,"europe");
        ArrayList<Capital> TopWorldCapitals = a.TopWorldCapital(3);
        // Display results
       // a.displayCapitals(WorldCapitals);
       // a.displayCapitals(ContinentCapitals);
        a.displayCapitals(TopContinentCapitals);
        a.displayCapitals(TopWorldCapitals);
        //a.displayCountry(c);
        a.displayCountries(topCountriesWorld);
        // get cities
        ArrayList<City> cities = a.listCities("Europe");
        // Display results
        a.displayCities(cities);
        // Get top cities
        ArrayList<City> topCitiesWorld = a.topCitiesWorld(10);
        ArrayList<City> topCitiesContinent = a.topCitiesContinent(10, "Europe");
        ArrayList<City> topCitiesRegion = a.topCitiesRegion(10, "Southern Europe");
        ArrayList<City> topCitiesCountry = a.topCitiesCountry(10, "United Kingdom");
        ArrayList<City> topCitiesDistrict = a.topCitiesDistrict(10, "England");
        // Display areas populations
        a.displayCities(topCitiesContinent);
        ArrayList<Population> areasPopulation = a.areasPopulation();
        a.displayAreasPopulation(areasPopulation);
        // Disconnect from database
        a.disconnect();
    }

    /**
     * Connection to MySQL database.
     */

    private Connection con = null;
    /**
     * Connect to the MySQL database.
     */
    public void connect(String location) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }
        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000);
                //Connect to database locally
                con = DriverManager.getConnection("jdbc:mysql://" + location + "/world?useSSL=true", "root", "example");
                // Connect to database inside docker
               // con = DriverManager.getConnection("jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public Country getCountry(String name) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT  Name, Code, Region, Population " +
                    "FROM country " +
                    "WHERE Name = '" + name + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            //Return country if valid
            //Check one is returned
            if (rset.next()) {
                Country c = new Country();
                c.Name = rset.getString("Name");
                c.Code = rset.getString("Code");
                c.Region = rset.getString("Region");
                c.Population = rset.getInt("Population");
                return c;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get Country details");
            return null;
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public City getCity(String name) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT  Name, CountryCode, District, Population " +
                    "FROM city " +
                    "WHERE Name = '" + name + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            //Return country if valid
            //Check one is returned
            if (rset.next()) {
                City c = new City();
                c.Name = rset.getString("Name");
                c.CountryCode = rset.getString("CountryCode");
                c.District = rset.getString("District");
                c.Population = rset.getInt("Population");
                return c;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get City details");
            return null;
        }
    }

    /**
     *
     * @param continent
     * @return
     */
    public ArrayList<Country> listCountries(String continent) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT country.Name, country.Code, country.Region, country.Population " +
                            "FROM country " +
                            "WHERE country.Continent = '" + continent + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<Country> countries = new ArrayList<Country>();
            while (rset.next()) {
                Country co = new Country();
                co.Name = rset.getString("country.name");
                co.Code = rset.getString("country.Code");
                co.Region = rset.getString("country.Region");
                co.Population = rset.getInt("country.Population");
                countries.add(co);
            }
            return countries;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @return
     */
    public ArrayList<Country> topCountriesWorld(){//(Integer topX) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT country.Name, country.Code, country.Region, country.Population " +
                    "FROM country " +
                    "ORDER BY country.Population DESC "
                   ;// "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract country information
            ArrayList<Country> countries = new ArrayList<Country>();
            while (rset.next()) {
                Country co = new Country();
                co.Name = rset.getString("country.name");
                co.Code = rset.getString("country.Code");
                co.Region = rset.getString("country.Region");
                co.Population = rset.getInt("country.Population");
                countries.add(co);
            }
            return countries;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }

    /**
     *
     * @param countries
     */
    public void displayCountries(ArrayList<Country> countries) {
        // Check cities is not null
        if (countries == null) {
            System.out.println("No countries");
            return;
        }
        // Print header
        System.out.println("Countries List");
        // Loop over all cities in the list
//        for (Country co : countries) {
//            String country_string = ("Name: " + co.Name + "\n" +
//                    "Code: " + co.Code + "\n" +
//                    "Region: " + co.Region + "\n" +
//                    "Population: " + co.Population);
//            System.out.println(country_string);
            //        Print a comma separated list of the cities
            System.out.println("Name,Code,Region,Population");
            for (Country co : countries){
                String country_string = co.Name + "," + co.Code + "," + co.Region + "," + co.Population;
                System.out.println(country_string);
        }
    }

    /**
     *
     * @param c
     */
    public void displayCountry(Country c) {
        if (c != null) {
            System.out.println("Name: " + c.Name + "\n" +
                    "Country: " + c.Code + "\n" +
                    "Region: " + c.Region + "\n" +
                    "Population: " + c.Population);
        }
    }

    /**
     *
     * @param continent
     * @return
     */
    public ArrayList<City> listCities(String continent) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, city.CountryCode, city.District, city.Population " +
                            "FROM city JOIN country ON city.CountryCode = country.Code " +
                            "WHERE country.Continent = '" + continent + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<City> cities = new ArrayList<City>();
            while (rset.next()) {
                City c = new City();
                c.Name = rset.getString("city.Name");
                c.CountryCode = rset.getString("city.CountryCode");
                c.District = rset.getString("city.District");
                c.Population = rset.getInt("city.Population");
                cities.add(c);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @return
     */
    public ArrayList<City> topCitiesWorld(Integer topX) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city JOIN country ON city.CountryCode = country.Code " +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<City> cities = new ArrayList<City>();
            while (rset.next()) {
                City c = new City();
                c.Name = rset.getString("city.Name");
                c.CountryCode = rset.getString("city.CountryCode");
                c.District = rset.getString("city.District");
                c.Population = rset.getInt("city.Population");
                cities.add(c);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @param continent
     * @return
     */
    public ArrayList<City> topCitiesContinent(Integer topX, String continent) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city JOIN country ON city.CountryCode = country.Code " +
                    "WHERE country.Continent = '" + continent + "'" +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<City> cities = new ArrayList<City>();
            while (rset.next()) {
                City c = new City();
                c.Name = rset.getString("city.Name");
                c.CountryCode = rset.getString("city.CountryCode");
                c.District = rset.getString("city.District");
                c.Population = rset.getInt("city.Population");
                cities.add(c);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @param region
     * @return
     */
    public ArrayList<City> topCitiesRegion(Integer topX, String region) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city JOIN country ON city.CountryCode = country.Code " +
                    "WHERE country.Region = '" + region + "'" +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<City> cities = new ArrayList<City>();
            while (rset.next()) {
                City c = new City();
                c.Name = rset.getString("city.Name");
                c.CountryCode = rset.getString("city.CountryCode");
                c.District = rset.getString("city.District");
                c.Population = rset.getInt("city.Population");
                cities.add(c);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @param country
     * @return
     */
    public ArrayList<City> topCitiesCountry(Integer topX, String country) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city JOIN country ON city.CountryCode = country.Code " +
                    "WHERE country.Name = '" + country + "'" +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<City> cities = new ArrayList<City>();
            while (rset.next()) {
                City c = new City();
                c.Name = rset.getString("city.Name");
                c.CountryCode = rset.getString("city.CountryCode");
                c.District = rset.getString("city.District");
                c.Population = rset.getInt("city.Population");
                cities.add(c);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @param district
     * @return
     */
    public ArrayList<City> topCitiesDistrict(Integer topX, String district) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, city.CountryCode, city.District, city.Population " +
                    "FROM city JOIN country ON city.CountryCode = country.Code " +
                    "WHERE city.District = '" + district + "'" +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<City> cities = new ArrayList<City>();
            while (rset.next()) {
                City c = new City();
                c.Name = rset.getString("city.Name");
                c.CountryCode = rset.getString("city.CountryCode");
                c.District = rset.getString("city.District");
                c.Population = rset.getInt("city.Population");
                cities.add(c);
            }
            return cities;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    /**
     *
     * @param cities
     */
    public void displayCities(ArrayList<City> cities) {
        // Check cities is not null
        if (cities == null) {
            System.out.println("No cities");
            return;
        }
        // Print header
        System.out.println("Cities List");
        // Print a list of the cities
//        for (City c : cities) {
//            String city_string = ("Name: " + c.Name + "\n" +
//                    "Country: " + c.CountryCode + "\n" +
//                    "District: " + c.District + "\n" +
//                    "Population: " + c.Population);
//            System.out.println(city_string);
//        Print a comma separated list of the cities
        System.out.println("Name,Country,District,Population");
        for (City c : cities){
            String city_string = c.Name + "," + c.CountryCode + "," + c.District + "," + c.Population;
            System.out.println(city_string);

        }
    }



    public Capital getCapital(String country) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, country.Name, city.Population " +
                    "FROM city JOIN country ON city.ID = country.Capital " +
                    "WHERE country.Name = '" + country + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            if (rset.next()) {
                Capital ca = new Capital();
                ca.Name = rset.getString("city.Name");
                ca.Country = rset.getString("country.Name");
                ca.Population = rset.getInt("city.Population");
                return ca;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get Capital details");
            return null;
        }
    }

     /**
     *
     * @param
     * @return
     *Get Capital Cities of the world by population
     */
    public ArrayList<Capital> getWorldCapital() {
        try {
              // Create an SQL statement
              Statement stmt = con.createStatement();
             // Create string for SQL statement
              String strSelect = "SELECT city.Name, country.Name, city.Population " +
                     "FROM city JOIN country ON city.ID = country.Capital " +
                     "ORDER BY city.Population DESC " ;
              // Execute SQL statement
             ResultSet rset = stmt.executeQuery(strSelect);
              // Extract city information
            ArrayList<Capital> capitals = new ArrayList<Capital>();
              while (rset.next()) {
                  Capital ca = new Capital();
                  ca.Name = rset.getString("city.Name");
                  ca.Country = rset.getString("country.Name");
                  ca.Population = rset.getInt("city.Population");
                  capitals.add(ca);
              }
             return capitals;
          } catch (Exception e) {
              System.out.println(e.getMessage());
              System.out.println("Failed to get capital details");
              return null;
         }
    }

    /**
     *
     * @param continent
     * @return
     */

    public ArrayList<Capital> getContinentCapital(String continent) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, country.Name, city.Population " +
                    "FROM city JOIN country ON city.ID = country.Capital " +
                    "WHERE country.Continent = '" + continent + "'" +
                    "ORDER BY city.Population DESC " ;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<Capital> capitals = new ArrayList<Capital>();
            while (rset.next()) {
                Capital cap = new Capital();
                cap.Name = rset.getString("city.Name");
                cap.Country = rset.getString("country.Name");
                cap.Population = rset.getInt("city.Population");
                capitals.add(cap);
            }
            return capitals;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital details");
            return null;
        }
    }

    /**
     *
     * @param region
     * @return
     */
    public ArrayList<Capital> getRegionCapital(String region) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, country.Name, city.Population " +
                    "FROM city JOIN country ON city.ID = country.Capital " +
                    "WHERE country.Region = '" + region + "'" +
                    "ORDER BY city.Population DESC " ;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<Capital> capitals = new ArrayList<Capital>();
            while (rset.next()) {
                Capital cap = new Capital();
                cap.Name = rset.getString("city.Name");
                cap.Country = rset.getString("country.Name");
                cap.Population = rset.getInt("city.Population");
                capitals.add(cap);
            }
            return capitals;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital details");
            return null;
        }
    }


    /**
     *
     * @param topX
     * @param region
     * @return
     */
    public ArrayList<Capital> TopRegionCapital(Integer topX, String region) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, country.Name, city.Population " +
                    "FROM city JOIN country ON city.ID = country.Capital " +
                    "WHERE country.Region = '" + region + "'" +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<Capital> capitals = new ArrayList<Capital>();
            while (rset.next()) {
                Capital cap = new Capital();
                cap.Name = rset.getString("city.Name");
                cap.Country = rset.getString("country.Name");
                cap.Population = rset.getInt("city.Population");
                capitals.add(cap);
            }
            return capitals;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @param continent
     * @return
     */
    public ArrayList<Capital> TopContinentCapital(Integer topX, String continent) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, country.Name, city.Population " +
                    "FROM city JOIN country ON city.ID = country.Capital " +
                    "WHERE country.Continent = '" + continent + "'" +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<Capital> capitals = new ArrayList<Capital>();
            while (rset.next()) {
                Capital cap = new Capital();
                cap.Name = rset.getString("city.Name");
                cap.Country = rset.getString("country.Name");
                cap.Population = rset.getInt("city.Population");
                capitals.add(cap);
            }
            return capitals;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital details");
            return null;
        }
    }

    /**
     *
     * @param topX
     * @return
     */
    public ArrayList<Capital> TopWorldCapital(Integer topX) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "SELECT city.Name, country.Name, city.Population " +
                    "FROM city JOIN country ON city.ID = country.Capital " +
                    "ORDER BY city.Population DESC " +
                    "LIMIT " + topX;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<Capital> capitals = new ArrayList<Capital>();
            while (rset.next()) {
                Capital cap = new Capital();
                cap.Name = rset.getString("city.Name");
                cap.Country = rset.getString("country.Name");
                cap.Population = rset.getInt("city.Population");
                capitals.add(cap);
            }
            return capitals;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital details");
            return null;
        }
    }
    /**
     *
     * @param capitals
     */
    public void displayCapitals(ArrayList<Capital> capitals) {
        // Check capitals is not null
        if (capitals == null) {
            System.out.println("No capitals");
            return;
        }
        // Print header
        System.out.println("Capitals List");

//        Print a comma separated list of the cities
        System.out.println("Name,Country,Population");
        for (Capital ca : capitals){
            String capital_string = ca.Name + "," + ca.Country +  "," + ca.Population;
            System.out.println(capital_string);

        }
    }

    /**
     * @param populations in different areas
     */
    public void displayAreasPopulation(ArrayList<Population> areasPopulation) {


        // Check capitals is not null
        if (areasPopulation == null) {
            System.out.println("No population");
            return;
        }
        // Print header
        System.out.println("Populations List");
        // Print a comma separated list of the cities
        System.out.println("Area Type,Area Name,Population");
        for (Population pop : areasPopulation){
            String capital_string = pop.areaKind + "," + pop.name +  "," + pop.population;
            System.out.println(capital_string);
        }
    }

    public ArrayList<Population> areasPopulation() {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect = "" +
                    "SELECT 'world' as kind, 'World' as name, sum(population )  as population from country " +
                    "UNION ALL " +
                    "SELECT 'continent' as kind, continent as name, sum(population )  as population from country group by continent" +
                    "UNION ALL " +
                    "SELECT 'region' as kind, region as name, sum(population ) as population  from country group by region" +
                    "UNION ALL " +
                    "SELECT 'country' as kind, country as name, population as population from country " +
                    "UNION ALL " +
                    "SELECT 'district' as kind, district as name, sum(population ) as population from city group by district" +
                    "UNION ALL " +
                    "SELECT 'city' as kind, name as name, sum(population ) as population from city ;";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract city information
            ArrayList<Population> pops = new ArrayList();
            while (rset.next()) {
                Population pop = new Population();
                pop.areaKind = rset.getString("kind");
                pop.name = rset.getString("name");
                pop.population = rset.getInt("population");
                pops.add(pop);
            }
            return pops;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get Populations report");
            return null;
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }
}
