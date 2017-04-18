package net.wynsolutions.ytdl.config.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import net.wynsolutions.ytdl.config.Configuration;
import net.wynsolutions.ytdl.config.ConfigurationProvider;
import net.wynsolutions.ytdl.config.YamlConfiguration;

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
public class FileTitlesConfig {
	private File configFile;
	private Configuration configuration;
	
	private List<String> tv_show_titles = new ArrayList<String>();
	
	public FileTitlesConfig(String dFolder) {
		
		configFile = new File(dFolder, "ytdl-db.yml");

        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("src/main/resources/ytdl-db.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
			
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setTv_show_titles(configuration.getStringList("tv-shows-titles"));
		
	}
	
	public void saveConfig(){
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getResourceAsStream(String par1){
		return  getClass().getClassLoader().getResourceAsStream(par1);
	}

	/**
	 * @return the tv_show_titles
	 */
	public List<String> getTv_show_titles() {
		return tv_show_titles;
	}

	/**
	 * @param tv_show_titles the tv_show_titles to set
	 */
	public void setTv_show_titles(List<String> tv_show_titles) {
		this.tv_show_titles = tv_show_titles;
	}
}
