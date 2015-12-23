SmartThings Honeywell TotalConnect Device
---
I didn't do any real work here, @mhatrey did (https://github.com/mhatrey/TotalConnect/blob/master/TotalConnect.groovy)

Installation
---
Please see his
[thread](https://community.smartthings.com/t/new-app-integration-with-honeywell-totalconnect-alarm-monitoring-system/)
about how to configure the settings for this ST Device.

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
