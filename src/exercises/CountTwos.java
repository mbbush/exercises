package exercises;

public class CountTwos {

	public static void main (String[] args){
		for (String s : args){
			try{
				long i = Long.valueOf(s);
//				System.out.println(countTwosBruteForce(i));
//				System.out.println(countTwosFast(i));
//				System.out.println(countTwosFaster(i));
				System.out.println(countTwosFaster(i));
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static int countTwosFast(int n){
//		if (n < 10) {
//			return n >= 2 ? 1 : 0;
//		}
////		elif (n == 99) { return 20; }
//		else {
			int count = 0;
			if (n % 10 >= 2) count++;
			for(int m = 10; m <= n; m *= 10) {
				int c = countTwosFast(m - 1); // can be replaced with explicit values after more testing
				if ((n/m) % 10 < 2){
					count += c * ((n/m) % 10);
				} else if ((n/m) % 10 == 2){
					count += 2 * c + 1 + n % m;
				} else {
					count += m + c * ((n/m) % 10);
				}
			}
			return count;
//		}
	}
	public static long countTwosFaster(long n){
		long count = 0;
		if (n % 10 >= 2) count++;
		for(long m = 10; m <= n; m *= 10) {
			long c = m/10 * (long) Math.log10(m);
			if ((n/m) % 10 < 2){
				count += c * ((n/m) % 10);
			} else if ((n/m) % 10 == 2){
				count += 2 * c + 1 + n % m;
			} else {
				count += m + c * ((n/m) % 10);
			}
		}
		return count;
	}
	public static int countTwosFaster(int n){
		int count = 0;
		if (n % 10 >= 2) count++;
		for(int m = 10; m <= n; m *= 10) {
			int c = m/10 * (int) Math.log10(m);
			if ((n/m) % 10 < 2){
				count += c * ((n/m) % 10);
			} else if ((n/m) % 10 == 2){
				count += 2 * c + 1 + n % m;
			} else {
				count += m + c * ((n/m) % 10);
			}
		}
		return count;
	}

	public static int countTwosBruteForce(int n){
		int count = 0;
		for (int i = 2; i<=n; i++){
			for (int m = 1; m <= n; m *= 10){
				if ((i/m) % 10 == 2) count++;
			}
		}
		return count;
	}
}


