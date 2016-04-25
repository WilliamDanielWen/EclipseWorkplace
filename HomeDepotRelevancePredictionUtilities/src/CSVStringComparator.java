import java.util.Comparator;

// compare according to the first column "id", in an ascending order
public class CSVStringComparator implements Comparator<String>{

	public int compare(String a, String b) {
		// a.newPoint and b.newPoint must be the same point
		String[]array_A=a.split(",");
		String[]array_B=b.split(",");
		// compare the "id"
		double id_A=Double.parseDouble(array_A[1]);
		double id_B=Double.parseDouble(array_B[1]);

		// sort in an ascending order
		return id_A < id_B ? -1 : (id_A == id_B ? 0 : 1);
	}
}