package org.mountsinai.peakappservices

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONException
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * This class will deals with the PEAK web services (login, validateBarcode ,getCourseInformation, getCourses, addCheckin, addDummyRecord and deleteDummyRecord)
 * @author anshulb
 *
 */
class ServiceController {

	static def dataService

	def index() { }

	/**
	 * This method validate the User authentication details passed as request parameter and response will send in JSON format.
	 * @params params
	 * @return JSON
	 */
	def login() {
		params['applicationName'] = 'PEAK Web Services'
		render dataService.login(params) as JSON
	}

	/**
	 * This method will validate the barcode details based on the parameter passed to the service either netwokID or lifeNumber.
	 * @return JSON
	 */
	def validateBarcode() {

		println "Calling Validate Bar Code API with params : "+ params

		render dataService.validateBarcode(params) as JSON
	}

	/**
	 * This method will return the course details as response to the coursId passed as parameter to the service.
	 * @param params
	 * @return JSON
	 */
	def getCourseInformation(def params){

		println "calling get Course  Information API and prams :"+ params

		render dataService.getCourseInformation(params) as JSON
	}

	/**
	 *
	 * @return JSON
	 */
	def getAllCourses(def params){

		println "calling Get All Courese API"

		render dataService.getAllCourses(params)  as JSON
	}



	def addCheckin(){
		
		def retutnMap = [:]
		JSONObject jsonDataObject = request.JSON

		params['JSONObject'] = jsonDataObject

		if(jsonDataObject == null || jsonDataObject.length()==0) {
			retutnMap['STATUS'] = 'FAILED'
			retutnMap['MESSAGE'] = ['error':'Missing JSON data']
		} else if(jsonDataObject.isNull('Data') == true ){
			retutnMap['STATUS'] = 'FAILED'
			retutnMap['MESSAGE'] = ['error':'Missing JSON deatils array data']
		} else {

			retutnMap = dataService.addCheckIn(params)
		}

		render retutnMap as JSON
	}
	
	
	def addDummyRecord(){
		
		def retutnMap = [:]
		JSONObject jsonDataObject = request.JSON

		params['JSONObject'] = jsonDataObject

	/*	if(jsonDataObject == null || jsonDataObject.length()==0) {
			retutnMap['STATUS'] = 'FAILED'
			retutnMap['MESSAGE'] = ['error':'Missing JSON data']
		} else {*/

			retutnMap = dataService.addDummyRecord(params)
	/*	}*/

		render retutnMap as JSON
	}
	

	/**
	 * Dummy data insertion to the local DB for the CHECKIN table
	 * @return null
	 */
	def addCheckinTest(){
		PeakCheckIn cehckin = new PeakCheckIn()

		cehckin.courseType = "ILT"
		cehckin.firstName = "First1"
		cehckin.lastName = "Last1"
		def coursestatus = PeakCourseStatus.findByStatusDesc("Complete")
		cehckin.courseStatus = coursestatus
		def course = PeakCourse.findByCourseEnrollmentId("16830")

		cehckin.course = course
		cehckin.checkInDate = new Date(System.currentTimeMillis())
		cehckin.lifeNumber = 774858
		cehckin.isActive = "Y"
		cehckin.save(FailOnError:true,flush:true)
	}

	/**
	 * Dummy data insertion to the local DB for DUMMAY RECORD Table
	 * @return null
	 */

	def dummyRecord(){
		PeakDummyRecord record = new PeakDummyRecord()

		def course = PeakCourse.findByCourseEnrollmentId("16831")

		record.course = course
		record.dummyRecord ="external_16831_001"
		record.firstName ="FN2"
		record.lastName  = "LN2"
		def coursestatus = PeakCourseStatus.findByStatusDesc("Complete")
		record.courseStatus = coursestatus
		record.dummyRecordDate = new Date(System.currentTimeMillis())
		record.isActive = "Y"
		record.save(FailOnError:true)
	}
}
