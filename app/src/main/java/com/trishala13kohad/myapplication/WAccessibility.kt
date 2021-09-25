package com.trishala13kohad.myapplication


import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat


class WAccessibility: AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(rootInActiveWindow == null) return

        val preferenceManager = PreferenceManager(applicationContext)
        if(!preferenceManager.getISON()) return

        val rootNodeInfo: AccessibilityNodeInfoCompat
                = AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)

        var sendMessageNodeList
                = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
        if(!sendMessageNodeList.isNullOrEmpty()){
            val sendMessage = sendMessageNodeList[0]
            if(sendMessage.isVisibleToUser) {
                sendMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
        else Log.e("see", "No button")

        val searchNodeList
                = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/menuitem_search")
        if(!searchNodeList.isNullOrEmpty()){
            val searchIcon = searchNodeList[0]
            if(searchIcon.isVisibleToUser){
                searchIcon.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                val searchTextNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                    "com.whatsapp:id/search_src_text")
                if(!searchTextNodeList.isNullOrEmpty()){
                    val searchText = searchTextNodeList[0]
                    if(searchText != null) {
                        val arguments = Bundle()
                        arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            "Name")
                        searchText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                        try{
                            Thread.sleep(1000)
                        }
                        catch(e: Exception){
                            e.printStackTrace()
                        }
                        val contactPickerRowNode
                                = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                            "com.whatsapp:id/contactpicker_row_name")
                        if(!contactPickerRowNode.isNullOrEmpty()){
                            val contactPickerRow = contactPickerRowNode[0]
                            if(contactPickerRow != null){
                                Log.e(
                                    "cc", "contactPickerRow"
                                            +contactPickerRow.parent.isClickable )
                                if(contactPickerRow.parent.isClickable) {
                                    contactPickerRow.parent
                                        .performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                    sendMessageNodeList =
                                        rootNodeInfo.findAccessibilityNodeInfosByViewId(
                                            "com.whatsapp:id/send")
                                    if(!sendMessageNodeList.isNullOrEmpty()){
                                        val sendMessage = sendMessageNodeList[0]
                                        if(sendMessage.isVisibleToUser) {
                                            sendMessage.performAction(
                                                AccessibilityNodeInfo.ACTION_CLICK)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else Log.e("see", "No search button")

            Thread.sleep(5000)

        performGlobalAction(GLOBAL_ACTION_HOME)
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }


}