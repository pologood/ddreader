package com.dangdang.readerV5.reponse;

public class ChannelList {
	Integer channelId;
	String description;
	String icon;
	Integer subNumber;
	String title;
	
	//频道数据库中表字段
	Integer sale_id; //sale_id与channelId对应
	String sale_name; //sale_name与title对应
	Integer sub_number; //sub_number与subNumber对应
	public Integer getSale_id() {
		return sale_id;
	}
	public void setSale_id(Integer sale_id) {
		this.sale_id = sale_id;
	}
	public String getSale_name() {
		return sale_name;
	}
	public void setSale_name(String sale_name) {
		this.sale_name = sale_name;
	}
	public Integer getSub_number() {
		return sub_number;
	}
	public void setSub_number(Integer sub_number) {
		this.sub_number = sub_number;
	}
	
	public Integer getChannelId() {
		return channelId;
	}
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Integer getSubNumber() {
		return subNumber;
	}
	public void setSubNumber(Integer subNumber) {
		this.subNumber = subNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	

}
