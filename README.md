# SafetyNetAttestationAPIDemo
The SafetyNet Attestation API helps you assess the security and compatibility of the Android environments in which your apps run. You can use this API to analyze devices that have installed your app.SafetyNet examines software and hardware information on the device where your app is installed to create a profile of that device. The service then attempts to find this same profile within a list of device models that have passed Android compatibility testing. The API also uses this software and hardware information to help you assess the basic integrity of the device, as well as the APK information of the calling app. This attestation helps you to determine whether or not the particular device has been tampered with or otherwise modified. It also provides information about the app that is using this API so that you can assess whether the calling app is legitimate.

In this repo we demonstrate Google Device Verification API by using SafetyNet that verify your running device is tampered or not. We can detect that device is rooted or have failed basic integrity or we block our app to run on that device. So it is useful in the case if someone don't want to run their app in tampered device due to security concern.

Prerequesites :
Must enable Android Device Verification API in Google Developer Console for your project.
Must have an API key for device verification generated from Google Developer Console.
Put your project's api key in res->values->strings.xml ->  <string name="api_key">Your API key</string>

Output:
In Virtual Device [Emulator]
 ![safetynetfail](https://github.com/dishantwalia/SafetyNetCheckDemo/blob/master/SafetyNetDemo/emulator.png)
 
In Real Device which is non rooted and passed all check
 ![safetynetpass](https://github.com/dishantwalia/SafetyNetCheckDemo/blob/master/SafetyNetDemo/realdevice.png)



