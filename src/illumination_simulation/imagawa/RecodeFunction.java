package illumination_simulation.imagawa;

import java.util.BitSet;

public class RecodeFunction {
	private int[] ha;
	private int led;
	private boolean st;
	private boolean eye;
	private double millitime;
	private BitSet button=new BitSet(7);//hLL, hLC, hRC, hRR, eye, str, led
	RecodeFunction(int[] h,int l,boolean s, boolean e, double m){
		ha=h.clone();
		led=l;
		st=s;
		eye=e;
		millitime=m;
	}
	RecodeFunction(int[] h,int l,boolean s, boolean e, double m,BitSet b){
		ha=h.clone();
		led=l;
		st=s;
		eye=e;
		millitime=m;
		button=(BitSet) b.clone();
	}
	public String toString() {
		int S=st?1:0;
		int E=eye?1:0;
		String st=ha[0]+","+ha[1]+","+ha[2]+","+ha[3]+","+led+","+S+","+E+","+(int)millitime+",";
		for(int i=0;i<7;i++) {
			st+=(button.get(i)?1:0)+",";
		}
		return st;
	}
	int[] getHalogen() {
		return ha.clone();
	}
	int getLED() {
		return led;
	}
	void setLED(int l) {
		led=l;
	}
	boolean getStrobo() {
		return st;
	}
	boolean getEye() {
		return eye;
	}
	void setEye(boolean e) {
		eye=e;
	}
	void setStrobo(boolean s) {
		st=s;
	}

	double getTime() {
		return millitime;
	}
	void setTime(double t) {
		millitime=t;
	}
	BitSet getBitSet() {
		return (BitSet)button.clone();
	}
	void setBitSet(BitSet b) {
		button=(BitSet) b.clone();
	}
	void setBitSet(int id,boolean flg) {
		button.set(id, flg);
	}
	void setBitSet(RecodeFunction r) {
		//button.clear();
		for(int i=0;i<4;i++) {
			button.set(i,ha[i]!=r.getHalogen()[i]);
		}
		button.set(4,eye!=r.getEye());
		button.set(5,st!=r.getStrobo());
		button.set(6,led!=r.getLED());
	}
	int getID() {
		int HA=ha[3]*8+ha[2]*4+ha[1]*2+ha[0];
		int id=HA*1000+led*100;
		if(st) {
			id+=10;
		}
		if(eye) {
			id++;
		}
		return id;
	}
	void setHalogen(int id,int flg) {
		ha[id]=flg;
	}
}