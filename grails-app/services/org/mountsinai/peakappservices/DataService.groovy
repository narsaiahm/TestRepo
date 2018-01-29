package org.mountsinai.peakappservices

import java.text.DecimalFormat
import java.util.Date;

import grails.transaction.Transactional
import groovy.json.JsonSlurper
import groovy.sql.Sql

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.runtime.DateGroovyMethods

/**
 * Data Service
 * @author anshulb
 *
 */
@Transactional
class DataService {

	def dataSource;

	def login(def paramsMap) {
		def returnMap = [:]
		try {

			paramsMap['isAuthzRequired'] = 'Y'
			paramsMap['appName'] = 'NPSI'
			paramsMap['groupName'] = 'NPSI_USER'

			def resultJSONString =  HTTPHelper.postHTTPCall(GlobalConstants.getAuthURLBase(),GlobalConstants.AUTH_SERVICE_ENDPOINT,paramsMap,'')

			if(resultJSONString != null){
				JSONObject jsonObject = new JSONObject(resultJSONString)

				if(jsonObject != null && "SUCCESS".equalsIgnoreCase(jsonObject.get("STATUS"))) {
					returnMap["STATUS"] = "SUCCESS"

					JSONObject jsonMsgObject = jsonObject.getJSONObject("MESSAGE")
					returnMap["MESSAGE"] = ['fullname' : jsonMsgObject.get("fullname"),'email':jsonMsgObject.get("email"),'userId':jsonMsgObject.get("userid")]

					return returnMap
				} else {

					returnMap["STATUS"] = "ERROR"
					returnMap["MESSAGE"] = ['error':"Please enter valid user id and password. Your account may be locked after 3 invalid attempt."]
					return returnMap
				}
			} else {
				returnMap["STATUS"] = "ERROR"
				returnMap["MESSAGE"] = ['error':"Invalid Response or Response Object is empty."]
				return returnMap
			}
		} catch(Exception e) {
			e.printStackTrace()
			returnMap["STATUS"] = "ERROR"
			returnMap["MESSAGE"] =['error':e.getMessage()]
			return returnMap
		}
	}

	def validateBarcode(def params) {
		def returnMap = [:]
		try {
			def resultMap = getPEAKUser(params)
			if(resultMap == null) {
				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error':params.networkId != null ? 'invalid network ID' : 'invalid life number']
				return returnMap
			}

			returnMap["STATUS"] = "SUCCESS"
			returnMap["MESSAGE"] = resultMap
		} catch(Exception e) {
			e.printStackTrace()
			returnMap["STATUS"] = "FAILED"
			returnMap["MESSAGE"] = ['error':"network / data error"]
		}
		return returnMap
	}

	def getCourseInformation(def params){

		println  "course id :" + params?.courseId

		def returnMap = [:]
		try {
			def cList = PeakCourse.findByCourseEnrollmentId(params?.courseId)

			if(cList !=  null){

				def responseMap = [:]
				cList.each {
					responseMap["courseName"] = cList.courseName
					responseMap["courseStart"] = cList.courseStartDate.format("MM/dd/yyyy HH:mm")
					responseMap["courseEnd"] = cList.courseEndDate.format("MM/dd/yyyy HH:mm")
					responseMap["courseID"] = cList.courseEnrollmentId
				}

				returnMap["STATUS"] = "SUCCESS"
				returnMap["MESSAGE"] = responseMap
			} else {
				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error':"network / data error"]
			}
		}catch(Exception e) {
			e.printStackTrace()
			returnMap["STATUS"] = "FAILED"
			returnMap["MESSAGE"] = ['error':"network / data error"]
		}

		return returnMap
	}

	def getAllCourses(params){

		def returnMap = [:]
		try {
			def coursesListByDate = PeakCourse.executeQuery( "from PeakCourse course where trunc(course.courseStartDate) = :dateCreated", [dateCreated : new Date().parse('MM/dd/yyyy', params.date)] );
			if(coursesListByDate != null){
				def responseMap = [:]
				def courseList = []
				coursesListByDate.each {
					def courseInfoMap = [:]
					courseInfoMap["courseName"] = it.courseName
					courseInfoMap["courseStart"] = it.courseStartDate.format("MM/dd/yyyy HH:mm")
					courseInfoMap["courseEnd"] = it.courseEndDate.format("MM/dd/yyyy HH:mm")
					courseInfoMap["courseID"] = it.courseEnrollmentId
					courseList << courseInfoMap
				}
				JSONArray courses = courseList
				responseMap["courses"]=courses
				returnMap["STATUS"] = "SUCCESS"
				returnMap["MESSAGE"] = responseMap
			} else {

				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error':"network / data error"]
			}
		}catch(Exception e) {
			e.printStackTrace()
			returnMap["STATUS"] = "FAILED"
			returnMap["MESSAGE"] = ['error':e.getMessage()]
		}

		return returnMap
	}

	private getPEAKUser(params) {
		def returnMap = null
		try {
			def queryString = """select ut.secid Life_Number, lower(laa.hospital_username) Network_ID, ut.lname Last_Name, ut.fname First_Name
                                 	from user_tab ut, lms_ad_accounts laa
                            	 		where (lower(laa.hospital_username) = lower(:networkId) or lower(ut.secid) = lower(:lifeNumber)) 
											and laa.userid = ut.objid """
			Sql sql = new Sql(dataSource_LMS)
			def sqlResult = sql.firstRow(queryString, ['networkId':params.networkId,'lifeNumber':params.lifenumber])

			if(sqlResult) {
				returnMap = [:]
				returnMap['life_number'] = sqlResult.get("LIFE_NUMBER")
				returnMap['first_name'] = sqlResult.get("FIRST_NAME")
				returnMap['last_name'] = sqlResult.get("LAST_NAME")
				returnMap['network_id'] = sqlResult.get("NETWORK_ID")
			}
		}catch (Exception e) {
			e.printStackTrace()
			return null
		}

		return returnMap
	}

	def addCheckIn(params){

		def returnMap = [:]
		try {
			JSONObject jsonObject = params['JSONObject']
			def jsonTempArray = jsonObject.get("Data")

			if(jsonTempArray instanceof JSONArray == false) {
				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error': "JSON Array error: "+ jsonObject.toString()]
				return returnMap
			}

			def jsonArray = jsonTempArray
			if(jsonArray && jsonArray.length()<1) {
				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error': "JSON Array error"]
				return returnMap
			}

			JSONObject requestData =jsonObject.get("Data")
			println  requestData.get("courseID") + " " + requestData.get("lifenumber") + " "+ requestData.get("status")


			//Getting PEAK User Details with lifenumber
			def userResultMap = getPEAKUser(requestData.get("lifenumber"))
			if(userResultMap == null) {
				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error':'invalid life number']
				return returnMap
			}


			Sql sql = new Sql(dataSource)

			def cehckin = new PeakCheckIn()

			cehckin.courseType = "ILT"
			cehckin.firstName = userResultMap['first_name']
			cehckin.lastName = userResultMap['last_name']
			def statusString = requestData.get("status").toString().toLowerCase().capitalize()
			def coursestatus = PeakCourseStatus.findByStatusDesc(statusString)
			cehckin.courseStatus = coursestatus
			def course = PeakCourse.findByCourseEnrollmentId(requestData.get("courseID"))
			cehckin.course = course
			cehckin.checkInDate = new Date(System.currentTimeMillis())
			cehckin.lifeNumber = requestData.get("lifenumber")
			cehckin.isActive = "Y"

			def checkInId = cehckin.save(FailOnError:true,flush:true)

			println "Added new Checking Entry with checkinID : " + checkInId.id

			//constructing success response message
			def responseMap =[:]
			responseMap['lifenumber'] = requestData.get("lifenumber")
			responseMap['status'] =  requestData.get("status")
			returnMap["STATUS"] = "SUCCESS"
			returnMap["MESSAGE"] = responseMap
			return returnMap

		}catch(Exception e) {

			e.printStackTrace()
			returnMap["STATUS"] = "FAILED"
			returnMap["MESSAGE"] = ['error':"network / data error"]
			return returnMap
		}
		return returnMap
	}

	def addDummyRecord(params){

		def returnMap = [:]

		DecimalFormat df = new DecimalFormat("000");
		try {
			JSONObject jsonObject = params['JSONObject']
			def jsonTempArray = jsonObject.get("Data")

			if(jsonTempArray instanceof JSONArray == false) {
				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error': "JSON Array error: "+ jsonObject.toString()]
				return returnMap
			}

			def jsonArray = jsonTempArray
			if(jsonArray && jsonArray.length()<1) {
				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error': "JSON Array error"]
				return returnMap
			}

			JSONObject requestData =jsonObject.get("Data")

			println  requestData.get("courseID") + " " + requestData.get("firstname") + " "+ requestData.get("lastname") + " "+requestData.get("status")


			Sql sql = new Sql(dataSource)

			def dummyRecord = new PeakDummyRecord()

			def course = PeakCourse.findByCourseEnrollmentId(requestData.get("courseID"))
			if(course?.id){

				dummyRecord.course = course

				List<PeakDummyRecord> courseListByEnrollmentId = PeakDummyRecord.findAllByCourse(course)
				println courseListByEnrollmentId
				if(courseListByEnrollmentId?.size() > 0){
					dummyRecord.dummyRecord ="external_"+requestData.get("courseID")+"_"+df.format(courseListByEnrollmentId.size() + 1)
				} else {
					dummyRecord.dummyRecord ="external_"+requestData.get("courseID")+"_"+df.format(1)
				}

				dummyRecord.firstName = requestData.get("firstname")
				dummyRecord.lastName = requestData.get("lastname")
				def statusString = requestData.get("status").toString().toUpperCase()
				def coursestatus = PeakCourseStatus.findByStatusDesc(statusString) 
				dummyRecord.courseStatus = coursestatus
				dummyRecord.dummyRecordDate = new Date()
				dummyRecord.isActive = "Y"


				def dummyRecordId = dummyRecord.save(failOnError:true,flush:true)

				println "Added new Dummy Record entry with dummy record ID : " + dummyRecordId?.id

				//constructing success response message
				def responseMap =[:]

				responseMap['coursename'] ="external_"+requestData.get("courseID")+"_"+dummyRecordId?.id
				responseMap['status'] =  requestData.get("status")
				returnMap["STATUS"] = "SUCCESS"
				returnMap["MESSAGE"] = responseMap

				return returnMap
			} else {

				returnMap["STATUS"] = "FAILED"
				returnMap["MESSAGE"] = ['error': "Invalid courseID :" + requestData.get("courseID")]
				return returnMap
			}




		}catch(Exception e) {

			e.printStackTrace()
			returnMap["STATUS"] = "FAILED"
			returnMap["MESSAGE"] = ['error':"network / data error"]
			return returnMap
		}
		return returnMap
	}
}
