package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;

public class ColorUtils {

	public static Color hex2Rgb(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}

}
