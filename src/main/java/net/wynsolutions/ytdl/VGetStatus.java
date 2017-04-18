package net.wynsolutions.ytdl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;

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
public class VGetStatus implements Runnable {
    VideoInfo videoinfo;
    public ProgressBar progress;
    double var = 0;
    long last;
    private String path = "";

    Map<VideoFileInfo, SpeedInfo> map = new HashMap<VideoFileInfo, SpeedInfo>();
    Main app;

    public VGetStatus(VideoInfo i, ProgressBar pb, Main ap) {
        this.videoinfo = i;
        this.progress = pb;
        this.app = ap;
        Platform.runLater( () -> ap.lock());
    }
    
    public VGetStatus(String path, VideoInfo i, ProgressBar pb, Main ap) {
        this.videoinfo = i;
        this.progress = pb;
        this.app = ap;
        this.path = path;
        Platform.runLater( () -> ap.lock());
    }

    public SpeedInfo getSpeedInfo(VideoFileInfo dinfo) {
        SpeedInfo speedInfo = map.get(dinfo);
        if (speedInfo == null) {
            speedInfo = new SpeedInfo();
            speedInfo.start(dinfo.getCount());
            map.put(dinfo, speedInfo);
        }
        return speedInfo;
    }
    
    private void askToConvert(String audio, String video, String output){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Convert with FFmpeg?");
    	alert.setHeaderText("Would you like me to convert this multipart download using FFmpeg?");
    	alert.setContentText("* You must already have FFmpeg installed!");

    	ButtonType buttonTypeOne = new ButtonType("Website");
    	ButtonType buttonTypeTwo = new ButtonType("Convert");
    	ButtonType buttonTypeNo = new ButtonType("No", ButtonData.CANCEL_CLOSE);

    	alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeNo);

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == buttonTypeOne){
    	    // Open FFmpeg's website
    		java.awt.Desktop myNewBrowserDesktop = java.awt.Desktop.getDesktop();
    		if(myNewBrowserDesktop != null){
    			try {
    			       java.net.URI myNewLocation = new java.net.URI("https://ffmpeg.org/");
    			      myNewBrowserDesktop.browse(myNewLocation);
    			  }catch(Exception e) {
    				  e.printStackTrace();
    			   }
    			Platform.runLater( () -> app.unlock());
    		}
    	} else if (result.get() == buttonTypeTwo) {
    	    // Convert
    		
    		new ConvertMedia(path, audio, video, output).convert();
    		
    	}else{
    		Platform.runLater( () -> app.unlock());
    	}
    }

    public void run() {
        List<VideoFileInfo> dinfoList = videoinfo.getInfo();

        // notify app or save download state
        // you can extract information from DownloadInfo info;
        switch (videoinfo.getState()) {
        case EXTRACTING:
        case EXTRACTING_DONE:
        	Platform.runLater( () -> app.setTitleMessage("Extracting Information..."));
        	break;
        case DONE:
        	progress.setProgress(100.0);
        	Platform.runLater( () -> app.setPercent("0"));
        	Platform.runLater( () -> app.setSpeed("0 kb/s"));
        	
            if (videoinfo instanceof YouTubeInfo) {
                YouTubeInfo i = (YouTubeInfo) videoinfo;
                System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
            } else if (videoinfo instanceof VimeoInfo) {
                VimeoInfo i = (VimeoInfo) videoinfo;
                System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
            } else {
                System.out.println("downloading unknown quality");
                Platform.runLater( () -> app.setUrlError(true));
            }
            int c = 0;
            for (VideoFileInfo d : videoinfo.getInfo()) {
            	
        		String pathWebm = d.getTarget().getAbsolutePath(), parent = d.getTarget().getParent(),
        				title = videoinfo.getTitle();
            	if(c>0 && path.equals("")){
            		Platform.runLater( () -> this.askToConvert(pathWebm,
            				parent + File.separatorChar + title + ".mp4", parent + File.separatorChar + title + ".mp4"));
            	}else if(!path.equals("") && c>0){
            		new ConvertMedia(path, pathWebm, parent + File.separatorChar + title + ".mp4", parent + File.separatorChar + title + ".mp4").convert();
            	}
            	
                SpeedInfo speedInfo = getSpeedInfo(d);
                speedInfo.end(d.getCount());
                System.out.println(String.format("file:%d - %s (%s)", dinfoList.indexOf(d), d.targetFile,
                        formatSpeed(speedInfo.getAverageSpeed())));
                c++;
            }
            
            if(c == 1)
            	Platform.runLater( () -> app.unlock());
            break;
        case ERROR:
            System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());
            Platform.runLater( () -> app.setUrlError(true));
            Platform.runLater( () -> app.setTitleMessage("Error downloading."));
            if (dinfoList != null) {
                for (DownloadInfo dinfo : dinfoList) {
                    System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getException() + " delay:"
                            + dinfo.getDelay());
                }
            }
            break;
        case RETRYING:
            System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());
            Platform.runLater( () -> app.setTitleMessage("Retrying download..."));
            if (dinfoList != null) {
                for (DownloadInfo dinfo : dinfoList) {
                    System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getState() + " "
                            + dinfo.getException() + " delay:" + dinfo.getDelay());
                }
            }
            break;
        case DOWNLOADING:
            long now = System.currentTimeMillis();
            if (now - 1000 > last) {
            	
                last = now;
                String parts = "";
                double low = 1000;
                final double flow;
                List<Double> values = new ArrayList<Double>();
                
                for (VideoFileInfo dinfo : dinfoList) {
                	
                	if(dinfo.getCount() != dinfo.getLength()){
                		SpeedInfo speedInfo = getSpeedInfo(dinfo);
                        speedInfo.step(dinfo.getCount());
                        values.add((double) (dinfo.getCount() / (float) dinfo.getLength()));

                        List<Part> pp = dinfo.getParts();
                        if (pp != null) {
                            // multipart download
                            for (Part p : pp) {
                                if (p.getState().equals(States.DOWNLOADING)) {
                                    parts += String.format("part#%d(%.2f) ", p.getNumber(),
                                            p.getCount() / (float) p.getLength());
                                }
                                
                            }
                        }
                       
                        System.out.println(String.format("file:%d - %s %.2f %s (%s)", dinfoList.indexOf(dinfo),
                                videoinfo.getState(), dinfo.getCount() / (float) dinfo.getLength(), parts,
                                formatSpeed(speedInfo.getCurrentSpeed())));
                        Platform.runLater( () ->  app.setSpeed(formatSpeed(speedInfo.getCurrentSpeed())));
                	}	            
                }

                for(double d : values)
                	if(d < low)
                		low = d;
                
                flow = low;

                progress.setProgress(low);
               
                Platform.runLater( () -> app.setPercent(String.format("%.2f", flow * 100)));
                Platform.runLater( () -> app.setTitleMessage("Downloading:\n" + videoinfo.getTitle()));
                Platform.runLater( () -> app.setImv(new Image(videoinfo.getIcon().toString())));
                Platform.runLater( () -> app.getImv().resize(10.0, 10.0));
                
            }
            break;
        default:
            break;
        }
    }
    
    public static String formatSpeed(long s) {
        if (s > 0.1 * 1024 * 1024 * 1024) {
            float f = s / 1024f / 1024f / 1024f;
            return String.format("%.1f GB/s", f);
        } else if (s > 0.1 * 1024 * 1024) {
            float f = s / 1024f / 1024f;
            return String.format("%.1f MB/s", f);
        } else {
            float f = s / 1024f;
            return String.format("%.1f kb/s", f);
        }
    }
}
