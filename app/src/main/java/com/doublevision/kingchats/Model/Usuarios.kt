package com.doublevision.kingchats.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val Bio: String = "",
    val email: String = "",
    val Nome: String? = "",
    val id: String = ""
): Parcelable
