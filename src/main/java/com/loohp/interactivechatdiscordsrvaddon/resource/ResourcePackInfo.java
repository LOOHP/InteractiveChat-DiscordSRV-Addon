package com.loohp.interactivechatdiscordsrvaddon.resource;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;

public class ResourcePackInfo {
	
	private boolean status;
	private boolean exist;
	private String rejectedReason;
	private String name;
	private int packFormat;
	private Component description;
	
	private ResourcePackInfo(String name, boolean status, boolean exist, String rejectedReason, int packFormat, Component description) {
		this.name = name;
		this.status = status;
		this.exist = exist;
		this.rejectedReason = rejectedReason;
		this.packFormat = packFormat;
		this.description = description;
	}
	
	public ResourcePackInfo(String name, boolean status, String rejectedReason, int packFormat, Component description) {
		this(name, status, true, rejectedReason, packFormat, description);
	}
	
	public ResourcePackInfo(String name, String rejectedReason) {
		this(name, false, false, rejectedReason, -1, null);
	}

	public boolean getStatus() {
		return status;
	}
	
	public String getRejectedReason() {
		return rejectedReason;
	}

	public boolean exists() {
		return exist;
	}

	public String getName() {
		return name;
	}

	public int getPackFormat() {
		return packFormat;
	}

	public Component getDescription() {
		return description;
	}

}
