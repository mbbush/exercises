package exercises;

import java.math.BigInteger;



public class BI {
	//data store
	private int[] value;

	public BI add(BI other){
		int[] out = new int[Math.max(this.value.length, other.value.length)];
		long carry = 0;
		for (int i = 0; i < out.length; i++){
			long myVal, otherVal;
			if (i < value.length) myVal = (long) value[i];
			else myVal = 0;
			if (i < other.value.length) otherVal = (long) other.value[i];
			else otherVal = 0;
			long result = myVal + otherVal + carry;
			out[i] = ((int) result & 0x7FFFFFFF);
			carry = result &       0x3FFFFFFF80000000l;
			carry >>= 31;
		}
		return new BI(out, carry);
//	    I changed the design from a constructor based only on out to one based on out and carry,
//	    at the same time as I switched from easily resizeable Lists to fixed-size int[] arrays.
	}



	public BI(int initial){
		this.value = new int[1];
		this.value[0] = initial;
		System.out.println();
	}
	private BI(int[] value, long carry){
		if (carry == 0) this.value = value;
		else {
			this.value = new int[value.length + 1];
			System.arraycopy(value, 0, this.value, 0, value.length);
			this.value[value.length] = (int) carry;
		}
	}
	@Override
	public String toString(){
		char[] binary = new char[value.length * 31];
		int k = 0;
		for (int i = value.length - 1; i >= 0; i--){
			int v = value[i];
			for (int j = 30; j >= 0; j --){
				int exp = (1 << j);
				binary[k++] = ( (v & exp) > 0) ? '1' : '0';
			}
		}

		return new String(binary);

	}

	public static void main(String[] args){
		BI x = new BI(1);
		for (int i = 0; i < 70; i++) {
			System.out.println(x.toString());
			x = x.add(x);
		}
		System.out.println("done");
	}
}

//		0x40000000
//		0x80000000 =
//		result = 0x80000000

		//immutable
		//unsigned
		//stores arbitrary-length integers. 1000 bits ok, 10000 bits ok, etc...
		//implement the data store (you decide how)
		//implement a basic constructor (from a positive integer)

		//implement addition.
		//EXAMPLES:

		//  BI a = new BI(5);
		//  BI b = new BI(6)
		//  BI sum = a.add(b);
		// -> produces a new BI, does not modify a or b, value is 11

		// BI x = new BI(1);
		// for (int i = 0; i < 10000; i++) x = x.add(x)
		// x is now 2 ^ 10000

