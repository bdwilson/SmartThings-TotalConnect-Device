/**
 *  TotalConnect Device API
 *
 *  Copyright 2015 Brian Wilson 
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  This Device is based on work by @mhatrey (https://github.com/mhatrey/TotalConnect/blob/master/TotalConnect.groovy)
 *  The goal of this is to expose the TotalConnect Alarm to be used in other routines and in modes.  To do this, I setup
 *  both lock and switch capabilities for it. Switch On = Armed Stay, Lock On = Armed Away, Switch/Lock Off = Disarm. 
 *  There are no tiles because I don't need them, but feel free to add them.  Also, you'll have to use @mhatrey's tester
 *  tool to get your deviceId and locationId.  See his thread for more info: 
 *   https://community.smartthings.com/t/new-app-integration-with-honeywell-totalconnect-alarm-monitoring-system/
 *
 */
 
preferences {
	// See above ST thread above on how to configure the user/password.  Make sure the usercode is configured
        // for whatever account you setup. That way, arming/disarming/etc can be done without passing a user code.
    	input("userName", "text", title: "Username", description: "Your username for TotalConnect")
    	input("password", "password", title: "Password", description: "Your Password for TotalConnect")
        // get this info by using https://github.com/mhatrey/TotalConnect/blob/master/TotalConnectTester.groovy 
        input("deviceId", "text", title: "Device ID - You'll have to look up", description: "Device ID")
        // get this info by using https://github.com/mhatrey/TotalConnect/blob/master/TotalConnectTester.groovy 
	input("locationId", "text", title: "Location ID - You'll have to look up", description: "Location ID")
    	input("applicationId", "text", title: "Application ID - It is '14588' currently", description: "Application ID")
    	input("applicationVersion", "text", title: "Application Version - use '3.0.32'", description: "Application Version")
}
metadata {
	definition (name: "TotalConnect Device", namespace: "bdwilson", author: "Brian Wilson") {
		capability "Lock"
		//capability "Polling"
		capability "Switch"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// Maybe someone will add Arm Away/Arm Stay/Disarm tiles for this??  And the home tile would show if it's armed/disarmed??
      
	}
}

// Login Function. Returns SessionID for rest of the functions
def login(token) {
	log.debug "Executed login"
	def paramsLogin = [
    		uri: "https://rs.alarmnet.com/TC21API/TC2.asmx/AuthenticateUserLogin",
    		body: [userName: settings.userName , password: settings.password, ApplicationID: settings.applicationId, ApplicationVersion: settings.applicationVersion]
    	]
	httpPost(paramsLogin) { responseLogin ->
    		token = responseLogin.data.SessionID 
       	}
       	log.debug "Smart Things has logged In. SessionID: ${token}" 
    	return token
} // Returns token      

// Logout Function. Called after every mutational command. Ensures the current user is always logged Out.
def logout(token) {
	log.debug "During logout - ${token}"
   	def paramsLogout = [
    		uri: "https://rs.alarmnet.com/TC21API/TC2.asmx/Logout",
    		body: [SessionID: token]
    		]
   	httpPost(paramsLogout) { responseLogout ->
        	log.debug "Smart Things has successfully logged out"
        }  
}

// Gets Panel Metadata. Takes token & location ID as an argument
Map panelMetaData(token, locationId) {
	def alarmCode
    	def lastSequenceNumber
    	def lastUpdatedTimestampTicks
    	def partitionId
 	def getPanelMetaDataAndFullStatus = [
    		uri: "https://rs.alarmnet.com/TC21API/TC2.asmx/GetPanelMetaDataAndFullStatus",
        	body: [ SessionID: token, LocationID: locationId, LastSequenceNumber: 0, LastUpdatedTimestampTicks: 0, PartitionID: 1]
    	]
   	httpPost(getPanelMetaDataAndFullStatus) {	response -> 
        	lastUpdatedTimestampTicks = response.data.PanelMetadataAndStatus.'@LastUpdatedTimestampTicks'
        	lastSequenceNumber = response.data.PanelMetadataAndStatus.'@ConfigurationSequenceNumber'
        	partitionId = response.data.PanelMetadataAndStatus.Partitions.PartitionInfo.PartitionID
        	alarmCode = response.data.PanelMetadataAndStatus.Partitions.PartitionInfo.ArmingState
                                                
    	}
	//log.debug "AlarmCode is " + alarmCode
  	return [alarmCode: alarmCode, lastSequenceNumber: lastSequenceNumber, lastUpdatedTimestampTicks: lastUpdatedTimestampTicks]
} //Should return alarmCode, lastSequenceNumber & lastUpdateTimestampTicks

// Arm Function. Performs arming function
def armAway() {        
       	def token = login(token)
    	def locationId = settings.locationId
    	def deviceId = settings.deviceId            
    	def paramsArm = [
    		uri: "https://rs.alarmnet.com/TC21API/TC2.asmx/ArmSecuritySystem",
    		body: [SessionID: token, LocationID: locationId, DeviceID: deviceId, ArmType: 0, UserCode: '-1']
    	]
   	httpPost(paramsArm) // Arming Function in away mode
        def metaData = panelMetaData(token, locationId) // Get AlarmCode
        while( metaData.alarmCode != 10201 ){ 
        	pause(3000) // 3 Seconds Pause to relieve number of retried on while loop
                metaData = panelMetaData(token, locationId)
        }  
        //log.debug "Home is now Armed successfully" 
        sendPush("Home is now Armed successfully")     
   	logout(token)
}

def armStay() {        
	def token = login(token)
        def locationId = settings.locationId
    	def deviceId = settings.deviceId
    	def paramsArm = [
    		uri: "https://rs.alarmnet.com/TC21API/TC2.asmx/ArmSecuritySystem",
    		body: [SessionID: token, LocationID: locationId, DeviceID: deviceId, ArmType: 1, UserCode: '-1']
    	]
   	httpPost(paramsArm) // Arming function in stay mode
    	def metaData = panelMetaData(token, locationId) // Gets AlarmCode
        while( metaData.alarmCode != 10203 ){ 
        	pause(3000) // 3 Seconds Pause to relieve number of retried on while loop
                metaData = panelMetaData(token, locationId)
        } 
        //log.debug "Home is now Armed for Night successfully"     
 	sendPush("Home is armed in Night mode successfully")
    	logout(token)
}

def disarm() {
	def token = login(token)
   	def locationId = settings.locationId
    	def deviceId = settings.deviceId
        def paramsDisarm = [
    		uri: "https://rs.alarmnet.com/TC21API/TC2.asmx/DisarmSecuritySystem",
    		body: [SessionID: token, LocationID: locationId, DeviceID: deviceId, UserCode: '-1']
    	]
   	httpPost(paramsDisarm)  
   	def metaData = panelMetaData(token, locationId) // Gets AlarmCode
        while( metaData.alarmCode != 10200 ){ 
        	pause(3000) // 3 Seconds Pause to relieve number of retried on while loop
                metaData = panelMetaData(token, locationId)
        }
        // log.debug "Home is now Disarmed successfully"   
        sendPush("Home is now Disarmed successfully")
	logout(token)
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

// handle commands.  If this isn't proof ST needs to handle a "Alarm/Monitoring"
// capability, nothing is. 
def lock() {
	log.debug "Executing 'Arm Away'"
    	armAway()
}

def unlock() {
	log.debug "Executing 'Disarm'"
    	disarm()
}

def on() {
	log.debug "Executing 'Arm Stay'"
    	armStay()
}

def off() {
	log.debug "Executing 'Disarm'"
    	disarm()
}

