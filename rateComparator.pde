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