package com.loohp.interactivechatdiscordsrvaddon.objectholders;

public class AttachmentData {
	
	private String name;
	private byte[] data;
	
	public AttachmentData(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}
	
	public byte[] getData() {
		return data;
	}

}
