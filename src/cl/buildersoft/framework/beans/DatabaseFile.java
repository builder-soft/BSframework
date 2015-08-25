package cl.buildersoft.framework.beans;
@Deprecated 
public class DatabaseFile extends BSBean { 
	private static final long serialVersionUID = 6072774469939611902L;
	private String desc = null;
	private String content = null;
	private String fileName = null;
	private Long size = null;
	private Long category = null;
	private String TABLE = "tFile";
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Long getCategory() {
		return category;
	}
	public void setCategory(Long category) {
		this.category = category;
	}
}
