import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;

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
int leading = int(fontSize * 1.25);

int sortSwitch = 0;

color[] eddy = {#5C2849, #A73E5C, #EC7263, #FE9551, #FFD285};
color[] breath = {#D14B2D, #414D4A, #748277, #96BDAF, #827A67};
color[] palette = breath;

void setup() {
	size(displayWidth, displayHeight);
	
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
				int(map(i, 0, rowCount, 7, 4))
			)
		);
	}	
}

void draw() {

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

void mousePressed(){
	
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

void mouseMoved() {
	idleCount = 0;
	for (Employee e: employees){
			e.startFollow();
	}	
}

void keyPressed(){
	refreshAPI();
}

void refreshAPI(){
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
				int(map(i, 0, rowCount, 7, 4))
			)
		);
	}

}