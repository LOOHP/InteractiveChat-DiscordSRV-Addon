package com.loohp.interactivechatdiscordsrvaddon.resource.texture;

import java.util.Collections;
import java.util.List;

public class TextureAnimation {
	
	private boolean interpolate;
	private int width;
	private int height;
	private int masterFrametime;
	private List<TextureAnimationFrames> frames;
	
	public TextureAnimation(boolean interpolate, int width, int height, int masterFrametime, List<TextureAnimationFrames> frames) {
		this.interpolate = interpolate;
		this.width = width;
		this.height = height;
		this.masterFrametime = masterFrametime;
		this.frames = Collections.unmodifiableList(frames);
	}

	public boolean isInterpolate() {
		return interpolate;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public boolean hasWidth() {
		return width >= 0;
	}

	public boolean hasHeight() {
		return height >= 0;
	}

	public int getMasterFrametime() {
		return masterFrametime;
	}
	
	public boolean hasMasterFrametime() {
		return masterFrametime >= 0;
	}

	public List<TextureAnimationFrames> getFrames() {
		return frames;
	}
	
	public static class TextureAnimationFrames {
		
		private int index;
		private int times;
		
		public TextureAnimationFrames(int index, int times) {
			this.index = index;
			this.times = times;
		}

		public int getIndex() {
			return index;
		}

		public int getTimes() {
			return times;
		}
		
		public boolean hasTimes() {
			return times >= 0;
		}
		
	}

}
