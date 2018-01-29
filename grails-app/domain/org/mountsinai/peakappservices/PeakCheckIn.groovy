package org.mountsinai.peakappservices

import java.text.SimpleDateFormat

class PeakCheckIn {
	
	String courseType
	String firstName
	String lastName
	PeakCourseStatus courseStatus
	PeakCourse course
	Date checkInDate
	String lifeNumber
	String isActive
	
    static constraints = {
		courseType nullable:true
		firstName nullable:true
		lastName nullable:true
		checkInDate nullable: true
		lifeNumber nullable:true
		isActive nullable:true 
    }
	
	static mapping = {
		id generator: 'sequence', params:[sequence:'checkin_id_seq']
		version false
	}

}
