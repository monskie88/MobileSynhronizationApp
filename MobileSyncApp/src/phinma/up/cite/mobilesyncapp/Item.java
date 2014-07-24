package phinma.up.cite.mobilesyncapp;

public class Item implements Comparable<Item>{
	private String fileName, filePath, fileSize,fileType,  fileLastModified;
	private int fileIcon;
	public Item(String fileName, String filePath, String fileSize, String fileType,String fileLastModified, int fileIcon){
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileSize = fileSize;
		this.fileType = fileType;
		this.fileLastModified = fileLastModified;
		this.fileIcon = fileIcon;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public String getFileSize(){
		return fileSize;
	}
	
	public String getFileType(){
		return fileType;
	}
	
	public String getFileLastModified(){
		return fileLastModified;
	}
	
	public int getFileIcon(){
		return fileIcon;
	}

	@Override
	public int compareTo(Item another) {
		// TODO Auto-generated method stub
		if(!(this.fileName==null))  return this.fileName.toLowerCase().compareTo(another.getFileName().toLowerCase());
		else throw new IllegalArgumentException();
	}
	
}
