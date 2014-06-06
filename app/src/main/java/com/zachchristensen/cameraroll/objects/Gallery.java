package com.zachchristensen.cameraroll.objects;

public class Gallery {
	
	String name;
	int photoCount;
	
	public Gallery(String n, int pc){
		this.name = n;
		this.photoCount = pc;
	}
	
	public void incrememntCount(){
		this.photoCount = this.photoCount + 1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}
}