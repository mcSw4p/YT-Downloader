package net.wynsolutions.ytdl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoInfo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.wynsolutions.ytdl.nodes.TitledBorderPane;
 
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
public class Main extends Application{

	private Button startButton = new Button("Start"), cancelBtn = new Button("Cancel"), fileBtn = new Button("Folder");
	private CheckBox plexBox = new CheckBox("Plex Format"), standBox = new CheckBox("Standard Format"), convertBox = new CheckBox("Auto-Convert");
	private TextField urlField = new TextField(), pathField = new TextField(), episodeNameField, fileNameField, showNameField, movieNameField;
	private Label percent, speed;
	private TextFlow videoName;
	public static ProgressBar pb = new ProgressBar(0.01);
	private Spinner<Integer> spinner, spinner1, spinner2;
	private ImageView imv;
	private RadioButton plexTv, plexMovie;
	private TitledBorderPane plexBorder, standBorder;
	private HBox tvBox = new HBox(2), movieBox = new HBox(2);
	
	private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

	private Stage stage;
	private static Main instance;
	
	private Thread dlThread;

	@Override public void start(final Stage arg0) throws Exception {

		this.stage = arg0;
		instance = this;

		// Scene Layout Variables
		GridPane grid = new GridPane(), grid1 = new GridPane(), grid2 = new GridPane();
		BorderPane mainPane = new BorderPane();
		Scene scene = new Scene(mainPane, 450, 520);
		//
		
		// Tweaking Scene Layout
		grid.setAlignment(Pos.BOTTOM_CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 2, 25));

		grid1.setAlignment(Pos.TOP_CENTER);
		grid1.setHgap(10);
		grid1.setVgap(10);
		grid1.setPadding(new Insets(2, 25, 2, 25));
		
		grid2.setAlignment(Pos.TOP_CENTER);
		grid2.setHgap(10);
		grid2.setVgap(10);
		grid2.setPadding(new Insets(5, 25, 25, 25));
		
		mainPane.setTop(grid);
		mainPane.setCenter(grid1);
		mainPane.setBottom(grid2);
		
		arg0.setScene(scene);
		arg0.setTitle("YouTube Downloader");
		arg0.setResizable(false);
		
		scene.getStylesheets().add(Main.class.getResource("stylesheet.css").toExternalForm());
		//
		
		// Title
		Text scenetitle = new Text("Download Video File");
		//
		
		// URL Label
		Label urlName = new Label("URL:");
		urlName.setAlignment(Pos.CENTER_RIGHT);
		urlName.setPrefWidth(110.0);
		//
		
		// Download Speed, Percent, and Video Title & Videos Labels
		speed = new Label("(0.0 kb/s)");
		percent = new Label("(0.0%)");
		videoName = new TextFlow();
	    imv = new ImageView();
	    HBox imvBox = new HBox(2);
	    imvBox.getChildren().add(imv);
		//
		
		// Naming Format Label
		Label format = new Label("Select Naming Format:");
		//
		
		// Naming Format Checkboxs
		VBox formatBoxes = new VBox(2);
		formatBoxes.getChildren().addAll(plexBox, standBox);
		//
		
		// Plex Naming Titled Box
		VBox plexVBox = new VBox(2);
		HBox buttonsBox = new HBox(2);
		
		// Radio Buttons
		plexTv = new RadioButton("Tv Show");
		plexMovie = new RadioButton("Movie");
		
		buttonsBox.setAlignment(Pos.CENTER);
		
		buttonsBox.getChildren().addAll(plexTv, plexMovie);
		plexVBox.getChildren().add(buttonsBox);
		
		//Movie Box
		movieNameField = new TextField("Movie Name");
		
		spinner2 = new Spinner<Integer>();
		spinner2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1800, 3000, 2017, 1));
	    spinner2.setEditable(true);
	    spinner2.setPrefWidth(80.0);
	    
	    Label year = new Label("Year");
	    
	    movieBox.getChildren().addAll(movieNameField, year, spinner2);
	    plexVBox.getChildren().add(movieBox);
		movieBox.setDisable(true);
	    
		//Tv Show Box
		showNameField = new TextField("Show Name");
		
		spinner = new Spinner<Integer>();
		spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 1));
	    spinner.setEditable(true);
	    spinner.setPrefWidth(55.0);
	    
		spinner1 = new Spinner<Integer>();
		spinner1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 1));
	    spinner1.setEditable(true);
	    spinner1.setPrefWidth(55.0);
		
	    Label season = new Label("S"), episode = new Label("E");
	    episodeNameField = new TextField("Episode Name");
	    episodeNameField.setPrefWidth(100.0);
	    
		plexTv.setSelected(true);
	    
		tvBox.getChildren().addAll(showNameField, season, spinner, episode, spinner1, episodeNameField);
		plexVBox.getChildren().add(tvBox);
		plexBorder = new TitledBorderPane("Plex Naming", plexVBox);
		plexBorder.setDisable(true);
		//
		
		// Standard Naming Titled Box
		HBox standHbox = new HBox(2);
		fileNameField = new TextField();
		standHbox.getChildren().add(fileNameField);
		standBorder = new TitledBorderPane("Standard Naming", standHbox);
		standBorder.setDisable(true);
		//
	
		// Create Buttons Actions
		this.createButtonActions();
		
		// Add Nodes to First grid
		grid.add(scenetitle, 0, 0, 2, 1);
		grid.add(urlName, 0, 1);
		grid.add(this.urlField, 1, 1);
		grid.add(fileBtn, 0, 2);
		grid.add(pathField, 1, 2);
		//
		
		// Add Nodes to Second grid
		grid1.add(format, 0, 0);
		grid1.add(imvBox, 1, 0, 1, 2);
		grid1.add(formatBoxes, 0, 1);
		grid1.add(plexBorder, 0, 2, 2, 1);
		grid1.add(standBorder, 0, 3, 2, 1);
		//
		
		// Add Nodes to Third grid
		grid2.add(pb, 0, 1, 3, 1);
		
		BorderPane pane1 = new BorderPane();
		pane1.setLeft(speed);
		pane1.setRight(percent);
		grid2.add(pane1, 0, 2, 1, 2);
		
		BorderPane pane = new BorderPane();
		pane.setRight(startButton);
		pane.setCenter(cancelBtn);
		grid2.add(pane, 1, 2, 2 ,1);
		
		grid2.add(videoName, 0, 0, 3, 1);
		
		grid2.add(convertBox, 2, 3);
		//
		
		// Tweak sizing and alignment
		this.fileBtn.setPrefSize(70, 10);
		this.fileBtn.setAlignment(Pos.CENTER);
		urlField.setPrefWidth(340.0);
		
		pb.setPrefWidth(450.0);
		pane1.setPrefWidth(250.0);
		videoName.setPrefSize(450.0, 10.0);
		videoName.setTextAlignment(TextAlignment.CENTER);
		imv.setFitHeight(120.0);
		imv.setFitWidth(190.0);
		imvBox.setAlignment(Pos.CENTER_RIGHT);
		format.setAlignment(Pos.BASELINE_LEFT);
		
		plexBorder.setPrefWidth(410.0);
		standBorder.setPrefWidth(410.0);
		episodeNameField.setPrefWidth(150.0);
		fileNameField.setPrefWidth(410.0);
		showNameField.setPrefWidth(115.0);
		movieNameField.setPrefWidth(270.0);
		//
		
		// Settings fonts
		percent.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
		speed.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
		urlName.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		format.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
		plexBox.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		standBox.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		season.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		episode.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		year.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		startButton.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		cancelBtn.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		this.fileBtn.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		this.convertBox.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
		//
		
		arg0.show();
	}

	private void createButtonActions(){
		AtomicBoolean stop = new AtomicBoolean(false);

		this.fileBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				File selectedDirectory = 
						directoryChooser.showDialog(stage);

				if(selectedDirectory == null){
					pathField.setText("No Directory selected");
				}else{
					pathField.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});
		
		this.cancelBtn.setOnAction(new EventHandler<ActionEvent>(){

			public void handle(ActionEvent arg0) {
				stage.close();
				stop.set(true);
				
			}

		});
		
		this.plexMovie.setOnAction(new EventHandler<ActionEvent>() {

			@Override public void handle(ActionEvent event) {
				if(plexTv.isSelected()){
					plexMovie.setSelected(false);
					return;
				}
				
				if(plexMovie.isSelected()){
					movieBox.setDisable(false);
				}else{
					movieBox.setDisable(true);
				}
				
			}
		});
		
		this.plexTv.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override public void handle(ActionEvent event) {
				if(plexMovie.isSelected()){
					plexTv.setSelected(false);
					return;
				}
				
				if(plexTv.isSelected()){
					tvBox.setDisable(false);
				}else{
					tvBox.setDisable(true);
				}
				
			}
		});
		
		this.plexBox.setOnAction(new EventHandler<ActionEvent>(){

			@Override public void handle(ActionEvent arg0) {
				if(standBox.isSelected()){
					plexBox.setSelected(false);
					return;
				}
				
				if(plexBox.isSelected()){
					plexBorder.setDisable(false);
				}else{
					plexBorder.setDisable(true);
				}
			}
			
		});
		
		this.standBox.setOnAction(new EventHandler<ActionEvent>(){

			@Override public void handle(ActionEvent arg0) {
				if(plexBox.isSelected()){
					standBox.setSelected(false);
					return;
				}
				
				if(standBox.isSelected()){
					standBorder.setDisable(false);
				}else{
					standBorder.setDisable(true);
				}
			}
			
		});

		this.startButton.setOnAction(new EventHandler<ActionEvent>(){

			public void handle(ActionEvent arg0) {
				pathField.pseudoClassStateChanged(errorClass, false);
				urlField.pseudoClassStateChanged(errorClass, false);
				plexBox.pseudoClassStateChanged(errorClass, false);
				standBox.pseudoClassStateChanged(errorClass, false);

				boolean flag = true;

				if(urlField.getText().trim().equalsIgnoreCase("")){
					urlField.pseudoClassStateChanged(errorClass, true);
					flag = false;
				}


				if(pathField.getText().trim().length() == 0 || pathField.getText().trim().equalsIgnoreCase("No Directory selected")){
					pathField.pseudoClassStateChanged(errorClass, true);
					flag = false;
				}
				
				if(!plexBox.isSelected() && !standBox.isSelected()){
					
					plexBox.pseudoClassStateChanged(errorClass, true);
					standBox.pseudoClassStateChanged(errorClass, true);
					
					flag = false;
				}

				if(flag){
					
					String path = "";
					
					if(convertBox.isSelected()){
						TextInputDialog dialog = new TextInputDialog("C:\\ffmpeg\\bin\\");
						dialog.setTitle("Path Selector");
						dialog.setHeaderText("Enter Path to FFmpeg.");
						dialog.setContentText("Path:");

						// Traditional way to get the response value.
						Optional<String> result = dialog.showAndWait();
						if(result.isPresent()){
							path = result.get();
						}else{
							Platform.runLater( () -> Main.instance().unlock());
							return;
						}
					}
					
					try {
						URL url = new URL(urlField.getText().trim());
						VGetParser user = VGet.parser(url);
						VideoInfo videoinfo = user.info(url);
						VGet v = new VGet(videoinfo, new File(pathField.getText().trim()));
						VGetStatus notify = new VGetStatus(path, v.getVideo(), pb, instance);
						
						v.extract(user, stop, notify);
						Text dl = new Text("Downloading:\n"), title = new Text(videoinfo.getTitle());
						videoName.getChildren().clear();
						videoName.getChildren().addAll(dl, title);
						
						if(plexBox.isSelected()){
							
							if(plexTv.isSelected()){
								v.getVideo().setTitle(showNameField.getText() + " S" + String.format("%02d", spinner.getValue())
								+ "E" + String.format("%02d", spinner1.getValue()) + " " + episodeNameField.getText().trim());
							}else if(plexMovie.isSelected()){
								v.getVideo().setTitle(movieNameField.getText().trim() + " (" + spinner2.getValue() + ")");
							}else{
								
							}
							

						}else{
							v.getVideo().setTitle(fileNameField.getText().trim());
						}

						dlThread = new Thread(new Runnable(){

							public void run() {
								v.download(user, stop, notify);
							}

						});
						dlThread.start();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}

				}


				return;

			}

		});
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(String per) {
		percent.setText("(" + per + " %)");
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(String sped) {
		speed.setText("(" + sped + ")");
	}

	public void setUrlError(boolean flag){
		urlField.pseudoClassStateChanged(errorClass, flag);
	}
	
	public void setTitleMessage(String str){
		videoName.getChildren().clear();
		videoName.getChildren().addAll(new Text(str));
	}
	
	public void lockPlexBox(){
		this.plexBox.setDisable(true);
	}
	
	public void unlockPlexBox(){
		this.plexBox.setDisable(false);
	}

	public static Main instance(){
		return instance;
	}
	
	public void lock(){
		pathField.pseudoClassStateChanged(errorClass, false);
		urlField.pseudoClassStateChanged(errorClass, false);
		plexBox.pseudoClassStateChanged(errorClass, false);
		standBox.pseudoClassStateChanged(errorClass, false);
		
		startButton.setDisable(true);
		fileBtn.setDisable(true);
		pathField.setDisable(true);
		urlField.setDisable(true);
		lockPlexBox();
		standBox.setDisable(true);
		plexBorder.setDisable(true);
		standBorder.setDisable(true);
	}

	public void unlock(){
		pb.setProgress(0);
		setPercent("0.00");
		setSpeed("0.0 kb/s");
		this.videoName.getChildren().clear();
		this.videoName.getChildren().add(new Text("Finished"));

		startButton.setDisable(false);
		pathField.setDisable(false);
		urlField.setDisable(false);
		fileBtn.setDisable(false);
		
		unlockPlexBox();
		standBox.setDisable(false);
		plexBorder.setDisable(false);
		standBorder.setDisable(false);
	}

	/**
	 * @param imv the imv to set
	 */
	public void setImv(Image im) {
		this.imv.setImage(im);
	}

	/**
	 * @return the imv
	 */
	public ImageView getImv() {
		return imv;
	}
}
