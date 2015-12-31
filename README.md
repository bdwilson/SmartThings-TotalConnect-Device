SmartThings Honeywell TotalConnect Device
---
I didn't do any real work here, @mhatrey did (https://github.com/mhatrey/TotalConnect/blob/master/TotalConnect.groovy)

Installation
---
Please install the [SmartThings TotalConnect Tester
SmartApp](SmartThings-TotalConnect-Tester.groovy) to determine your LocationID
and DeviceID required for this device driver.  Once you figure out your
LocationID and DeviceID, then you can proceed to the steps below.  Make sure
your TotalConnect username has been configured to have the ARM/DISARM PIN
already configured in the GUI so that it's not required for whatever user you
select.  It's recommended you create a user specifically for this application
because of this.  For additional info on this, you can check this 
[thread](https://community.smartthings.com/t/new-app-integration-with-honeywell-totalconnect-alarm-monitoring-system/).

Then, 

1. Go to [My Device Types](https://graph.api.smartthings.com/ide/devices) and
create a new device using the above .groovy file.
2. Go to [My Devices](https://graph.api.smartthings.com/device/list) and create
a new device - like Honeywell Alarm.  Network Device ID can be
'''HONEYWELL_ALARM_1'''. Type will be
the type you configured in step 1.  Make sure Locaion and/or Hub is selected
and select create.
3. Go edit the new device you created and fill in the preferences (this is
where you'll need to use the info in the thread above to capture your
LocationId and DeviceId.


Bugs/Contact Info
-----------------
Bug me on Twitter at [@brianwilson](http://twitter.com/brianwilson) or email me [here](http://cronological.com/comment.php?ref=bubba).
