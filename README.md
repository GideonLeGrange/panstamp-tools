# panstamp-tools

A a Java based GUI tool for configuring and monitoring panStamp networks, and debugging panStamp applications.

![Screenshot](https://github.com/GideonLeGrange/panstamp-tools/releases/download/v1.0.2/screenshot1.png?raw=true "Screen shot")

## What are panStamps? 

PanStamps are autonomous low-power wireless modules programmable from the Arduino IDE and made for telemetry and control projects. 

You can read all about them [on their commercial site](http://www.panstamp.com/) and [their wiki](https://github.com/panStamp/panstamp/wiki). There's a GitHub [repository](https://github.com/panStamp/panstamp) and a [community forum.](http://www.panstamp.org/forum/)


## Versions

**The current stable version is 1.0.2**

Version 1.0.2 brings in some important serial modem IO bug fixes from the [panstamp-java library](https://github.com/GideonLeGrange/panstamp-java). 

## Getting and running the application 

### System requirements

You need a Windows, OS X or Linux system with the Java Runtime version 1.7 or newer installed. 

### Downloading

You can download binary versions of the application from the [Releases](https://github.com/GideonLeGrange/panstamp-tools/releases) section. 

Pre-built binaries are supplied for:
* [Mac OS X](https://github.com/GideonLeGrange/panstamp-tools/releases/download/v1.0.2/panstamp-tool-1.0.2.dmg)
* [Windows](https://github.com/GideonLeGrange/panstamp-tools/releases/download/v1.0.2/panstamp-gui.exe)

A cross-platform jar file is also provided and can be downloaded [here](https://github.com/GideonLeGrange/panstamp-tools/releases/download/v1.0.2/panstamp-tool-1.0.2-shaded.jar)

### Running 

The Windows application is currently shipped as a single .exe file and can be run directly. The OS X application is provided as a .dmg file containing an application that can be dragged into the Mac Applications folder and run from there. 

To run the cross platform jar version, execute the jar using the Java runtime:

```shell
java -jar panstamp-tool-1.0.2-shaded.jar
```

## Getting help

If you have problems with this application, please make a post in the Java section on the [panStamp forum](http://www.panstamp.org/forum/forumdisplay.php?fid=24). 

To report bugs, please use the [issues section](https://github.com/GideonLeGrange/panstamp-tools/issues) on GitHub

## Licence

This application is released under the Apache 2.0 licence. See the [Licence.md](Licence.md) file
