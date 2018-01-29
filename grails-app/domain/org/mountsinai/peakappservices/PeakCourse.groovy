package org.mountsinai.peakappservices

import java.text.SimpleDateFormat

class PeakCourse {

	String courseEnrollmentId
	String courseName
	Date courseStartDate
	Date courseEndDate
	String activeStatus
	String iltTrackName
	String iltCourseName
	String isActive

	static constraints = {
		courseEnrollmentId unique:true
		courseName nullable: true
		courseStartDate nullable: true
		courseEndDate nullable: true
		activeStatus nullable:true
		iltTrackName nullable:true
		iltCourseName nullable:true
		isActive nullable:false
	}
	
	static mapping = {
		id generator: 'sequence', params:[sequence:'course_id_seq']
		version false
	}
	
}
