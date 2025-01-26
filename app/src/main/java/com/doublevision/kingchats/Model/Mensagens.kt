package com.doublevision.kingchats.Model


import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Mensagens(


    val idRemetente: String = "",
    val texto: String = "",
    @ServerTimestamp
    val data: Date? = null
)
