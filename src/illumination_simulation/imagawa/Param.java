package illumination_simulation.imagawa;

class Param{
	private int halogen=0;
	private int led=0;
	private double fade=0;
	private int speed=0;
	private boolean eye=false;
	private boolean strobe=false;
	public Param() {

	}
	/**
	 * ハロゲンの状態の定義
	 * 2進数で左から1なら点灯，0なら消灯
	 * @param i ハロゲンの状態
	 */
	public void setHalogen(int i) {
		halogen=i%16;
	}
	/**
	 * LEDの状態の定義
	 * @param i LEDの番号
	 */
	public void setLED(int i) {
		halogen=i%12;
	}
	/**
	 * フェードの設定：未実装
	 * @param f
	 */
	public void setFade(double f) {
		fade=f;
	}
	/**
	 * スピードの設定
	 * BPMで定義
	 * @param bpm
	 */
	public void setSpeed(int bpm) {
		speed=bpm;
	}
	/**
	 * 目つぶしの設定
	 * @param flg
	 */
	public void setEye(boolean flg) {
		eye=flg;
	}
	/**
	 * ストロボの設定
	 * @param flg
	 */
	public void setStrobe(boolean flg) {
		strobe=flg;
	}
	public int getHalogen() {
		return halogen;
	}
	public int getLED() {
		return led;
	}
	public double getFade() {
		return fade;
	}
	public int getSpeed() {
		return speed;
	}
	public boolean getEye() {
		return eye;
	}
	public boolean getStrobo() {
		return strobe;
	}
}