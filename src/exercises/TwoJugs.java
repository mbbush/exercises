package exercises;

import java.util.*;

//import java.util.LinkedList;
//import java.util.List;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TwoJugs {

	public final int cap1;
	public final int cap2;
	public final int vol1;
	public final int vol2;

	public final int iteration;
	public TwoJugs parent;
	public final Move move;
	public static enum Move {EMPTY1, EMPTY2, FILL1, FILL2, TRANSFER12, TRANSFER21}


	public static void main(String[] args){

		int cap1 = 0;
		int cap2 = 0;
		int target = 0;


		if (args.length >= 3) cap2 = Integer.valueOf(args[2]);
		if (args.length >= 3) cap1 = Integer.valueOf(args[1]);
		if (args.length >= 1) target = Integer.valueOf(args[0]);

		if (target == 0) target = randomInt(25);

		while (Math.min(cap1, cap2) <= 0 || target > cap1 + cap2 || findGCD(cap1, cap2) != 1){
			cap1 = randomInt(Math.max(25, target));
			cap2 = randomInt(Math.max(25, target));
		}

		if (cap2 <= cap1){
			int c = cap2;
			cap2 = cap1;
			cap1 = c;
		}

		System.out.println("Trying to make " + target + " with jugs of size " + cap1 + " and " + cap2 + ".");
		TwoJugs initial = new TwoJugs(cap1, cap2);
		TwoJugs goal = null;

		HashSet<TwoJugs> toCheck = new HashSet<TwoJugs>();
		HashSet<TwoJugs> explored = new HashSet<TwoJugs>();
		HashSet<TwoJugs> checkNext = new HashSet<TwoJugs>();
		toCheck.add(initial);
		explored.add(initial);

		search:
		while (!toCheck.isEmpty()){
			for (TwoJugs state : toCheck){
				for (Move move : Move.values()){
					TwoJugs tj = new TwoJugs(state, move);
					if (tj.equals(tj.parent)) continue;
					if (!explored.contains(tj)){
						checkNext.add(tj);
						explored.add(tj);
						if (tj.getTotal() == target){
							goal = tj;
							break search;
						}
					}
				}
			}
			toCheck.clear();
			toCheck.addAll(checkNext);
			checkNext.clear();
		}

		List<TwoJugs> solution = traceback(goal);
		Iterator<TwoJugs> solIt = solution.iterator();
		System.out.println(goal.iteration + "-step Solution found after examining " + explored.size() + " states.");
		System.out.println();
		System.out.println("Jug 1 has capacity " + goal.cap1 + " and Jug 2 has capacity " + goal.cap2 + ".");
		System.out.println();

		int i = 1;
		solIt.next(); // discard the starting position, since it's always empty
		while (solIt.hasNext()){
			System.out.println();
			TwoJugs state = solIt.next();
			switch (state.move){
			case EMPTY1:
				System.out.println("Step " + i + ": Pour out Jug 1.");
				break;
			case EMPTY2:
				System.out.println("Step " + i + ": Pour out Jug 2.");
				break;
			case FILL1:
				System.out.println("Step " + i + ": Fill Jug 1.");
				break;
			case FILL2:
				System.out.println("Step " + i + ": Fill Jug 2.");
				break;
			case TRANSFER12:
				System.out.println("Step " + i + ": Pour " + Integer.toString(state.vol2 - state.parent.vol2) + " units from Jug 1 to Jug 2.");
				break;
			case TRANSFER21:
				System.out.println("Step " + i + ": Pour " + Integer.toString(state.vol1 - state.parent.vol1) + " units from Jug 2 to Jug 1.");
				break;
			}
			System.out.println("Now there are " + state.vol1 + " units in Jug 1 and " + state.vol2 + " units in Jug 2.");
			i++;
		}

	}

	public TwoJugs(TwoJugs parent, Move move){
		super();
		this.cap1 = parent.cap1;
		this.cap2 = parent.cap2;
		int v1 = parent.vol1;
		int v2 = parent.vol2;
		this.parent = parent;
		this.iteration = parent.iteration + 1;
		this.move = move;
		switch (move) {
		case EMPTY1:
			v1 = 0;
			break;
		case EMPTY2:
			v2 = 0;
			break;
		case FILL1:
			 v1 = this.cap1;
			break;
		case FILL2:
			v2 = this.cap2;
			break;
		case TRANSFER12:
			int xfer12 = Math.min(v1, cap2 - v2);
			v1 -= xfer12;
			v2 += xfer12;
			break;
		case TRANSFER21:
			int xfer21 = Math.min(v2, cap1 - v1);
			v1 += xfer21;
			v2 -= xfer21;
			break;
		}
		this.vol1 = v1;
		this.vol2 = v2;

	}

	public TwoJugs(int cap1, int cap2) {
		super();
		this.cap1 = cap1;
		this.cap2 = cap2;
		this.vol1 = 0;
		this.vol2 = 0;
		this.iteration = 0;
		this.parent = null;
		this.move = null;
	}

	public int getTotal(){
		return vol1 + vol2;
	}

	public static int findGCD(int i1, int i2){
		if (i2 == 0) return i1;
		return findGCD(i2, i1 % i2);
	}

	public static List<TwoJugs> traceback(TwoJugs endState){
		LinkedList<TwoJugs> out = new LinkedList<TwoJugs>();
		out.addLast(endState);
		TwoJugs tj = endState.parent;

		while (tj != null){
			out.addFirst(tj);
			tj = tj.parent;
		}
		return out;
	}
	@Override
	public boolean equals(Object other){
		if (other instanceof TwoJugs)
		return (this.vol1 == ((TwoJugs)other).vol1 &&
				this.vol2 == ((TwoJugs)other).vol2 &&
				this.cap1 == ((TwoJugs)other).cap1 &&
				this.cap2 == ((TwoJugs)other).cap2);
		else return false;
	}

	@Override
	public int hashCode(){
		return new HashCodeBuilder(17,41).
				append(cap1).
				append(cap2).
				append(vol1).
				append(vol2).
				toHashCode();
	}
	public static int randomInt(int max){
		double rand = Math.random();
		return (int) (rand * (max)) + 1;
		// output in the range [1, max]
	}
	public static int randomInt(int min, int max){
		if (min > max) throw new IllegalArgumentException("asked to find random int in empty range");
		double rand = Math.random();
		return (int) (rand * (max - min + 1)) + min;
		// output in the range [min, max]
	}

}
