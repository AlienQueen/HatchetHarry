<p align="center">
<img src="https://raw.githubusercontent.com/AlienQueen/HatchetHarry/master/src/main/java/org/alienlabs/hatchetharry/view/page/image/logo.png" alt="LOGO"/>
</p>
### Welcome to HatchetHarry, the free Magic: The Gathering playing webapp.

HatchetHarry was started out of a need to play the Magic cards using Linux and FreeBSD. It is free software licensed under the terms of the [GNU Affero General Public License, version 3](http://www.gnu.org/licenses/agpl.txt). You are free to modify any part of its source code (client-side or server-side), provided that the changes keep this license.

The supported browsers are Internet Explorer 11+, Mozilla Firefox 4+, Opera 11+, Google Chrome 31+ and all the HTML5 compliant browsers. The hardware and software platforms known to run the application successfully are Windows, Linux, FreeBSD, MacOS X, Android tablets, iPad and the Freebox (the modem and media player from the French ISP Free).

If you find any bugs, feel free to report them as we are looking forward for your feedback.

Please note that the card images are not provided since they are not free of rights.

### Fancy playing?

Go to: [HH official web application](http://hatchetharry.net).

### Technical information

The technology stack used in HH consists of:

    - jQuery 1.11.1
    - Atmosphere 2.19
    - Wicket 6.18.0
    - Spring 4.1.2
    - Hibernate 4.3.7

The project is built with Maven 3.

The following dependencies of this project are on Github only:

    * [wicket-quickview 6.0-SNAPSHOT](https://github.com/vineetsemwal/quickview): a wicket component (RepeatingView) that lets you add or remove the rows without the need to re-render the whole repeater again in Ajax
    * [mistletoe 0.3-SNAPSHOT](https://github.com/): my own version of this small test tool

Every other dependency is available through Maven Central.

There are file paths to change in order to match your own setup, in the following files:

	* src/main/java/org/alienlabs/hatchetharry/HatchetHarryApplication.properties
	* src/main/java/org/alienlabs/hatchetharry/service/DataGenerator.properties
	* src/main/java/org/alienlabs/hatchetharry/view/page/HomePage.properties
    * src/test/java/org/alienlabs/hatchetharry/integrationTest/FunctionalTests.java line 179 => you have to provide the Selenium Chrome driver for your platform (the one provided in src/main/resources/chromedriver is for Linux)
    * the PMD ruleset paths in the pom.xml (not strictly necessary)
    * src/main/resources/rebel.xml => [JRebel](http://zeroturnaround.com/software/jrebel) configuration file, not strictly necessary

Some of the software products used are under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

Magic: The Gathering is a registered trademark of Wizards of the Coast, Inc., a subsidiary of Hasbro, Inc.

HatchetHarry (c) 2011-2014 [Zala Pierre GOUPIL](goupilpierre@gmail.com)