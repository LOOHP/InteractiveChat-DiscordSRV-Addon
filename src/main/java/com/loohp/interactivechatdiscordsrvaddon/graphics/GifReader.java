package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.madgag.gif.fmsware.GifDecoder;

public class GifReader {

	public static ImageFrame[] readGif(InputStream stream) throws IOException {
		GifDecoder reader = new GifDecoder();
		if (reader.read(stream) == 0) {
			ImageFrame[] frames = new ImageFrame[reader.getFrameCount()];
			int width = (int) reader.getFrameSize().getWidth();
			int height = (int) reader.getFrameSize().getHeight();
			for (int i = 0; i < reader.getFrameCount(); i++) {
				BufferedImage image = reader.getFrame(i);
				int delay = reader.getDelay(i);
				frames[i] = new ImageFrame(image, delay, width, height);
			}
			return frames;
		} else {
			throw new IOException("Unable to read Gif");
		}
	}

}
