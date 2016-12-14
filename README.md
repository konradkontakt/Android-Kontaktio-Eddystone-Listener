# Eddystone URL Listener

Simple code example presenting how to do th simple URL listener for Kontakt.io beacons broadcasting Eddystone URL packet. Secondly application is listing clickable URLs.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

1. A [Kontakt.io Panel](https://panel.kontakt.io/signin) account with few beacons there
2. Android Studio
3. Device with Android OS 4.4 or higher
4. Java 6 or higher

### Installing

#### Option 1

Download or clone the project and open it by Android Studio.

#### Option 2

You can also only copy the code from the java files but than remember to add in application build file *build.gradle*

```java
dependencies {
    compile 'com.kontaktio:sdk:3.2.0'
}
```

also in *AndroidManifest.xml* you will need to add permissions listed below if you still did not do it

```java
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

and also enable our *Proximity service* in the application section

```java
<service android:name="com.kontakt.sdk.android.ble.service.ProximityService" android:exported="false"/>
```

To initialize the SDK you will also need *API key*, that can be aquired from [Kontakt.io Panel](https://panel.kontakt.io/signin) after login.

## Authors

* **Konrad Bujak**

See also the list of [contributors](https://github.com/konradkontakt/ExtendedAndroidSDKSample/graphs/contributors) who participated in this project.

## Acknowledgments

* Kontakt.io for developing such good SDK and hardware
* Inspiration
