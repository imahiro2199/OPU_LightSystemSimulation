package illumination_simulation.imagawa;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.BitSet;
import java.util.Scanner;

import javax.swing.JOptionPane;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ISapp extends Application{
	static Illumination ill;
	static long time;
	static boolean keyType=false;//照明操作がされたか
	static boolean READ_NOTE=false;//ノートからの照明の読み込みが必要か
	static boolean READ_TIME=false;//時間からの照明の読み込みが必要か
	static boolean NOTE_UPDATE=true;//ノートのスクロールバーの更新フラグ
	static boolean CHANGE_FLG=true;
	static int bpm=180;
	static long thresh_mili_time=60000/bpm;
	static long key_time=0;
	static int LED=0;
	static int HA=0;
	static int halogen[]= {0,0,0,0};
	static int strobo_time=0;
	static boolean EYE=false;
	static int eye_dim=0;
	static boolean STROBO=false;
	static boolean IS_SET=false;
	//ステージの画像サイズ
	static int width=800;
	static int height=600;
	//windowのサイズ
	static int window_width=800;
	static int window_height=700;
	public static void main(String args[]) {
		ill=new Illumination();
		launch(args);
	}
	RecodeList rlist=new RecodeList();
	//音楽ファイル
	MediaPlayer mplayer = null;
	//再生ボタン
	Button b = new Button("---");
	//再生スライダ
	Slider slider = new Slider();
	//ボリュームスライダ
	Slider volume = new Slider();
	Label l = new Label("");
	//曲名＆ドロップ場所
	Label mname=new Label("Drag and Drop music file to this window!");

	//照明用ボタン
	Label lid=new Label("0000");
	ScrollBar sc = new ScrollBar();
	Button rec=new Button("rec");
	Button lplay=new Button("read");
	CheckBox llh=new CheckBox();
	CheckBox lch=new CheckBox();
	CheckBox rch=new CheckBox();
	CheckBox rrh=new CheckBox();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	ChoiceBox led = new ChoiceBox(FXCollections.observableArrayList(0,1,2,3,4,5,6,7,8,9,10,11,12));
	CheckBox st=new CheckBox();
	CheckBox ey=new CheckBox();
	Button add=new Button("add");
	Button rem=new Button("remove");
	Button clr=new Button("clear");
	Button save=new Button("save");
	Button opt=new Button("optimize");

	//recode用
	int Rlight;
	boolean recFlg=false;
	boolean readFlg=false;
	RecodeList RL=new RecodeList();
	int note=0;
	File mfile;

	boolean checkClicked(MouseEvent e,CheckBox b) {
		return b.isSelected();
	}

	void saveFunc(MouseEvent e) {
		File saveFile=new File(mname.getText().substring(0,mname.getText().lastIndexOf('.'))+".opu");
		try {
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(saveFile)));
			@SuppressWarnings("resource")
			FileChannel inCh=new FileInputStream(mfile).getChannel();
			@SuppressWarnings("resource")
			FileChannel outCh=new FileOutputStream(mfile.getName()).getChannel();
			inCh.transferTo(0, inCh.size(), outCh);
			inCh.close();
			outCh.close();
			pw.print(mname.getText()+",\r\n"+ RL.toString());
			pw.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}

	void recClicked(MouseEvent e) {
		recFlg=!recFlg;
		if(recFlg) {
			rec.setStyle("-fx-background-color: red; ");
		}else {
			rec.setStyle(null);
		}
	}

	void readClicked(MouseEvent e) {
		readFlg=!readFlg;
		if(readFlg) {
			lplay.setStyle("-fx-background-color: lime; ");
		}else {
			lplay.setStyle(null);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage)throws Exception{

		//レイアウトの設定
		primaryStage.setTitle("OPU Light System Simulation β1.0");
		slider.setPrefWidth(550);
		volume.setPrefWidth(100);
		b.setPrefWidth(60);
		led.setPrefWidth(40);
		led.setValue(0);
		sc.setMin(0);
		sc.setMax(0);
		sc.setUnitIncrement(1);
		sc.setBlockIncrement(1);
		MusicParam music = new MusicParam(slider,volume,l,mname,b);
		slider.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			CHANGE_FLG=false;
			READ_TIME=true;
			});
		b.addEventHandler(MouseEvent.MOUSE_CLICKED, e->CHANGE_FLG=false);
		FlowPane  pane = new FlowPane (5,5);
		pane.setPrefWidth(800);
		pane.getChildren().add( b );
		pane.getChildren().add( slider );
		pane.getChildren().add( l );
		Rectangle mbox=new Rectangle(10,605,280,18);
		mbox.setFill(Color.gray(0.8));
		mname.setPadding(new Insets(605, 0, 0, 12));
		pane.setPadding(new Insets(630, 0, 0, 10));
		HBox vbox=new HBox(5);
		vbox.getChildren().add(new Label("volume"));
		vbox.getChildren().add(volume);
		vbox.getChildren().add(save);
		vbox.getChildren().add(opt);
		vbox.setPadding(new Insets(605, 0, 0, 500));

		HBox illbox=new HBox(5);
		illbox.getChildren().add(rec);
		illbox.getChildren().add(lplay);
		illbox.getChildren().add(new Label("note: "));
		illbox.getChildren().add(lid);
		illbox.getChildren().add(sc);
		illbox.getChildren().add(new Label("ハロゲン"));
		illbox.getChildren().add(llh);
		illbox.getChildren().add(lch);
		illbox.getChildren().add(rch);
		illbox.getChildren().add(rrh);
		illbox.getChildren().add(new Label("目つぶし"));
		illbox.getChildren().add(ey);
		illbox.getChildren().add(new Label("ストロボ"));
		illbox.getChildren().add(st);
		illbox.getChildren().add(new Label("LED"));
		illbox.getChildren().add(led);
		illbox.getChildren().add(add);
		illbox.getChildren().add(rem);
		illbox.getChildren().add(clr);
		illbox.setPadding(new Insets(665, 0, 0, 10));

        Group root = new Group();
        Scene   scene   = new Scene( root);
		try {
			//Drag&Dropの設定
			scene.setOnDragOver(event -> {
			    Dragboard board = event.getDragboard();
			    if (board.hasFiles()) {
			        event.acceptTransferModes(TransferMode.MOVE);
			    }
			});
			scene.setOnDragDropped(event -> {
				Dragboard board = event.getDragboard();
				if (board.hasFiles()) {
					board.getFiles().forEach(file -> {
						if(isOpuFile(file)) {
							String mpath=getRL(file);
							//System.out.println(file.getParent());
							file=new File(file.getParent()+"\\"+mpath);
						}
						int flg=music.setPath(file.getAbsolutePath());
						mfile=file;
						if(!IS_SET&&flg==0) {
							rec.addEventHandler(MouseEvent.MOUSE_CLICKED, e->recClicked(e));
							lplay.addEventHandler(MouseEvent.MOUSE_CLICKED, e->readClicked(e));
							save.addEventHandler(MouseEvent.MOUSE_CLICKED, e->saveFunc(e));
							opt.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
								RL.optimize();
								int l=RL.getLength();
								sc.setMax(l-1);
								sc.setBlockIncrement((l/10)+1);
								});
							rem.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
								RL.remove(note);
								int l=RL.getLength();
								sc.setMax(l-1);
								sc.setBlockIncrement((l/10)+1);
								READ_TIME=true;
								CHANGE_FLG=false;
							});
							add.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
								if(CHANGE_FLG) {
									RL.change(new RecodeFunction(halogen,LED, STROBO, EYE, RL.get(note).getTime()),note);
								}else {
									RL.add(halogen,LED, STROBO, EYE, music.getTime(),true);
								}
								int l=RL.getLength();
								sc.setMax(l-1);
								sc.setBlockIncrement((l/10)+1);
								READ_TIME=true;
								CHANGE_FLG=true;
							});
							clr.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
								RL.clear();
								sc.setMin(0);
								sc.setMax(0);
								sc.setUnitIncrement(1);
								sc.setBlockIncrement(1);
								note=0;
								CHANGE_FLG=false;
							});
							IS_SET=true;
						}
						//System.out.println(file.getAbsolutePath());
					});
					event.setDropCompleted(true);
				} else {
			        event.setDropCompleted(false);
			    }
			});
		} catch (Exception e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
        primaryStage.setScene( scene );
		Canvas canvas = new Canvas( window_width, window_height );
        root.getChildren().add(canvas);
		root.getChildren().add(illbox);
		root.getChildren().add(mbox);
		root.getChildren().add(pane);
        root.getChildren().add(vbox);
		root.getChildren().add(mname);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		//ボタンの設定
		llh.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			halogen[3]=checkClicked(e,llh)? 1:0;
			keyType=true;
			});
		lch.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			halogen[2]=checkClicked(e,lch)? 1:0;
			keyType=true;
			});
		rch.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			halogen[1]=checkClicked(e,rch)? 1:0;
			keyType=true;
			});
		rrh.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			halogen[0]=checkClicked(e,rrh)? 1:0;
			keyType=true;
			});
		ey.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			EYE=checkClicked(e,ey);
			keyType=true;
			});
		st.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			STROBO=checkClicked(e,st);
			keyType=true;
			});
		sc.addEventFilter(MouseEvent.MOUSE_PRESSED, e->{
			note=(int)sc.getValue();
			NOTE_UPDATE=false;
			});
		sc.addEventFilter(MouseEvent.MOUSE_RELEASED, e->{
			note=(int)sc.getValue();
			music.seekTime(new Duration(RL.get(note).getTime()));
			/*
			System.out.println(new Duration(RL.get(note).getTime()).toMillis());
			System.out.println(RL.get(note).getTime());
			System.out.println(music.getTime());*/
			CHANGE_FLG=true;
			READ_NOTE=true;
			NOTE_UPDATE=true;
			});
		sc.addEventFilter(MouseEvent.MOUSE_DRAGGED, e->{
			NOTE_UPDATE=false;
			note=(int)sc.getValue();
			});
		led.setOnAction(e->{
			LED=(int)led.getValue();
			keyType=true;
			});


		//タイプキーの設定
		scene.setOnKeyPressed(
				new EventHandler<KeyEvent>(){
					public void handle(KeyEvent e){
						String code = e.getCode().toString();
						keyType=true;
						//System.out.println(code);//タイプしたキーを表示
						switch(code) {
						case "A":
							halogen[3]=1;
							break;
						case "S":
							halogen[3]=0;
							break;
						case "V":
							halogen[3]=(halogen[3]+1)%2;
							break;
						case "D":
							halogen[2]=1;
							break;
						case "F":
							halogen[2]=0;
							break;
						case "B":
							halogen[2]=(halogen[2]+1)%2;
							break;
						case "G":
							halogen[1]=1;
							break;
						case "H":
							halogen[1]=0;
							break;
						case "N":
							halogen[1]=(halogen[1]+1)%2;
							break;
						case "J":
							halogen[0]=1;
							break;
						case "K":
							halogen[0]=0;
							break;
						case "M":
							halogen[0]=(halogen[0]+1)%2;
							break;
						case "L":
							EYE=true;
							break;
						case "COMMA":
							EYE=!EYE;
							break;
						case "SEMICOLON":
							EYE=false;
							break;
						case "COLON":
							STROBO=true;
							break;
						case "CLOSE_BRACKET":
							STROBO=false;
							break;
						case "PERIOD":
							STROBO=!STROBO;
							break;
						case "DIGIT1":
							LED=1;
							break;
						case "DIGIT2":
							LED=2;
							break;
						case "DIGIT3":
							LED=3;
							break;
						case "DIGIT4":
							LED=4;
							break;
						case "DIGIT5":
							LED=5;
							break;
						case "DIGIT6":
							LED=6;
							break;
						case "DIGIT7":
							LED=7;
							break;
						case "DIGIT8":
							LED=8;
							break;
						case "DIGIT9":
							LED=9;
							break;
						case "DIGIT0":
							LED=10;
							break;
						case "MINUS":
							LED=11;
							break;
						case "CIRCUMFLEX":
							LED=12;
							break;
						case "BACK_SLASH":
							LED=0;
							break;
						}
					}
				});



		new AnimationTimer(){
			public void handle(long currentNanoTime) {
				READ_TIME=(readFlg&&music.isPlaying()&&!keyType)||READ_TIME;
				if(recFlg&&keyType&&music.isPlaying()) {
					RL.add(halogen,LED, STROBO, EYE, music.getTime());
					int l=RL.getLength();
					sc.setMax(l-1);
					sc.setBlockIncrement((l/10)+1);
					READ_TIME=false;
					READ_NOTE=false;
				}else if(READ_TIME) {
					note=RL.getNote(music.getTime());
					READ_NOTE=true;
				}
				if(READ_NOTE) {
					RecodeFunction rf=RL.get(note);
					halogen=rf.getHalogen();
					LED=rf.getLED();
					EYE=rf.getEye();
					STROBO=rf.getStrobo();
				}
				if(NOTE_UPDATE) {
					sc.setValue(note);
				}
				if(CHANGE_FLG) {
					add.setText("change");
				}else {
					add.setText("add");
				}
				READ_NOTE=false;
				READ_TIME=false;
				keyType=false;
		        HA=halogen[3]*8+halogen[2]*4+halogen[1]*2+halogen[0];
				if(!STROBO) {
					strobo_time=0;
				}else {
					strobo_time=(strobo_time+1)%4;
				}
				lid.setText(String.format("%04d", note));
				llh.setSelected(halogen[3]==1);
				lch.setSelected(halogen[2]==1);
				rch.setSelected(halogen[1]==1);
				rrh.setSelected(halogen[0]==1);
				led.setValue(LED);
				ey.setSelected(EYE);
				st.setSelected(STROBO);
				gc.setGlobalAlpha(1);
				gc.drawImage(ill.getStage(),0, 0,width, height);
				gc.setGlobalBlendMode(BlendMode.OVERLAY);
				gc.drawImage(ill.getHalogen(HA),0, 0,width, height);
				gc.drawImage(ill.getLED_back(LED),0, 0,width, height);
				//gc.setGlobalAlpha(0.6);
				gc.setGlobalAlpha(1);
				gc.setGlobalBlendMode(BlendMode.SRC_OVER);
				gc.setGlobalAlpha(1-(strobo_time/3));
				gc.drawImage(ill.getShade(HA),0, 0,width, height);
				key_time=(System.currentTimeMillis()-time)/thresh_mili_time;
				if(key_time>1) {
					time=System.currentTimeMillis();
					key_time=0;
				}
				gc.setGlobalAlpha(key_time);
				gc.drawImage(ill.getLED_front(LED)[0],0, 0,width, height);
				gc.setGlobalAlpha(1-key_time);
				gc.drawImage(ill.getLED_front(LED)[1],0, 0,width, height);
				if(EYE) {
					eye_dim=7;
				}else if(eye_dim>0){
					eye_dim--;
				}
				gc.setGlobalAlpha((double)eye_dim/7);
				gc.drawImage(ill.getEye(),0, 0,width, height);
			}
		}.start();
		primaryStage.show();
	}
	private String getRL(File file) {
		String rname;
		try(Scanner sc=new Scanner(file)){
			RL.clear(false);
			sc.useDelimiter(",");
			rname=sc.next();
			sc.nextLine();
			while(sc.hasNextLine()) {
				int[] hlgn=new int[]{sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt()};
				int le=sc.nextInt();
				boolean strb=sc.nextInt()==1;
				boolean mtbs=sc.nextInt()==1;
				double tt=sc.nextDouble();
				BitSet bs=new BitSet(7);
				for(int i=0;i<7;i++) {
					bs.set(i,sc.nextInt()==1);
				}
				RecodeFunction r=new RecodeFunction(hlgn,le,strb,mtbs,tt,bs);
				//System.out.println(hlgn[0]+","+hlgn[1]+","+hlgn[2]+","+hlgn[3]+","+le+","+strb+","+mtbs+","+tt+","+bs.get(0)+","+bs.get(1)+","+bs.get(2)+","+bs.get(3)+","+bs.get(4)+","+bs.get(5)+","+bs.get(6));
				RL.addSimply(r);
				sc.nextLine();
			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "OPUファイルが正しく読み込めません．", "ERROR",
			JOptionPane.ERROR_MESSAGE);
			RL.clear();
			return "ERR";
		}
		READ_TIME=true;
		int l=RL.getLength();
		sc.setMax(l-1);
		sc.setBlockIncrement((l/10)+1);
		return rname;
	}

	public boolean isOpuFile(File file) {
		return file.isFile() && file.canRead() && file.getPath().endsWith(".opu");
	}


}
