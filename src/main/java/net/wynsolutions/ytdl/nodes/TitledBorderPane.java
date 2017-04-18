package net.wynsolutions.ytdl.nodes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
public class TitledBorderPane extends StackPane{
	
	  public TitledBorderPane(String titleString, Node content) {
	    Label title = new Label(" " + titleString + " ");  
	    title.setStyle("-fx-background-color: transparent; -fx-translate-y: -16;");
	    title.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
	    
	    StackPane.setAlignment(title, Pos.TOP_CENTER);

	    StackPane contentPane = new StackPane();
	    content.setStyle("-fx-padding: 7 5 5 5;");
	    contentPane.getChildren().add(content);

	    setStyle("-fx-border-color: lightskyblue; -fx-content-display: top; -fx-border-insets: 5 5 5 5; -fx-border-width: 2; -fx-border-radius: 4 4 4 4;");
	    getChildren().addAll(title, contentPane);
	}
	
}
