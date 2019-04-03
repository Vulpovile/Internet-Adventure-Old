package com.androdome.iadventure.utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class IconManager
{
	private static HashMap<String, BufferedImage> map = new HashMap<String, BufferedImage>();
	public static BufferedImage getImage(String location)
	{
		BufferedImage img = map.get(location);
		if(img == null)
		{
			try
			{
				img = ImageIO.read(IconManager.class.getResourceAsStream(location));
				map.put(location, img);
			}
			catch (Exception e)
			{
			}
		}
		return img;
	}
}
