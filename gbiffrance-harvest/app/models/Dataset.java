package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@Entity
public class Dataset extends Model 
{
  public String name;
  public String url;
  public String type;
  public boolean done;
  
  public Dataset() {}
  
  public Dataset(String name, String url, String type)
  {
	this.name = name;
	this.url = url;
	this.type = type; 
	this.done = false;
  }
  
  public boolean markDataset()
  {
		this.done = true;
		return true;
  }
}


