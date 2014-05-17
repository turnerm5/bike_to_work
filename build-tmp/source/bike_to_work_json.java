import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Comparator; 
import java.util.Collections; 
import java.util.Iterator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class bike_to_work_json extends PApplet {





PFont f;
PImage bikePhoto;

JSONObject saa, saaResults;
JSONArray saaValues;

String lastRun;

Buttons buttons;
ArrayList<Employee> employees;

int rowCount;
int newRank;
int padding = 22;
int fontSize = 20;
int headingSize = 20;
int idleCount = 0;
int leading = PApplet.parseInt(fontSize * 1.25f);

int sortSwitch = 0;

int[] eddy = {0xff5C2849, 0xffA73E5C, 0xffEC7263, 0xffFE9551, 0xffFFD285};
int[] breath = {0xffD14B2D, 0xff414D4A, 0xff748277, 0xff96BDAF, 0xff827A67};
int[] palette = breath;

public void setup() {
	size(400, 700);
	
	smooth();

	background(palette[1]);

	bikePhoto = loadImage("bike.png");

	saa = loadJSONObject("http://www.kimonolabs.com/api/3r9s4496?apikey=9adbc380301d558ba05315f79fff2e31");
	saaResults = saa.getJSONObject("results");
	saaValues = saaResults.getJSONArray("values");

	lastRun = saa.getString("lastsuccess");
	println("API Refreshed: " + lastRun);

	buttons = new Buttons();

	employees = new ArrayList<Employee>();

	f = createFont("Verdana", fontSize);
	textFont(f);

	//how many people in the list?
	rowCount = saa.getInt("count");

	// load data from table
	for (int i = 0; i < rowCount; i++){
		//add employees to our arraylist.
		
		JSONObject employeeJSON = saaValues.getJSONObject(i);
		JSONObject nameJSON = employeeJSON.getJSONObject("name");

		employees.add(
			new Employee(
				nameJSON.getString("text"), 
				employeeJSON.getString("commute_rate"), 
				employeeJSON.getFloat("trips"), 
				employeeJSON.getFloat("miles"),
				PApplet.parseInt(map(i, 0, rowCount, 7, 4))
			)
		);
	}	
}

public void draw() {

	background(palette[1]);

	stroke(255);
	strokeWeight(1);

	for (Employee e: employees){
		e.separate(employees);	
		e.run();
	}

	buttons.display();
      
    // displayRankings();
    idleCount++;

    if (idleCount == 280) {
    	for (Employee e: employees){
			e.stopFollow();
		}
    }

    if (idleCount > 280 && idleCount % 150 == 0){
    	PVector newTarget = new PVector(random(40, width - 40),random(40, height - 40));
    	for (Employee e: employees){
			e.changeTarget(newTarget);
		}
    }
       
}

public void mousePressed(){
	
	buttons.toggle();

	switch (sortSwitch) {
	
	//if sortSwitch is 0, move around randomly
	case 0:
		for (Employee e: employees){
			e.changeRank(0);
		}
		break;

	//if sortSwitch is 1, sort by commute rate
	case 1:
		Collections.sort(employees, new rateComparator());
		newRank = 1;
		for (Employee e: employees){
			e.changeRank(newRank);
			newRank++;
		}
		break;

	//if sortSwitch is 2, sort by total miles
	case 2:
		Collections.sort(employees, new mileComparator());
		newRank = 1;
		for (Employee e: employees){
			e.changeRank(newRank);
			newRank++;
		}
		break;
	}	


}

public void mouseMoved() {
	idleCount = 0;
	for (Employee e: employees){
			e.startFollow();
	}	
}

public void keyPressed(){
	refreshAPI();
}

public void refreshAPI(){
	saa = loadJSONObject("http://www.kimonolabs.com/api/3r9s4496?apikey=9adbc380301d558ba05315f79fff2e31");
	saaResults = saa.getJSONObject("results");
	saaValues = saaResults.getJSONArray("values");

	lastRun = saa.getString("lastsuccess");
	println("API Refreshed: " + lastRun);

	//how many people in the list?
	rowCount = saa.getInt("count");

	for (int i = employees.size() - 1; i >= 0; i--){
			employees.remove(i);
	}	

	// load data from table
	for (int i = 0; i < rowCount; i++){
		//add employees to our arraylist.
		
		JSONObject employeeJSON = saaValues.getJSONObject(i);
		JSONObject nameJSON = employeeJSON.getJSONObject("name");

		employees.add(
			new Employee(
				nameJSON.getString("text"), 
				employeeJSON.getString("commute_rate"), 
				employeeJSON.getFloat("trips"), 
				employeeJSON.getFloat("miles"),
				PApplet.parseInt(map(i, 0, rowCount, 7, 4))
			)
		);
	}

}
class Buttons{

	//this is a quick and dirty hack of a class. Ideally, it'd take in a number of buttom names and behaviors and do the rest automatically

	public void display(){
		
		rectMode(CORNERS);
		noStroke();
		//Commute Rate
		if (mouseX > 0 && mouseX < width/2 && mouseY > height-20 && mouseY < height || sortSwitch == 1 ){
			fill(palette[0]);
		} else {
			fill(palette[4]);
		}
		rect(0, height, width/2, height-20);
		

		//Total Miles
		if (mouseX > width/2 && mouseX < width && mouseY > height-20 && mouseY < height || sortSwitch == 2){
			fill(palette[0]);
		} else {
			fill(palette[4]);
		}
		rect(width/2, height, width, height-20);

		fill(255);
		textAlign(CENTER);
		textSize(10);
		text("Commute Rate", width/4, height-5);
		text("Total Miles", 3 * (width/4), height-5);
	}

	public void toggle(){

		//this is hard coded to the two buttons. ideally, it'd be created automatically based on how many buttons to make
		if (mouseX > 0 && mouseX < width/2 && mouseY > height-20 && mouseY < height){
			if (sortSwitch != 1){
				sortSwitch = 1;
				for (Employee e: employees){
					e.explode();
				}
			} else {
				sortSwitch = 0;
				for (Employee e: employees){
					e.explode();
				}
			}
			
		} else if (mouseX > width/2 && mouseX < width && mouseY > height-20 && mouseY < height) {
			if (sortSwitch != 2){
				sortSwitch = 2;
				for (Employee e: employees){
					e.explode();
				}
			} else {
				sortSwitch = 0;
				for (Employee e: employees){
					e.explode();
				}
			}
		}
	}

}
class Employee{
	
	String name;
	float commuteRate, totalTrips, totalMiles;
	int rank;

	int stringWidth;

	int radius = 50;

	PVector location, velocity, acceleration, target;

	float maxspeed;
	float maxforce;

	boolean displayInfo;
	boolean following;


	Employee(String name_, String commuteRate_, float totalTrips_, float totalMiles_, int speed_){

		name = name_;
		
		int percPos = commuteRate_.lastIndexOf("%");
		String floatRate = commuteRate_.substring(0, percPos);
		
		commuteRate = Float.valueOf(floatRate);

		totalTrips = totalTrips_;
		totalMiles = totalMiles_;

		stringWidth = fontSize * 2;

		displayInfo = false;
		following = true;

		location = new PVector(random(radius, width - radius), random(radius, height - radius));
		velocity = new PVector(random(-10,10), random(-10,10));
		acceleration = new PVector(0,0);
		target = new PVector(width/2, height/2);
		
		maxspeed = speed_;
		maxforce = .4f;

		rank = 0;

	}

	// Our return functions
	
	public float getCommuteRate() {
		return commuteRate;
	}


	public float getTotalMiles() {
		return totalMiles;
	}


	// Our "change a variable" functions

	public void changeRank(int newRank){
		rank = newRank;
	}


	public void applyForce(PVector force){
		PVector f = force;
		acceleration.add(f);
	}


	public void stopFollow(){
		following = false;
	}

	public void startFollow(){
		following = true;
	}




	//our main function

	public void run(){	
		//rank 0 means wander randomly
		if (rank == 0) {
			chaseMouse();
		}

		if (rank != 0) {
			lineUp();
		}
		
		goToTarget(target);
		update();
		checkEdges();
		display();
	}




	// Our command functions

	public void update(){		
		//standard motion functions
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		location.add(velocity);
		acceleration.mult(0);
	}

	public void lineUp(){
		//make a new PVector telling them where to line up, based on rank
		int xLoc = width/2;
		int yLoc = PApplet.parseInt(padding + (rank * (radius+15)));
		changeTarget(new PVector(xLoc, yLoc));
	}

	public void changeTarget(PVector _target){
		target = _target;
	}


	public void goToTarget(PVector target){		
		//get our line up position	
		PVector desired = PVector.sub(target, location);
		
		//how far away are we?
		float d = desired.mag();

		//if we're close
	    if (d < 100) {
	      //arrive slowly
	      float m = map(d, 0, 100, 0, maxspeed);
	      desired.mult(m);
	    } else { //otherwise, travel normally
	      desired.mult(maxspeed);
	    }

	    //steer towards desired location
		PVector steer = PVector.sub(desired, velocity);
		
		//we can't steer too fast
		steer.limit(maxforce);

		//standard applyForce function
		applyForce(steer);
	}


	public void chaseMouse(){
		//where is the mouse?
		if (following){
			
			changeTarget(new PVector(mouseX, mouseY));

		}
	}


	public void checkEdges() {
	    if (location.x > width - (radius/2) - 5) {
	      location.x = width - (radius/2) - 5;
	      velocity.x *= -1.5f;
	    } else if (location.x < (radius/2) + 5) {
	      location.x = (radius/2) + 5;
	      velocity.x *= -1.5f;
	    }
	 
	    if (location.y > height - (radius/2) - 25) {
	      location.y = height - (radius/2) - 25;
	      velocity.y *= -1.5f;
	    }  else if (location.y < (radius/2) + 5) {
	      location.y = (radius/2) + 5;
	      velocity.y *= -1.5f;
	    }	
  	}


	public void explode(){
		PVector explode = new PVector(random(-20,20),random(-20, 20));
		applyForce(explode);
	}

	public void separate(ArrayList<Employee> employees){
		PVector sum = new PVector();
		float desiredSeparation = radius;
		int count = 0;

		for (Employee other : employees){
			float d = PVector.dist(location, other.location);

			if ((d > 0) && (d < desiredSeparation)) {

				PVector diff = PVector.sub(location, other.location);
				diff.normalize();
				sum.add(diff);
				count++;

			}

		}

		if (count > 0) {
			sum.div(count);
			sum.setMag(maxspeed);

			PVector steer = PVector.sub(sum, velocity);
			steer.limit(maxforce * 10);

			applyForce(steer);
		}
	}


	//our display functions

	public void display(){
		fill(palette[3]);
		textSize(fontSize);
		textAlign(CENTER);
		
		rectMode(CENTER);

		//if the mouse is hovering over it, change the fill and display the info
		if (dist(location.x, location.y, mouseX, mouseY) < radius/2) {
			
			fill(255);
			
			textAlign(LEFT);
			textSize(12);
			
			if (sortSwitch != 0){
				text(name, location.x + (radius / 1.5f), location.y);
			}

			if (sortSwitch == 1){
				text(commuteRate + "%", location.x + (radius / 1.5f), location.y+(fontSize/1.4f));
			} else if (sortSwitch == 2){
				text(totalMiles + " mi.", location.x + (radius / 1.5f), location.y+(fontSize/1.4f));
			}
			
			fill(palette[3]);

		//otherwise, use our standard fill
		} else {
			fill(palette[2]);
		}

		// stroke(255);
		// strokeWeight(1);
		// ellipse(location.x, location.y, radius, radius);

		image(bikePhoto,location.x-25, location.y-25);

		fill(255);
		textSize(fontSize);
		
		// get the first initials
		char firstInit = name.charAt(0);
		String f = firstInit+"";
		textAlign(CENTER);
		text(f, location.x, location.y+(fontSize/2)-2);		
	}


	


	
}	
class mileComparator implements Comparator<Employee> {

	public int compare(Employee e1, Employee e2){

		float e1Miles = e1.getTotalMiles();
		float e2Miles = e2.getTotalMiles();

		if (e1Miles > e2Miles){
			return -1;
		} else if (e1Miles < e2Miles){
			return 1;
		} else {
			return 0;
		}
	}
}
class rateComparator implements Comparator<Employee> {

	public int compare(Employee e1, Employee e2){

		float e1Rate = e1.getCommuteRate();
		float e2Rate = e2.getCommuteRate();

		if (e1Rate > e2Rate){
			return -1;
		} else if (e1Rate < e2Rate){
			return 1;
		} else {
			return 0;
		}
	}

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "bike_to_work_json" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
