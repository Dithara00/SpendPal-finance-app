buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter() // optional, if needed for older libraries
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.7.3")
        classpath ("com.google.gms:google-services:4.4.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}
