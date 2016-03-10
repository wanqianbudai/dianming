package se.anyro.nfc_reader;

public class Course {
     String teacher;
     String teacher_class;
	 int code;//0注册失败，1，点名失败，2,注册成功，3，点名成功
	 String stuId;
	 String stuName;
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getTeacher_class() {
		return teacher_class;
	}
	public void setTeacher_class(String teacher_class) {
		this.teacher_class = teacher_class;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getStuId() {
		return stuId;
	}
	public void setStuId(String stuId) {
		this.stuId = stuId;
	}
	public String getStuName() {
		return stuName;
	}
	public void setStuName(String stuName) {
		this.stuName = stuName;
	}
     
}
