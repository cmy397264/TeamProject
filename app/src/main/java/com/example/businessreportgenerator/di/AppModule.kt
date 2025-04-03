package com.example.businessreportgenerator.di

import org.koin.dsl.module

val appModule = module {
    includes(databaseModule)
}