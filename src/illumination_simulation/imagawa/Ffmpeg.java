package illumination_simulation.imagawa;

import java.util.ArrayList;
import java.util.List;

public class Ffmpeg {
	List<String>cmd=new ArrayList<String>();
	void makemp4() {
		cmd.add("ffmpeg");
		cmd.add("-r");
		cmd.add("60");
		cmd.add("-i");
		cmd.add("image_%04d.png");
		cmd.add("-vcodec");
		cmd.add("libx264");
		cmd.add("-pix_fmt");
		cmd.add("yuv420p");
		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		// 標準エラーを標準出力にマージする。
		processBuilder.redirectErrorStream(true);
	}
}