package com.z.po;

public class CourseDetail {
	
	public String CourseName;
	private String CourseCode;
	private String CourseType;
	private String CourseGrade;
	private String CourseCredit;
	private String CourseRetake = "未重修";                //默认
	private String CourseRetakeGrade = "无";
	
	public CourseDetail() {
		// TODO Auto-generated constructor stub
	}
	
	public String getCourseName() {
		return CourseName;
	}
	public void setCourseName(String courseName) {
		CourseName = courseName;
	}
	public String getCourseCode() {
		return CourseCode;
	}
	public void setCourseCode(String courseCode) {
		CourseCode = courseCode;
	}
	public String getCourseType() {
		return CourseType;
	}
	public void setCourseType(String courseType) {
		CourseType = courseType;
	}
	public String getCourseGrade() {
		return CourseGrade;
	}
	public void setCourseGrade(String courseGrade) {
		CourseGrade = courseGrade;
	}
	public String getCourseCredit() {
		return CourseCredit;
	}
	public void setCourseCredit(String courseCredit) {
		CourseCredit = courseCredit;
	}
	public String getCourseRetake() {
		return CourseRetake;
	}
	public void setCourseRetake(String courseRetake) {
		CourseRetake = courseRetake;
	}
	public String getCourseRetakeGrade() {
		return CourseRetakeGrade;
	}
	public void setCourseRetakeGrade(String courseRetakeGrade) {
		CourseRetakeGrade = courseRetakeGrade;
	}
	
	
	
	

}
