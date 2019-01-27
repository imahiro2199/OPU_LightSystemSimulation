package illumination_simulation.imagawa;

import java.io.File;

import javax.swing.JOptionPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class MusicParam {
	private MediaPlayer mplayer=null;
	private Slider slider;
	private Slider volume;
	private Label l;
	private Label mname;
	private Button b;
	private double time=0;
	private double length=0;
	private boolean mouse=false;
	private Media media;
	private final TimeListener TL=new TimeListener();
	MusicParam(Slider s,Slider v,Label ll, Label mn,Button bb){
		l=ll;
		slider=s;
		volume=v;
		b=bb;
		mname=mn;
		b.setText("---");
		volume.setMin(0.0);
		volume.setMax(1.0);
		volume.setValue(1.0);
	}

	int setPath(String path) {
		time=0;
		File path2=null;
		try {
			path2 = new File(path);
		} catch (Exception e2) {
			// TODO 自動生成された catch ブロック
			//e2.printStackTrace();
			JOptionPane.showMessageDialog(null, "音楽ファイルが存在しません．", "ERROR",
			JOptionPane.ERROR_MESSAGE);
			return -2;
		}
		try {
			media = new Media(path2.toURI().toString());
		} catch (Exception e1) {
			// TODO 自動生成された catch ブロック
			//e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "無効なファイルです．", "ERROR",
			JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		if(mplayer==null) {
			b.addEventHandler( MouseEvent.MOUSE_CLICKED ,  e -> buttonCliked( e ) );
		}else {
			mplayer.seek(mplayer.getStartTime());
			slider.setValue(0.0);
			mplayer.dispose();
		}
		mplayer = new MediaPlayer( media );
		mname.setText(path2.getName());
		b.setText("play ");
		mplayer.stop();
		//スライダーの最大最小を設定
		mplayer.setOnReady(() -> {
			slider.setMin(mplayer.getStartTime().toMillis());
			length=mplayer.getStopTime().toMillis();
			slider.setMax(length);
			//System.out.println(mplayer.getStopTime().toMillis());
		//曲の長さを表示
			l.setText(timeLabel(time));
		});

		mplayer.currentTimeProperty().addListener(TL);

		//再生しきった時の設定
 		mplayer.setOnEndOfMedia(()->{
				time=mplayer.getStartTime().toMillis();
				mplayer.seek(mplayer.getStartTime());
				mplayer.pause();
				slider.setValue(time);
				l.setText( timeLabel(time));
				b.setText("play ");
 		});

		// スライダを操作するとシークする
		EventHandler<MouseEvent>    sliderHandler   = ( e ) ->
		{
			// スライダを操作すると、シークする
			mplayer.pause();
			mplayer.seek( javafx.util.Duration.millis(slider.getValue() ) );
			mplayer.play();
			mouse=false;
  	     };
 		// スライダを操作してるとき
 		EventHandler<MouseEvent>    sliderHandler2   = ( e ) ->
 		{
 			mouse=true;
 			l.setText(timeLabel(slider.getValue() ));
 			time=slider.getValue();
 		};
 		slider.addEventFilter( MouseEvent.MOUSE_RELEASED , sliderHandler );
 		slider.addEventFilter( MouseEvent.MOUSE_DRAGGED , sliderHandler2 );
 		slider.addEventFilter( MouseEvent.MOUSE_PRESSED , sliderHandler2 );

		//slider.setValue( time);

		return 0;
	}
	public void buttonCliked( MouseEvent e ){
		playButton();
	}
	private String timeLabel(double time) {
		time=time>length?length:time;
		int mil=(int)time/10%100;
		int sec=(int)time/1000%60;
		int min=(int)time/1000/60;
		return String.format("time %02d : %02d . %02d / %02d : %02d . %02d", min, sec,mil,
				(int)(length)/1000/60, (int)(length)/1000%60,(int)(length)/10%100);
	}
	public double getTime() {
		if(mplayer==null) {
			return -1;
		}
		return mplayer.getCurrentTime().toMillis();
	}
	public boolean isPlaying() {
		return mplayer.getStatus()==Status.PLAYING;
	}
	public void seekTime(Duration time) {
		mplayer.seek(time);
	}

    private class TimeListener implements ChangeListener<Duration> {

        @Override
        public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
			if(!mouse) {
				time=mplayer.getCurrentTime().toMillis();
			//再生時間の更新
				l.setText( timeLabel(time));
				slider.setValue(time);
			}
			//音量の取得
 			mplayer.setVolume(volume.getValue());
        }
    }
    public String getName() {
    	return mname.getText();
    }
    public void playButton() {
    	if(mplayer==null) {
    		return;
    	}
        Status status = mplayer.getStatus();
        switch(status) {
        case PLAYING:
			mplayer.pause();
			b.setText("play ");
			break;
		case READY:
		case PAUSED:
		case STOPPED:
		default:
			mplayer.play();
			b.setText("pause");
			break;
        }
    }

}
