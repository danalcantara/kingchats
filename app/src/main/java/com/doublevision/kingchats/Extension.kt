package com.doublevision.kingchats

import android.app.Activity
import android.view.View
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

fun Activity.ShowMessage(view:View,message: String){
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()

}
