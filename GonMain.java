package nGon;

import java.util.Arrays;
import java.util.HashSet;

public class GonMain {

	static final int n = 11;
	static final long nF = factorial(n), pnF = factorial(n-1);
	
	public static void timedMain(){
		
//	int[] foo = new int[n];
//	writePerm2(96, foo);
//	System.out.println(Arrays.toString(foo));
//	System.out.println(nextPerm(96,1));
//	writePerm2(nextPerm(96,1), foo);
//	System.out.println(Arrays.toString(foo));
//	}
//	
//	public static void dummy(){
		
		GonCandidate gc = new GonCandidate(n);
		int validCount = 0;
		
//		HashSet<Integer> areas = new HashSet<>();
		int minArea = Integer.MAX_VALUE, maxArea = -1;
		GonCandidate bestMin = null, bestMax = null;
		
		for(long i=nF/n/4;i<nF;i++){
//		for(long i=(long)(Math.random()*nF/n);i<nF;i++){
			writePerm2(i, gc.xs);
			if(gc.xs[0] != 0)
				continue;
			if(gc.xs[1] > gc.xs[n-1])
				continue;
			
			if(Math.random() > 0.9)
			System.out.println(i+" of ~"+nF*2/(2*n+3));
			
			for(long j=0;j<nF;j++){
//			for(long j=(long)(Math.random()*nF/2);j<nF;j++){
				int lastChanged = writePerm2(j, gc.ys);
				if(gc.ys[0] > n/2){
					break;
				}
				
//				System.out.println(Arrays.toString(gc.xs)+" -- "+Arrays.toString(gc.ys));

//				if(!checkSlopes(gc.xs, gc.ys)){
//					continue;
//				}
				int slopeCulprit = getSlope(gc.xs, gc.ys, lastChanged);
				if(slopeCulprit != -1){
					if(slopeCulprit == n-1 || slopeCulprit == n-2)
						continue;
					j = nextPerm(j, slopeCulprit);
					if(j==nF)
						break;
					j--;
					continue;
				}
				
//				if(!checkIntersect(gc.xs, gc.ys)){					
//					continue;
//				}
				int intersectionCulprit = getIntersect(gc.xs, gc.ys, lastChanged);
				if(intersectionCulprit != -1){
					if(intersectionCulprit == n-1 || intersectionCulprit == n-2)
						continue;
					j = nextPerm(j, intersectionCulprit);
					if(j==nF)
						break;
					j--;
					continue;
				}
				
//				System.out.println("Okay "+gc);
				
				int area = area2(gc.xs,gc.ys);
//				areas.add(area);
				if(area < minArea){
					minArea = area;
					bestMin = gc.clone();
					System.out.println("New min: "+bestMin+" @ "+area);
				}
				if(area > maxArea){
					maxArea = area;
					bestMax = gc.clone();
					System.out.println("New max: "+bestMax+" @ "+area);
				}
				
				validCount++;
			}
		}
		
		System.out.println(validCount+" candidates");
//		System.out.println("As="+areas);
		System.out.println(minArea+" vs. "+maxArea);
		System.out.println(bestMin);
		System.out.println(bestMax);
	}
	
	//Returns 2x the area of the polygon
	static int area2(int[] xs, int[] ys) {
		int area = 0;
		int j = n - 1;
		for (int i = 0; i < n; i++) {
			area += (xs[j] + xs[i]) * (ys[j] - ys[i]);
			j = i;
		}
		return Math.abs(area);
	} 

	static long factorial(int i){
		long r=i;
		while(--i>1)r*=i;
		return r;
	}
	
	//Generates a permutation given a number `p` in [0,n!).
	static void writePerm(long p, int[] dest) {
		for (int i = 0; i < n; i++)
			dest[i] = -1;

		for (int i = 0; i < n; i++) {
			int rem = (int)(p % (n - i));
			p /= n - i;
			for (int j = 0; j < n; j++) {
				if (dest[j] == -1)
					rem--;
				if (rem == -1) {
					dest[j] = i;
					break;
				}
			}
		}
	}
	
	//Generates a lexicographically ordered permutation given a number `p` in [0,n!).
	//Basically the same as above, but a different ordering.
	//Returns the number of digits changed (so half have '2', some have '3', etc.)
	static int writePerm2(long p, int[] dest) {
		int[] invertTemp = writePerm2Alloc.get();
		for (int i = 0; i < n; i++)
			invertTemp[i] = -1;
		
		long div = nF;
		int straightZeroCount = 0;
		for (int i = 0; i < n; i++) {
			div /= n - i;
			int rem = (int)(p / div);
			if(rem == 0)
				straightZeroCount++;
			else
				straightZeroCount=0;
			p -= div*rem;
			for (int j = 0; j < n; j++) {
				if (invertTemp[j] == -1)
					rem--;
				if (rem == -1) {
					invertTemp[j] = i;
					break;
				}
			}
		}
		for (int i = 0; i < n; i++) {
			dest[invertTemp[i]] = i; 
		}
		return straightZeroCount+1;
	}
	
	
	// Returns true if no duplicates in the slopes
	static boolean checkSlopes(int[] srcX, int[] srcY) {
		final float eps = 5*Math.ulp(1.0f);
		float[] slopeVals = slopeValAlloc.get();
		for (int i = 0; i < n-1; i++) {
			float slope = (1.0f * srcX[i] - srcX[i+1]) / (srcY[i] - srcY[i+1]);
			slopeVals[i] = slope;
		}
		slopeVals[n-1] = (1.0f * srcX[n-1] - srcX[0]) / (srcY[n-1] - srcY[0]);
		Arrays.sort(slopeVals);
		for (int i = 1; i < n; i++) {
			if (Math.abs(slopeVals[i] - slopeVals[i - 1]) <= eps)
				return false;
		}
		return true;
	}
	
	// Returns true if no segments intersect
	static boolean checkIntersect(int[] srcX, int[] srcY){
		for (int i = 0; i < n-1; i++) {
			for (int j = i + 2; j < n-1; j++) {
				if(do_intersect(srcX[i],srcY[i],srcX[i+1],srcY[i+1],srcX[j],srcY[j],srcX[j+1],srcY[j+1]))
					return false;
			}
		}
		
		for (int i = 1; i < n-2; i++) {
			if(do_intersect(srcX[i],srcY[i],srcX[i+1],srcY[i+1],srcX[0],srcY[0],srcX[n-1],srcY[n-1]))
				return false;
		}
		return true;
	}
	
	// Returns the last index of a point that is responsible for a slope repetition.
	static int getSlope(int[] srcX, int[] srcY, int lastChanged){
		int startPoint = Math.min(n-1-lastChanged, 0);
		for (int i = startPoint; i < n-1; i++) {
			for (int j = 0; j < i; j++) {
				int A = (srcY[i+1]-srcY[i])*(srcX[j+1]-srcX[j]);
				int B = (srcY[j+1]-srcY[j])*(srcX[i+1]-srcX[i]);
				if(A==B)
					return i+1;
			}
		}
		
		for (int i = 0; i < n-1; i++) {
			int A = (srcY[i+1]-srcY[i])*(srcX[n-1]-srcX[0]);
			int B = (srcY[n-1]-srcY[0])*(srcX[i+1]-srcX[i]);
			if(A==B)
				return n-1;
		}
		return -1;
	}
		
	// Returns the last index of a point that is responsible for an intersection.
	static int getIntersect(int[] srcX, int[] srcY, int lastChanged){
		int startPoint = Math.min(n-1-lastChanged, 0);
		for (int i = startPoint; i < n-1; i++) {
			for (int j = 0; j < i-1; j++) {
				if(do_intersect(srcX[i],srcY[i],srcX[i+1],srcY[i+1],srcX[j],srcY[j],srcX[j+1],srcY[j+1]))
					return i+1;
			}
		}
		
		for (int i = 1; i < n-2; i++) {
			if(do_intersect(srcX[i],srcY[i],srcX[i+1],srcY[i+1],srcX[0],srcY[0],srcX[n-1],srcY[n-1]))
				return n-1;
		}
		return -1;
	}
	
	//Jumps to the next permutation (in lexicographic order, i.e. writePerm2) that modifies
	//the digit at position posToChange.
	static long nextPerm(long p, int posToChange){
		posToChange = n-posToChange;
		for (int i = 2; i < posToChange; i++) {
			p /= i;
		}
		p++;
		for (int i = 2; i < posToChange; i++) {
			p *= i;
		}
		return p;
	}

	//Checks if 4 points specify two overlapping line segments
	static boolean do_intersect(int p0_x, int p0_y, int p1_x, int p1_y, int p2_x, int p2_y, int p3_x, int p3_y) {
		int s1_x, s1_y, s2_x, s2_y;
		s1_x = p1_x - p0_x;
		s1_y = p1_y - p0_y;
		s2_x = p3_x - p2_x;
		s2_y = p3_y - p2_y;

		int sN, sD, tN, tD;
		sN = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y));
		sD = (-s2_x * s1_y + s1_x * s2_y);
		tN = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x));
		tD = (-s2_x * s1_y + s1_x * s2_y);
		
		int sP = sN*sD;
		int tP = tN*tD;

//		float s, t;
//	    s = (-(float)s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
//	    t = ( (float)s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

	    boolean ret1 = (sP >= 0 && sP <= sD*sD && tP >= 0 && tP <= tD*tD);
//	    boolean ret2 = (s >= 0 && s <= 1 && t >= 0 && t <= 1);
//	    if(ret1 != ret2)
//	    	throw new RuntimeException(sN+", "+sD+", "+tN+", "+tD+", "+s+", "+t);
	    return ret1;

	}

	static final ThreadLocal<int[]> writePerm2Alloc = new ThreadLocal<int[]>() {
		@Override
		protected int[] initialValue() {
			return new int[n];
		}
	};
	static final ThreadLocal<float[]> slopeValAlloc = new ThreadLocal<float[]>() {
		@Override
		protected float[] initialValue() {
			return new float[n];
		}
	};

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		timedMain();
		System.out.println("Completed in "+(System.currentTimeMillis()-start)+"ms");
	}
}
