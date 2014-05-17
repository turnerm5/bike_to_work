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
		maxforce = .4;

		rank = 0;

	}

	// Our return functions
	
	float getCommuteRate() {
		return commuteRate;
	}


	float getTotalMiles() {
		return totalMiles;
	}


	// Our "change a variable" functions

	void changeRank(int newRank){
		rank = newRank;
	}


	void applyForce(PVector force){
		PVector f = force;
		acceleration.add(f);
	}


	void stopFollow(){
		following = false;
	}

	void startFollow(){
		following = true;
	}




	//our main function

	void run(){	
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

	void update(){		
		//standard motion functions
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		location.add(velocity);
		acceleration.mult(0);
	}

	void lineUp(){
		//make a new PVector telling them where to line up, based on rank
		int xLoc = width/2;
		int yLoc = int(200 + (rank * (radius+30)));
		changeTarget(new PVector(xLoc, yLoc));
	}

	void changeTarget(PVector _target){
		target = _target;
	}


	void goToTarget(PVector target){		
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


	void chaseMouse(){
		//where is the mouse?
		if (following){
			
			//this could be rewritten to be a bit more sophisticated
			changeTarget(new PVector(mouseX, mouseY));

		}
	}


	void checkEdges() {
	    if (location.x > width - (radius/2) - 5) {
	      location.x = width - (radius/2) - 5;
	      velocity.x *= -1.5;
	    } else if (location.x < (radius/2) + 5) {
	      location.x = (radius/2) + 5;
	      velocity.x *= -1.5;
	    }
	 
	    if (location.y > height - (radius/2) - 25) {
	      location.y = height - (radius/2) - 25;
	      velocity.y *= -1.5;
	    }  else if (location.y < (radius/2) + 5) {
	      location.y = (radius/2) + 5;
	      velocity.y *= -1.5;
	    }	
  	}


	void explode(){
		PVector explode = new PVector(random(-20,20),random(-20, 20));
		applyForce(explode);
	}

	void separate(ArrayList<Employee> employees){
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

	void display(){
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
				text(name, location.x + (radius / 1.5), location.y);
			}

			if (sortSwitch == 1){
				text(commuteRate + "%", location.x + (radius / 1.5), location.y+(fontSize/1.4));
			} else if (sortSwitch == 2){
				text(totalMiles + " mi.", location.x + (radius / 1.5), location.y+(fontSize/1.4));
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
