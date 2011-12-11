package controllers;

import play.*;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.*;
import play.test.Fixtures;

import java.sql.SQLException;
import java.util.*;

import models.occurrence.taxonomy.Taxonomy;
import models.occurrence.harvest.*;
import models.occurrence.harvest.Harvester;
import models.occurrence.harvest.biocase.*;
import models.*;

public class Application extends Controller {

	@OnApplicationStart
	public class Bootstrap extends Job {
		@SuppressWarnings("deprecation")
		public void doJob() {
	          // Check if the database is empty
			  if(Dataset.count() == 0) {
	              Fixtures.load("initial-data.yml");
	          }
	    }
	}
	
	/*
	 * Renders the available datasets
	 */
	public static void index() 
    {
		List<Dataset> datasets = Dataset.all().fetch();
		render(datasets);
    }
	
	/*
	 * Harvest the dataset related to the given id. One done, marks the dataset as done.
	 */
	public static void harvest(Long id) 
    {
    	String targetDirectory = "/tmp";
    	Dataset dataset = Dataset.findById(id);
    	Harvester app;
    	
    	if (dataset.type.equals("ipt"))
    	{
    		app = new models.occurrence.harvest.ipt.Harvester(dataset, targetDirectory);
    		app.run();
    		System.out.println(Occurrence.count());
    	}
		if (dataset.type.equals("biocase"))
    	{
    		app = new models.occurrence.harvest.biocase.Harvester(dataset, targetDirectory);
    		app.run();
    		System.out.println(Occurrence.count());
    	}
		if (dataset.type.equals("tapir"))
    	{
    		app = new models.occurrence.harvest.tapir.Harvester(dataset, targetDirectory);
    		app.run();
    		System.out.println(Occurrence.count());
    	}
		if (dataset.type.equals("digir"))
    	{
    		app = new models.occurrence.harvest.digir.Harvester(dataset, targetDirectory);
    		app.run();
    		System.out.println(Occurrence.count());
    	}
		dataset.markDataset();
		dataset.save();
		index();
    }
	
	/*
	 * Generates the taxonomical classification for a given dataset. 
	 * Gives a "flag" to each occurrence depending if has been linked to a Catalog of life entry
	 */
	public static void taxonomy() throws SQLException 
    {
		List<Occurrence> occurrences = Occurrence.findAll();
		@SuppressWarnings("unused")
		Taxonomy taxonomy = new Taxonomy(occurrences);
		System.out.println("done!");
    }

}