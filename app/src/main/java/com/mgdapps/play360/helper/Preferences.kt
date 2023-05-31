package com.mgdapps.play360.helper

import android.content.Context
import androidx.preference.PreferenceManager

class Preferences {
    var uid: String? = ""
    var displayName: String? = ""
    var email: String? = ""
    var profilePic: String? = ""
    var isProfilePrivare = false
    var isSound = true
    var isVibration = true

    fun savePreferences(context: Context?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(Constants.UID, uid)
        editor.putString(Constants.DisplayName, displayName)
        editor.putString(Constants.Email, email)
        editor.putString(Constants.ProfilePic, profilePic)
        editor.putBoolean(Constants.isProfilePrivare, isProfilePrivare)
        editor.putBoolean(Constants.isSound, isSound)
        editor.putBoolean(Constants.isVibration, isVibration)

        editor.apply()

    }

    fun loadPreferences(context: Context?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        uid = preferences.getString(Constants.UID, "")
        displayName = preferences.getString(Constants.DisplayName, "")
        email = preferences.getString(Constants.Email, "")
        profilePic = preferences.getString(Constants.ProfilePic, "")
        isProfilePrivare = preferences.getBoolean(Constants.isProfilePrivare, false)
        isSound = preferences.getBoolean(Constants.isSound, true)
        isVibration = preferences.getBoolean(Constants.isVibration, true)

    }

    fun clearPreferences(context: Context?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}