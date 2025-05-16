package com.bigPicture.businessreportgenerator.di

import org.koin.dsl.module

val appModule = module {
    includes(databaseModule)
    includes(boardModule)
}

