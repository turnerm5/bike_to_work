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