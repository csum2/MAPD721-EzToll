package com.tanav.eztoll

object AppConst {
    const val PREF_UID = "uniqueID"                 //preference UID
    const val PREF_1ST_RUN_DATE = "1st_run_date"    //preference on store 1st run date of the app
    const val PREF_DO_TRACKING = "sp_key_do_tracking"       //preference enable tracking or not
    const val TEMP_OFF_DURATION = 60                //Duration to automatically resume the tracking in minutes
    const val ACTION_REMINDER = "com.tanav.eztoll.TrackToggleAlarmReceiver.ACTION_REMINDER"  //Intent filter to reminder alarm
    const val ACTION_BILLING = "com.tanav.eztoll.BillingReceiver.ACTION_BILLING"  //Intent filter to billingg alarm
    const val TOLL_ROAD_FILE = "toll_checkpoints.json"
    const val CHANNEL_ID = "eztollServiceChannel"
    const val CHANNEL_NAME = "Eztoll Start Service Channel"
    const val GPS_TOLERANCE = 0.000901              //100 meters. since 0.001 degree = 111 meters
    const val UNIT_CHARGES_PER_METER = 0.0002
}