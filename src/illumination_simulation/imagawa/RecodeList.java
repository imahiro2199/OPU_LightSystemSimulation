package illumination_simulation.imagawa;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class RecodeList {
	List <RecodeFunction>rec=new ArrayList<RecodeFunction>();
	RecodeList(){
		rec.add(new RecodeFunction(new int[] {0,0,0,0},0,false,false,0.0));
	}

	public String toString() {
		String st="";
		for(int i=0;i<rec.size();i++) {
			st+=rec.get(i).toString()+"\r\n";
		}
		return st;
	}
	boolean addSimply(RecodeFunction r) {
		return rec.add(r);
	}
	void add(int[] h,int l,boolean s, boolean e, double m) {
		add(new RecodeFunction( h, l, s,  e,  m));
	}

	void add(int[] h,int l,boolean s, boolean e, double m,boolean flg) {
		add(new RecodeFunction( h, l, s,  e,  m),flg);
	}

	void add(RecodeFunction r) {
		add(r,false);
	}
	//flg==true->最適化なし
	//flg==false->前後に近いノートがあれば統合
	void add(RecodeFunction r,boolean flg) {
		int id=getNote(r.getTime());
		RecodeFunction now=rec.get(id);
		r.setBitSet(now);
		BitSet rbit=r.getBitSet();
		double t1=r.getTime()-now.getTime();
		if(flg) {
			t1=t1<2?t1:1000;
		}
		if(id==rec.size()-1) {
			if(rbit.isEmpty()) {
				return;
			}
			if(t1<20.0) {
				r.setTime(now.getTime());
				rec.set(id,r);
				return;
			}
			rec.add(id+1,r);
			return;
		}
		double t2=rec.get(id+1).getTime()-r.getTime();
		if(r.getBitSet().equals(rec.get(id+1).getBitSet())&&r.getLED()==rec.get(id+1).getLED()) {
			rec.get(id+1).setTime(r.getTime());
			return;
		}
		int tmp[]=r.getHalogen();
		BitSet b=now.getBitSet();
		if(t2<20.0) {
			for(int i=0;i<4;i++) {
				if(rbit.get(i)&&!b.get(i)) {
					int j=id+1;
					while(!rec.get(j).getBitSet().get(i)) {
						rec.get(j).setHalogen(i,tmp[i]);
						j++;
						if(j>=rec.size()) {
							break;
						}
					}
					if(j!=rec.size()&&rec.get(j).getHalogen()[i]==0) {
						rec.get(j).setBitSet(i,false);
					}
				}
			}
			if(rbit.get(4)&&!b.get(4)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(4)) {
					rec.get(j).setEye(r.getEye());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&!rec.get(j).getEye()) {
					rec.get(j).setBitSet(4,false);
				}
			}
			if(rbit.get(5)&&!b.get(5)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(5)) {
					rec.get(j).setStrobo(r.getStrobo());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&!rec.get(j).getStrobo()) {
					rec.get(j).setBitSet(5,false);
				}
			}
			if(rbit.get(6)&&!b.get(6)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(6)) {
					rec.get(j).setLED(r.getLED());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&r.getLED()==rec.get(j).getLED()) {
					rec.get(j).setBitSet(6,false);
				}
			}
			b.or(rbit);
			if(r.getLED()!=rec.get(id+1).getLED()) {
				b.set(6,true);
			}
			r.setTime(now.getTime());
			rec.set(id, r);
			rec.get(id).setBitSet(b);
		}else if(t1<20.0) {
			for(int i=0;i<4;i++) {
				if(rbit.get(i)&&!b.get(i)) {
					int j=id;
					while(!rec.get(j).getBitSet().get(i)) {
						rec.get(j).setHalogen(i,tmp[i]);
						j++;
						if(j>=rec.size()) {
							break;
						}
					}
					if(j!=rec.size()&&rec.get(j).getHalogen()[i]==0) {
						rec.get(j).setBitSet(i,false);
					}
				}
			}
			if(rbit.get(4)&&!b.get(4)) {
				int j=id;
				while(!rec.get(j).getBitSet().get(4)) {
					rec.get(j).setEye(r.getEye());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&!rec.get(j).getEye()) {
					rec.get(j).setBitSet(4,false);
				}
			}
			if(rbit.get(5)&&!b.get(5)) {
				int j=id;
				while(!rec.get(j).getBitSet().get(5)) {
					rec.get(j).setStrobo(r.getStrobo());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&!rec.get(j).getStrobo()) {
					rec.get(j).setBitSet(5,false);
				}
			}
			if(rbit.get(6)&&!b.get(6)) {
				int j=id;
				while(!rec.get(j).getBitSet().get(6)) {
					rec.get(j).setLED(r.getLED());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&r.getLED()==rec.get(j).getLED()) {
					rec.get(j).setBitSet(6,false);
				}
			}
			rbit.or(b);
			rec.get(id).setBitSet(rbit);
		}else {
			System.out.println("rec");
			BitSet af=rec.get(id+1).getBitSet();
			for(int i=0;i<4;i++) {
				if(rbit.get(i)&&!af.get(i)) {
					int j=id+1;
					while(!rec.get(j).getBitSet().get(i)) {
						rec.get(j).setHalogen(i,tmp[i]);
						j++;
						if(j>=rec.size()) {
							break;
						}
					}
					if(j!=rec.size()) {
						rec.get(j).setBitSet(i,false);
					}
				}
			}
			if(rbit.get(4)&&!af.get(4)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(4)) {
					rec.get(j).setEye(r.getEye());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()) {
					rec.get(j).setBitSet(4,false);
				}
			}
			if(rbit.get(5)&&!af.get(5)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(5)) {
					rec.get(j).setStrobo(r.getStrobo());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()) {
					rec.get(j).setBitSet(5,false);
				}
			}
			if(rbit.get(6)&&!af.get(6)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(6)) {
					rec.get(j).setLED(r.getLED());
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&r.getLED()==rec.get(j).getLED()) {
					rec.get(j).setBitSet(6,false);
				}
			}
			rbit.flip(0,7);
			rbit.and(af);
			if(r.getLED()!=rec.get(id+1).getLED()) {
				rbit.set(6,true);
			}
			rec.get(id).setBitSet(rbit);
			if(r.getBitSet().isEmpty()) {
				return;
			}
			rec.add(id+1,r);
		}
		//rec.sort((a,b)->(int)(a.getTime()*100) - (int)(b.getTime()*100));
	}

	void change(RecodeFunction r,int id) {
		RecodeFunction old=rec.get(id);
		if(id!=0) {
			r.setBitSet(rec.get(id-1));
		}
		BitSet rbit=r.getBitSet();
		if(r.getBitSet().equals(old.getBitSet())&&r.getLED()==old.getLED()) {
			return;
		}
		rec.set(id, r);
		if(id==rec.size()-1) {
			return;
		}
		int tmp[]=r.getHalogen();
		BitSet af=rec.get(id+1).getBitSet();
		rbit.xor(af);
		for(int i=0;i<4;i++) {
			if(rbit.get(i)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(i)) {
					rec.get(j).setHalogen(i,tmp[i]);
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()) {
					rec.get(j).setBitSet(i,false);
				}
			}
		}
		if(rbit.get(4)) {
			int j=id+1;
			while(!rec.get(j).getBitSet().get(4)) {
				rec.get(j).setEye(r.getEye());
				j++;
				if(j>=rec.size()) {
					break;
				}
			}
			if(j!=rec.size()) {
				rec.get(j).setBitSet(4,false);
			}
		}
		if(rbit.get(5)) {
			int j=id+1;
			while(!rec.get(j).getBitSet().get(5)) {
				rec.get(j).setStrobo(r.getStrobo());
				j++;
				if(j>=rec.size()) {
					break;
				}
			}
			if(j!=rec.size()) {
				rec.get(j).setBitSet(5,false);
			}
		}
		if(rbit.get(6)) {
			int j=id+1;
			while(!rec.get(j).getBitSet().get(6)) {
				rec.get(j).setLED(r.getLED());
				j++;
				if(j>=rec.size()) {
					break;
				}
			}
			if(j!=rec.size()&&r.getLED()==rec.get(j).getLED()) {
				rec.get(j).setBitSet(6,false);
			}
		}
		if(r.getLED()!=rec.get(id+1).getLED()) {
			rec.get(id).setBitSet(6,true);
		}
		if(r.getLED()==rec.get(id+1).getLED()) {
			rec.get(id+1).setBitSet(6,false);
		}
		if(rec.get(id+1).getBitSet().isEmpty()) {
			rec.remove(id+1);
		}
		if(r.getBitSet().isEmpty()) {
			rec.remove(id);
		}

	}

	void optimize() {
		for(int i=1;i<rec.size();) {
			if(rec.get(i).getBitSet().isEmpty()||rec.get(i-1).getID()==rec.get(i).getID()) {
				rec.remove(i);
			}else {
				i++;
			}
		}
	}

	void remove(int id) {
		if(id==0) {
			rec.set(0,new RecodeFunction(new int[] {0,0,0,0},0,false,false,0.0));
			return;
		}
		if(id==rec.size()-1) {
			rec.remove(id);
			return;
		}
		BitSet af=rec.get(id+1).getBitSet();
		BitSet rm=rec.get(id).getBitSet();
		RecodeFunction bf=rec.get(id);
		int tmp[]=bf.getHalogen();
		for(int i=0;i<4;i++) {
			if(rm.get(i)&&!af.get(i)) {
				int j=id+1;
				while(!rec.get(j).getBitSet().get(i)) {
					rec.get(j).setHalogen(i,tmp[i]);
					j++;
					if(j>=rec.size()) {
						break;
					}
				}
				if(j!=rec.size()&&rec.get(j).getHalogen()[i]==0) {
					rec.get(j).setBitSet(i,false);
				}
			}
		}
		if(rm.get(4)&&!af.get(4)) {
			int j=id+1;
			while(!rec.get(j).getBitSet().get(4)) {
				rec.get(j).setEye(bf.getEye());
				j++;
				if(j>=rec.size()) {
					break;
				}
			}
			if(j!=rec.size()&&!rec.get(j).getEye()) {
				rec.get(j).setBitSet(4,false);
			}
		}
		if(rm.get(5)&&!af.get(5)) {
			int j=id+1;
			while(!rec.get(j).getBitSet().get(5)) {
				rec.get(j).setStrobo(bf.getStrobo());
				j++;
				if(j>=rec.size()) {
					break;
				}
			}
			if(j!=rec.size()&&!rec.get(j).getStrobo()) {
				rec.get(j).setBitSet(5,false);
			}
		}
		if(rm.get(6)&&!af.get(6)) {
			int j=id+1;
			while(!rec.get(j).getBitSet().get(6)) {
				rec.get(j).setLED(bf.getLED());
				j++;
				if(j>=rec.size()) {
					break;
				}
			}
			if(j!=rec.size()&&bf.getLED()==rec.get(j).getLED()) {
				rec.get(j).setBitSet(6,false);
			}
		}
		af.xor(rm);
		if((bf.getLED()!=rec.get(id+1).getLED())) {
			af.set(6,true);
		}
		rec.get(id+1).setBitSet(af);
		rec.remove(id);
	}

	//millitime以外等しければtrue
	boolean compare(RecodeFunction a,RecodeFunction b) {
		return a.getID()==b.getID();
	}

	void clear() {
		clear(true);
	}
	void clear(boolean flg) {
		rec.clear();
		if(flg) {
			rec.add(new RecodeFunction(new int[] {0,0,0,0},0,false,false,0.0));
		}
	}

	void optmize() {
		int i=0;
		while(i<rec.size()-1) {
			if(compare(rec.get(i), rec.get(i+1))){
				rec.remove(i+1);
			}else {
				i++;
			}
		}
	}
	int getLength() {
		return rec.size();
	}


	int getNote(double time) {
		int i=0;
		while(time>=rec.get(i).getTime()) {
			i++;
			if(i>=rec.size()) {
				break;
			}
		}
		return i-1<0?0:i-1;
	}
	RecodeFunction getParam(double m) {
		int id=this.getNote(m);
		if(id<0) {
			rec.add(new RecodeFunction(new int[] {0,0,0,0},0,false,false,0.0));
			id=0;
		}
		return rec.get(id);
	}
	RecodeFunction get(int i) {
		return rec.get(i);
	}
}
