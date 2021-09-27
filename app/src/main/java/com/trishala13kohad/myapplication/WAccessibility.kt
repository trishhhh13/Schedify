package com.trishala13kohad.myapplication


import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat


class WAccessibility: AccessibilityService() {

    private lateinit var name: String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {

            //getting name to be searched
            name = intent.getStringExtra("UserID").toString()

        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        //if whatsapp window isn't active, return
        if(rootInActiveWindow == null) return

        val preferenceManager = PreferenceManager(applicationContext)
        //if accessibility service is off, return
        if(!preferenceManager.getISON()) return

        //getting info of the root node of the current window
        val rootNodeInfo: AccessibilityNodeInfoCompat
                = AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)

        //getting whatsapp search list
        val searchNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId(
            "com.whatsapp:id/menuitem_search")

        //if search list is not empty and contacts are available
        if(!searchNodeList.isNullOrEmpty()){
            //find the search icon
            val searchIcon = searchNodeList[0]

            if(searchIcon.isVisibleToUser){

                //click on search icon
                searchIcon.performAction(AccessibilityNodeInfo.ACTION_CLICK)

                //now get the search text
                val searchTextNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                    "com.whatsapp:id/search_src_text")

                //if the searched item item is not null
                if(!searchTextNodeList.isNullOrEmpty()){

                    //get search text
                    val searchText = searchTextNodeList[0]
                    if(searchText != null) {
                        //getting the current bundle to type the text
                        val arguments = Bundle()

                        /*setting the argument character sequence as the name of group or
                        contact provided by the user*/
                        arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, name)

                        //set search text as the name provided by the user
                        searchText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

                        Thread.sleep(1000)

                        //getting contact node after searching for the argument provided by the user
                        val contactPickerRowNode = rootNodeInfo.findAccessibilityNodeInfosByViewId(
                            "com.whatsapp:id/contactpicker_row_name")

                        //if the searched contact or group is not null
                        if(!contactPickerRowNode.isNullOrEmpty()){

                            val contactPickerRow = contactPickerRowNode[0]
                            //if the searched contact is found
                            if(contactPickerRow != null){
                                Log.e("cc", "contactPickerRow"
                                            +contactPickerRow.parent.isClickable )

                                //if the contact is clickable
                                if(contactPickerRow.parent.isClickable) {

                                    //perform the click action
                                    contactPickerRow.parent
                                        .performAction(AccessibilityNodeInfo.ACTION_CLICK)

                                    //getting send message view
                                    val sendMessageNodeList =
                                        rootNodeInfo.findAccessibilityNodeInfosByViewId(
                                            "com.whatsapp:id/send")

                                    if(!sendMessageNodeList.isNullOrEmpty()){

                                        //getting send message icon
                                        val sendMessage = sendMessageNodeList[0]
                                        if(sendMessage.isVisibleToUser) {

                                            /* if send message icon is visible to user,
                                                perform click action*/
                                            sendMessage.performAction(
                                                AccessibilityNodeInfo.ACTION_CLICK)
                                        }
                                        else Log.e("TAG", "Message couldn't be sent")
                                    }
                                    else Log.e("TAG", "Message couldn't be sent")
                                }
                                else Log.e("TAG", "Contact not found!")
                            }
                            else Log.e("TAG", "Contact not found!")
                        }
                        else Log.e("TAG", "No contact on whatsapp")
                    }
                    else Log.e("TAG", "No contact on whatsapp")
                }
                else Log.e("TAG", "No contact on whatsapp")
            }
            else Log.e("TAG", "Message sending failed")
        }
        else Log.e("see", "Message sending failed")

            Thread.sleep(5000)

        // go to home page after sending message
        performGlobalAction(GLOBAL_ACTION_HOME)
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    override fun onInterrupt() {
        Log.e("TAG", "No contact on whatsapp")
    }


}