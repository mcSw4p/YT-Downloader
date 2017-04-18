package net.wynsolutions.ytdl;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

/**
 * Copyright (C) 2017  Sw4p
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Sw4p
 *
 */
public class ConvertMedia {

	private String audioPath, videoPath, output;
	private String[] exeCmd;
	private ProcessBuilder pb;
	private boolean flag = false;

	public ConvertMedia(String audio, String video, String output) {

		this.audioPath = audio;
		this.videoPath = video;
		this.output = output;

		File f1 = new File(videoPath);
		String name = f1.getName();
		File f2 = new File(f1.getParent() + File.separatorChar + name.replaceAll(".mp4", "1.mp4"));

		f1.renameTo(f2);
		videoPath = f2.getAbsolutePath();

		this.exeCmd = new String[]{"ffmpeg", "-i", audioPath, "-i", videoPath, output};
		this.pb = new ProcessBuilder(exeCmd);
	}

	public ConvertMedia(String path, String audio, String video, String output) {

		this.audioPath = audio;
		this.videoPath = video;
		this.output = output;

		File f1 = new File(videoPath);
		String name = f1.getName();
		File f2 = new File(f1.getParent() + File.separatorChar + name.replaceAll(".mp4", "1.mp4"));

		f1.renameTo(f2);
		videoPath = f2.getAbsolutePath();

		this.exeCmd = new String[]{path + "ffmpeg", "-i", audioPath, "-i", videoPath, output};
		this.pb = new ProcessBuilder(exeCmd);
	}

	private void askForPath(){
		TextInputDialog dialog = new TextInputDialog("C:\\ffmpeg\\bin\\");
		dialog.setTitle("Path Selector");
		dialog.setHeaderText("Convertion has failed! This could be due to the system not being able to find ffmpeg.exe. Please enter ffmpeg's path.");
		dialog.setContentText("Path:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()){
			new ConvertMedia(result.get().trim(), audioPath, videoPath.replace("1.mp4", ".mp4"), output).convert();
			return;
		}else{
			Platform.runLater( () -> Main.instance().unlock());
		}
	}

	private void startConvertTitle(){
		new Thread(new Runnable(){
			@Override public void run() {
				int i = 0;
				while(!flag){
					if(i == 0){
						Platform.runLater( () -> Main.instance().setTitleMessage("Converting."));
						i++;
					}else if(i == 1){
						Platform.runLater( () -> Main.instance().setTitleMessage("Converting.."));
						i++;
					}else if(i == 2){
						Platform.runLater( () -> Main.instance().setTitleMessage("Converting..."));
						i = 0;
					}
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public boolean convert(){
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.INHERIT);

		new Thread(new Runnable(){

			@Override public void run() {

				Process p = null;
				try {
					p = pb.start();
				}catch (Exception ex) {
					p = null;
					Platform.runLater( () -> askForPath());
					return;
				}

				try {
					startConvertTitle();
					p.waitFor();
					new File(audioPath).delete();
					new File(videoPath).delete();
					flag = true;
					Platform.runLater( () -> Main.instance().unlock());
				} catch (InterruptedException e) {
					e.printStackTrace();
					p.destroy();
					return;
				}
				return;	
			}	
		}).start();

		return true;
	}

}
