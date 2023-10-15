package com.example.kotlin

import top.myrest.myflow.enumeration.LanguageType

class LanguageBundle {

    var languageType = LanguageType.EN_US

    companion object {

        @JvmStatic
        lateinit var INSTANCE: LanguageBundle
    }
}