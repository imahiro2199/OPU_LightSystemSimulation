package illumination_simulation.imagawa;

import java.io.InputStream;

import javafx.scene.image.Image;

class Illumination{
	private String stage_filename="image/stage/stage.png";
	private Image[] halogen;
	private Image[] back_led;
	private Image[] front_led;
	private Image stage;
	private Image eye;
	public InputStream strToInpuStream(String path) {
		return getClass().getResourceAsStream(path); 
	}
	public Image getImage(String path) {
		return new Image(getClass().getResourceAsStream(path));
	}
	public Illumination(){
		System.out.println("loading images...");
		String eye_filename="image/illuminations/eye.png";
		String[] halogen_filename= {
				"b0000",
				"h0001",
				"b0001",
				"h0010",
				"b0010",
				"h0011",
				"b0011",
				"h0100",
				"b0100",
				"h0101",
				"b0101",
				"h0110",
				"b0110",
				"h0111",
				"b0111",
				"h1000",
				"b1000",
				"h1001",
				"b1001",
				"h1010",
				"b1010",
				"h1011",
				"b1011",
				"h1100",
				"b1100",
				"h1101",
				"b1101",
				"h1110",
				"b1110",
				"h1111",
				"b1111"
		};
		String[] back_led_filename= {
				"led1",
				"led2",
				"led3",
				"led4",
				"led5",
				"led11",
				"led12"
		};
		String[] front_led_filename= {
				"1l",
				"1r",
				"2l",
				"2r",
				"3l",
				"3r",
				"4l",
				"4r",
				"5l",
				"5r",
				"6m",
				"7r",
				"8r",
				"9r",
				"10m",
				"11l",
				"11r",
				"12l",
				"12r"
		};
		halogen=new Image[halogen_filename.length];
		back_led=new Image[back_led_filename.length];
		front_led=new Image[front_led_filename.length];
		try {
			stage=getImage(stage_filename);
		}catch(Exception e) {
			System.out.println("lack of PNG file:"+stage_filename);
		}
		try {
			eye=getImage(eye_filename);
		}catch(Exception e) {
			System.out.println("lack of PNG file:"+eye_filename);
		}
		for(int i=0;i<halogen_filename.length;i++) {
			try {
				halogen[i]=getImage("image/illuminations/"+halogen_filename[i]+".png");
			}catch(Exception e) {
				System.out.println("lack of PNG file:"+halogen_filename[i]+".png");
			}
		}
		for(int i=0;i<back_led_filename.length;i++) {
			try {
				back_led[i]=getImage("image/illuminations/"+back_led_filename[i]+".png");
			}catch(Exception e) {
				System.out.println("lack of PNG file:"+back_led_filename[i]+".png");
			}
		}
		for(int i=0;i<front_led_filename.length;i++) {
			try {
				front_led[i]=getImage("image/illuminations/"+front_led_filename[i]+".png");
			}catch(Exception e) {
				System.out.println("lack of PNG file:"+front_led_filename[i]+".png");
			}
		}
		System.out.println("complete!!!");
	}
	public Image getStage() {
		return stage;
	}
	public Image getLED_back(int n) {
		if(n==0) {
			return null;
		}
		int num=((n+11)%12)%7;
		switch(n) {
		case 6:
			num=3;
			break;
		case 7:
			num=0;
			break;
		case 8:
			num=1;
			break;
		case 9:
			num=2;
			break;
		case 10:
			num=0;
			break;
		case 11:
			num=5;
			break;
		case 12:
			num=6;
			break;
		}
		return back_led[num];
	}
	public Image[] getLED_front(int n) {
		if(n==0) {
			Image[] image2= {null,null};
			return image2;
		}
		int l=(n+11)*2%12;
		int r=(n+11)*2%12+1;
		switch(n) {
		case 6:
			l=10 ;
			r=10;
			break;
		case 7:
			l=0;
			r=11;
			break;
		case 8:
			l=2;
			r=12;
			break;
		case 9:
			l=4;
			r=13;
			break;
		case 10:
			l=14;
			r=14;
			break;
		case 11:
			l=15;
			r=16;
			break;
		case 12:
			l=17;
			r=18;
			break;
		}
		Image[] image= {front_led[l],front_led[r]};
		return image;
	}
	public Image getEye() {
		return eye;
	}
	public Image getHalogen(int n) {
		if(n==0) {
			return null;
		}
		return halogen[(n*2-1)%31];
	}
	public Image getShade(int n) {

		return halogen[(n*2)%31];
	}
}
