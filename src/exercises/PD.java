package exercises;

public class PD {

	public PD() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		int[][][] arr = new int[6][10][2];
        System.out.print("\t1\t\t2\t\t3\t\t4\t\t5\t\t6\t\t7\t\t8\t\t9\t\t10\n");
		for (int value = 1; value <= 6; value++){
			System.out.print(value + "\t");
        	for (int volume = 1; volume <= 10; volume++){
//        		System.out.println("Val: " + value + " Vol: " + volume + " HP: "+ value * volume + "  Percentage: " + (int) value * value *volume * volume + "%%");
        		int HP = value * volume;
        		int percentage = (int) Math.min(100, value * value * volume * volume);
        		arr[value-1][volume-1][0] = HP;
        		arr[value-1][volume-1][1] = percentage;
        		int HT = percentage == 100 ? 0 : (int) Math.ceil(HP * 100d / percentage);
        		HT /= 5;
        		HT = Math.max(0, HT - 4);
        		System.out.print(HT + "\t");
        	}
        	System.out.print("\n");
        }


//        value *= volume;
//        value = (int)Math.max(volume*volume*.01*hero.HT, value);

	}

}
