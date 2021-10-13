package com.ionutv.classroomplus.ui

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import java.util.*

object ClassroomPlusSharedPreferences {
private val DEFAULT_SUFFIX = "_preferences"
private val LENGTH = "#LENGTH"
private var mClassroomPlusSharedPreferences: SharedPreferences? = null

private fun initPrefs(context: Context, prefsName: String?, mode: Int) {
        if (mClassroomPlusSharedPreferences == null) {
        mClassroomPlusSharedPreferences = context.getSharedPreferences(prefsName, mode)
        }
        }

/**
 * Returns the underlying SharedPreference instance
 *
 * @return an instance of the SharedPreference
 * @throws RuntimeException if SharedPreference instance has not been instantiated yet.
 */
private fun getPreferences(): SharedPreferences {
        val sharedPreferences = mClassroomPlusSharedPreferences
        if (sharedPreferences != null) {
        return sharedPreferences
        }
        throw java.lang.RuntimeException(
        "Class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate.")
        }

        /**
         * @return Returns a map containing a list of pairs key/value representing
         * the preferences.
         * @see android.content.SharedPreferences.getAll
         */
        fun getAll(): Map<String?, *>? {
        return getPreferences().all
        }

        /**
         * Retrieves a stored int value.
         *
         * @param key      The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         * @return Returns the preference value if it exists, or defValue.
         * @throws ClassCastException if there is a preference with this name that is not
         * an int.
         * @see android.content.SharedPreferences.getInt
         */
        fun getInt(key: String?, defValue: Int): Int {
        return getPreferences().getInt(key, defValue)
        }

        /**
         * Retrieves a stored int value, or 0 if the preference does not exist.
         *
         * @param key The name of the preference to retrieve.
         * @return Returns the preference value if it exists, or 0.
         * @throws ClassCastException if there is a preference with this name that is not
         * an int.
         * @see android.content.SharedPreferences.getInt
         */
        fun getInt(key: String?): Int {
        return getPreferences().getInt(key, 0)
        }

        /**
         * Retrieves a stored boolean value.
         *
         * @param key      The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         * @return Returns the preference value if it exists, or defValue.
         * @throws ClassCastException if there is a preference with this name that is not a boolean.
         * @see android.content.SharedPreferences.getBoolean
         */
        fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return getPreferences().getBoolean(key, defValue)
        }

        /**
         * Retrieves a stored boolean value, or false if the preference does not exist.
         *
         * @param key The name of the preference to retrieve.
         * @return Returns the preference value if it exists, or false.
         * @throws ClassCastException if there is a preference with this name that is not a boolean.
         * @see android.content.SharedPreferences.getBoolean
         */
        fun getBoolean(key: String?): Boolean {
        return getPreferences().getBoolean(key, false)
        }

        /**
         * Retrieves a stored long value.
         *
         * @param key      The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         * @return Returns the preference value if it exists, or defValue.
         * @throws ClassCastException if there is a preference with this name that is not a long.
         * @see android.content.SharedPreferences.getLong
         */
        fun getLong(key: String?, defValue: Long): Long {
        return getPreferences().getLong(key, defValue)
        }

        /**
         * Retrieves a stored long value, or 0 if the preference does not exist.
         *
         * @param key The name of the preference to retrieve.
         * @return Returns the preference value if it exists, or 0.
         * @throws ClassCastException if there is a preference with this name that is not a long.
         * @see android.content.SharedPreferences.getLong
         */
        fun getLong(key: String?): Long {
        return getPreferences().getLong(key, 0L)
        }

        /**
         * Returns the double that has been saved as a long raw bits value in the long preferences.
         *
         * @param key      The name of the preference to retrieve.
         * @param defValue the double Value to return if this preference does not exist.
         * @return Returns the preference value if it exists, or defValue.
         * @throws ClassCastException if there is a preference with this name that is not a long.
         * @see android.content.SharedPreferences.getLong
         */
        fun getDouble(key: String?, defValue: Double): Double {
        return java.lang.Double.longBitsToDouble(getPreferences().getLong(key, java.lang.Double.doubleToLongBits(defValue)))
        }

        /**
         * Returns the double that has been saved as a long raw bits value in the long preferences.
         * Returns 0 if the preference does not exist.
         *
         * @param key The name of the preference to retrieve.
         * @return Returns the preference value if it exists, or 0.
         * @throws ClassCastException if there is a preference with this name that is not a long.
         * @see android.content.SharedPreferences.getLong
         */
        fun getDouble(key: String?): Double {
        return java.lang.Double.longBitsToDouble(getPreferences().getLong(key, java.lang.Double.doubleToLongBits(0.0)))
        }

        /**
         * Retrieves a stored float value.
         *
         * @param key      The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         * @return Returns the preference value if it exists, or defValue.
         * @throws ClassCastException if there is a preference with this name that is not a float.
         * @see android.content.SharedPreferences.getFloat
         */
        fun getFloat(key: String?, defValue: Float): Float {
        return getPreferences().getFloat(key, defValue)
        }

        /**
         * Retrieves a stored float value, or 0 if the preference does not exist.
         *
         * @param key The name of the preference to retrieve.
         * @return Returns the preference value if it exists, or 0.
         * @throws ClassCastException if there is a preference with this name that is not a float.
         * @see android.content.SharedPreferences.getFloat
         */
        fun getFloat(key: String?): Float {
        return getPreferences().getFloat(key, 0.0f)
        }

        /**
         * Retrieves a stored String value.
         *
         * @param key      The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         * @return Returns the preference value if it exists, or defValue.
         * @throws ClassCastException if there is a preference with this name that is not a String.
         * @see android.content.SharedPreferences.getString
         */
        fun getString(key: String?, defValue: String?): String? {
        return getPreferences().getString(key, defValue)
        }

        /**
         * Retrieves a stored String value, or an empty string if the preference does not exist.
         *
         * @param key The name of the preference to retrieve.
         * @return Returns the preference value if it exists, or "".
         * @throws ClassCastException if there is a preference with this name that is not a String.
         * @see android.content.SharedPreferences.getString
         */
        fun getString(key: String?): String? {
        return getPreferences().getString(key, "")
        }

/**
 * Retrieves a Set of Strings as stored by [.putStringSet]. On Honeycomb and
 * later this will call the native implementation in SharedPreferences, on older SDKs this will
 * call [.getOrderedStringSet].
 * **Note that the native implementation of [SharedPreferences.getStringSet] does not reliably preserve the order of the Strings in the Set.**
 *
 * @param key      The name of the preference to retrieve.
 * @param defValue Value to return if this preference does not exist.
 * @return Returns the preference values if they exist, or defValues otherwise.
 * @throws ClassCastException if there is a preference with this name that is not a Set.
 * @see android.content.SharedPreferences.getStringSet
 * @see .getOrderedStringSet
 */
    fun getStringSet(key: String, defValue: Set<String?>?): Set<String?>? {
        val prefs = getPreferences()
        return prefs.getStringSet(key, defValue)
        }

        /**
         * Retrieves a Set of Strings as stored by [.putOrderedStringSet],
         * preserving the original order. Note that this implementation is heavier than the native
         * [.getStringSet] method (which does not guarantee to preserve order).
         *
         * @param key      The name of the preference to retrieve.
         * @param defValue Value to return if this preference does not exist.
         * @return Returns the preference value if it exists, or defValues otherwise.
         * @throws ClassCastException if there is a preference with this name that is not a Set of
         * Strings.
         * @see .getStringSet
         */
        fun getOrderedStringSet(key: String, defValue: Set<String?>?): Set<String?>? {
        val prefs = getPreferences()
        if (prefs.contains(key + LENGTH)) {
        val set = LinkedHashSet<String?>()
        val stringSetLength = prefs.getInt(key + LENGTH, -1)
        if (stringSetLength >= 0) {
        for (i in 0 until stringSetLength) {
        set.add(prefs.getString("$key[$i]", null))
        }
        }
        return set
        }
        return defValue
        }

        /**
         * Stores a long value.
         *
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
         * @see android.content.SharedPreferences.Editor.putLong
         */
        fun putLong(key: String?, value: Long) {
        val editor = getPreferences().edit()
        editor.putLong(key, value)
        editor.apply()
        }

        /**
         * Stores an integer value.
         *
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
         * @see android.content.SharedPreferences.Editor.putInt
         */
        fun putInt(key: String?, value: Int) {
        val editor = getPreferences().edit()
        editor.putInt(key, value)
        editor.apply()
        }

        /**
         * Stores a double value as a long raw bits value.
         *
         * @param key   The name of the preference to modify.
         * @param value The double value to be save in the preferences.
         * @see android.content.SharedPreferences.Editor.putLong
         */
        fun putDouble(key: String?, value: Double) {
        val editor = getPreferences().edit()
        editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
        editor.apply()
        }

        /**
         * Stores a float value.
         *
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
         * @see android.content.SharedPreferences.Editor.putFloat
         */
        fun putFloat(key: String?, value: Float) {
        val editor = getPreferences().edit()
        editor.putFloat(key, value)
        editor.apply()
        }

        /**
         * Stores a boolean value.
         *
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
         * @see android.content.SharedPreferences.Editor.putBoolean
         */
        fun putBoolean(key: String?, value: Boolean) {
        val editor = getPreferences().edit()
        editor.putBoolean(key, value)
        editor.apply()
        }

        /**
         * Stores a String value.
         *
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
         * @see android.content.SharedPreferences.Editor.putString
         */
        fun putString(key: String?, value: String?) {
        val editor = getPreferences().edit()
        editor.putString(key, value)
        editor.apply()
        }

/**
 * Stores a Set of Strings. On Honeycomb and later this will call the native implementation in
 * SharedPreferences.Editor, on older SDKs this will call [.putOrderedStringSet].
 * **Note that the native implementation of [Editor.putStringSet] does not reliably preserve the order of the Strings in the Set.**
 *
 * @param key   The name of the preference to modify.
 * @param value The new value for the preference.
 * @see android.content.SharedPreferences.Editor.putStringSet
 * @see .putOrderedStringSet
 */
    fun putStringSet(key: String, value: Set<String?>) {
        val editor = getPreferences().edit()
        editor.putStringSet(key, value)
        editor.apply()
        }

        /**
         * Stores a Set of Strings, preserving the order.
         * Note that this method is heavier that the native implementation [.putStringSet] (which does not reliably preserve the order of the Set). To preserve the order of the
         * items in the Set, the Set implementation must be one that as an iterator with predictable
         * order, such as [LinkedHashSet].
         *
         * @param key   The name of the preference to modify.
         * @param value The new value for the preference.
         * @see .putStringSet
         * @see .getOrderedStringSet
         */
        fun putOrderedStringSet(key: String, value: Set<String?>) {
        val editor = getPreferences().edit()
        var stringSetLength = 0
        mClassroomPlusSharedPreferences?.let {
        if (it.contains(key + LENGTH)) {
        // First read what the value was
        stringSetLength = it.getInt(key + LENGTH, -1)
        }
        }
        editor.putInt(key + LENGTH, value.size)
        var i = 0
        for (aValue in value) {
        editor.putString("$key[$i]", aValue)
        i++
        }
        while (i < stringSetLength) {

        // Remove any remaining values
        editor.remove("$key[$i]")
        i++
        }
        editor.apply()
        }

        /**
         * Removes a preference value.
         *
         * @param key The name of the preference to remove.
         * @see android.content.SharedPreferences.Editor.remove
         */
        fun remove(key: String) {
        val prefs = getPreferences()
        val editor = prefs.edit()
        if (prefs.contains(key + LENGTH)) {
        // Workaround for pre-HC's lack of StringSets
        val stringSetLength = prefs.getInt(key + LENGTH, -1)
        if (stringSetLength >= 0) {
        editor.remove(key + LENGTH)
        for (i in 0 until stringSetLength) {
        editor.remove("$key[$i]")
        }
        }
        }
        editor.remove(key)
        editor.apply()
        }

        /**
         * Checks if a value is stored for the given key.
         *
         * @param key The name of the preference to check.
         * @return `true` if the storage contains this key value, `false` otherwise.
         * @see android.content.SharedPreferences.contains
         */
        operator fun contains(key: String?): Boolean {
        return getPreferences().contains(key)
        }

        /**
         * Removed all the stored keys and values.
         *
         * @return the [Editor] for chaining. The changes have already been committed/applied
         * through the execution of this method.
         * @see android.content.SharedPreferences.Editor.clear
         */
        fun clear(): SharedPreferences.Editor? {
        val editor = getPreferences().edit().clear()
        editor.apply()
        return editor
        }

        /**
         * Returns the Editor of the underlying SharedPreferences instance.
         *
         * @return An Editor
         */
        fun edit(): SharedPreferences.Editor? {
        return getPreferences().edit()
        }

/**
 * Builder class for the SharedPrefferences instance. You only have to call this once in the Application
 * onCreate. And in the rest of the code base you can call SharedPreferences.method name.
 */

class Builder {
    private var mKey: String? = null
    private var mContext: Context? = null
    private var mMode = -1
    private var mUseDefault = false
    fun setPrefsName(prefsName: String?): Builder {
        mKey = prefsName
        return this
    }

    fun setContext(context: Context?): Builder {
        mContext = context
        return this
    }

    @SuppressLint("WorldReadableFiles", "WorldWriteableFiles")
    fun setMode(mode: Int): Builder {
        mMode = if (mode == ContextWrapper.MODE_PRIVATE ) {
            mode
        } else {
            throw RuntimeException("The mode in the SharedPreference can only be set too ContextWrapper.MODE_PRIVATE, ContextWrapper.MODE_WORLD_READABLE, ContextWrapper.MODE_WORLD_WRITEABLE or ContextWrapper.MODE_MULTI_PROCESS")
        }
        return this
    }

    fun setUseDefaultSharedPreference(defaultSharedPreference: Boolean): Builder {
        mUseDefault = defaultSharedPreference
        return this
    }

    /**
     * Initialize the SharedPreferences instance to used in the application.
     *
     * @throws RuntimeException if Context has not been set.
     */
    fun build() {
        if (mContext == null) {
            throw RuntimeException("Context not set, please set context before building the SharedPreferences instance.")
        }
        if (TextUtils.isEmpty(mKey)) {
            mKey = mContext?.packageName
        }
        if (mUseDefault) {
            mKey += DEFAULT_SUFFIX
        }
        if (mMode == -1) {
            mMode = ContextWrapper.MODE_PRIVATE
        }
        mContext?.let {
            initPrefs(it, mKey, mMode)
        }
    }
}
}
