package org.mountsinai.peakappservices

import java.text.SimpleDateFormat

class PeakDummyRecord {

	PeakCourse course
	String dummyRecord
	String firstName
	String lastName
	PeakCourseStatus courseStatus
	Date dummyRecordDate
	String isActive

	static constraints = {
		dummyRecord unique:true
		firstName nullable:true
		lastName nullable:true
		dummyRecordDate nullable:true
		isActive nullable:true
	}

	static mapping = {
		id generator: "sequence", params:[sequence:'dummy_record_id_seq']
		version false
	}
}
