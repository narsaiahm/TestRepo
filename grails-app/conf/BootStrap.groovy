import org.mountsinai.peakappservices.PeakCourse;
import org.mountsinai.peakappservices.PeakCourseStatus;

class BootStrap {

	def init = { servletContext ->
		
		if(!PeakCourseStatus.findByStatusDesc("Complete")){
			new PeakCourseStatus(statusDesc:"Complete",isActive:"Y").save(failOnError:true)
		}
		if(!PeakCourseStatus.findByStatusDesc("Failed")){
			new PeakCourseStatus(statusDesc:"Failed",isActive:"Y").save(failOnError:true)
		}
		if(!PeakCourseStatus.findByStatusDesc("No Show")){
			new PeakCourseStatus(statusDesc:"No Show",isActive:"Y").save(failOnError:true)
		}
		if(!PeakCourseStatus.findByStatusDesc("Incomplete")){
			new PeakCourseStatus(statusDesc:"Incomplete",isActive:"Y").save(failOnError:true)
		}
		
		if(!PeakCourse.findByCourseEnrollmentId("16830")){
			new PeakCourse (courseEnrollmentId :"16830",courseName:"Level 2: VAD Awareness- February 2017",courseStartDate:new Date(),courseEndDate:new Date(),activeStatus:"ACTIVE",iltTrackName:"IVD",iltCourseName:"MSH Nursing - Specialty Mandated Programs",isActive:"Y").save(failOnError:true)
		}
		if(!PeakCourse.findByCourseEnrollmentId("16831")){
			new PeakCourse (courseEnrollmentId :"16831",courseName:"NDNQI, February 2017",courseStartDate:new Date(),courseEndDate:new Date(),activeStatus:"ACTIVE",iltTrackName:"NDNQI",iltCourseName:"MSH Nursing - Specialty Mandated Programs",isActive:"Y").save()
		}
		if(!PeakCourse.findByCourseEnrollmentId("16832")){
			new PeakCourse (courseEnrollmentId :"16832",courseName:"NICHE, February 2017",courseStartDate:new Date(),courseEndDate:new Date(),activeStatus:"ACTIVE",iltTrackName:"NICHE",iltCourseName:"MSH Nursing - Specialty Mandated Programs",isActive:"Y").save()
		}
		if(!PeakCourse.findByCourseEnrollmentId("16833")){
			new PeakCourse (courseEnrollmentId :"16833",courseName:"POC- AVOX - February 2017",courseStartDate:new Date(),courseEndDate:new Date(),activeStatus:"ACTIVE",iltTrackName:"Point of Care Testing (Specialty)",iltCourseName:"MSH Nursing - Specialty Mandated Programs",isActive:"Y").save()
		}
		if(!PeakCourse.findByCourseEnrollmentId("16834")){
			new PeakCourse (courseEnrollmentId :"16834",courseName:"POC- CHEM 8, PT/INR,CKMB, PLATELET INHIBITION (I-STAT), February 2017",courseStartDate:new Date(),courseEndDate:new Date(),activeStatus:"ACTIVE",iltTrackName:"The Joint Commission Interactive",iltCourseName:"MSH Nursing - Specialty Mandated Programs",isActive:"Y").save()
		}
	}
	
	def destroy = {
	}
}
