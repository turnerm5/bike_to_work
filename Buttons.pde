class Buttons{

	//this is a quick and dirty hack of a class. Ideally, it'd take in a number of buttom names and behaviors and do the rest automatically

	void display(){
		
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

	void toggle(){

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