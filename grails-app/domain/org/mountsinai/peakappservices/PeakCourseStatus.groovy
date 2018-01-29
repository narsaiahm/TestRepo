package org.mountsinai.peakappservices

class PeakCourseStatus {
	
	String statusDesc
	String isActive
	
	static mapping = {
		id generator: "sequence", params:[sequence:'course_status_id_seq']
		version false
	}
		
	
}
