package nGon;

public class GonCandidate {
	int[] xs, ys;
	int n;
	GonCandidate(int _n){
		n = _n;
		xs = new int[n];
		for(int i=0;i<n;i++){
			xs[i]=i;
		}
		ys = new int[n];
		for(int i=0;i<n;i++){
			ys[i]=i;
		}
	}
	StringBuilder sb = new StringBuilder();
	public String toString(){
		sb.setLength(0);
		for(int i=0;i<n;i++){
			sb.append('(');
			sb.append(xs[i]+1);
			sb.append(", ");
			sb.append(ys[i]+1);
			sb.append(")");
			if(i+1!=n)
				sb.append(", ");
		}
		return sb.toString();
	}
	
	public GonCandidate clone(){
		GonCandidate res = new GonCandidate(n);
		System.arraycopy(xs, 0, res.xs, 0, n);
		System.arraycopy(ys, 0, res.ys, 0, n);
		return res;
	}
}
