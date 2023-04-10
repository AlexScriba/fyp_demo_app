package com.example.demoapplication

import androidx.multidex.MultiDexApplication

/**
 * DemoApplication class necessary to allow for Multi-Dex Application
 * Multi-Dex needed as imported libraries have too many functions and classes for a single DEX file
 */
class DemoApplication : MultiDexApplication() {
}