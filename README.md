<p align="center">
<img src="https://raw.githubusercontent.com/AlienQueen/HatchetHarry/master/src/main/java/org/alienlabs/hatchetharry/view/page/image/logo.png" alt="LOGO"/>
</p>
### Welcome to HatchetHarry, the free Magic: The Gathering playing webapp.

HatchetHarry was started out of a need to play the Magic cards using Linux and FreeBSD. It is free software licensed under the terms of the [GNU Affero General Public License, version 3](http://www.gnu.org/licenses/agpl.txt). You are free to modify any part of its source code (client-side or server-side), provided that the changes keep this license.

The supported browsers are Mozilla Firefox 4+, Opera 11+, Google Chrome 31+, Internet Explorer 11+ and all the HTML5 compliant browsers. The hardware and software platforms known to run the application successfully are Linux, FreeBSD, Android tablets, Windows, MacOS X, iPad and the Freebox (the modem and media player from the French ISP Free).

If you find any bugs, feel free to report them as we are looking forward for your feedback.

Please note that the card images are not provided since they are not free of rights.

### Fancy playing?

Go to: [HH official web application](http://hatchetharry.net).

### Technical information

HatchetHarry is a Java webapp, the used technology stack consists of:

    - jQuery 1.11.1
    - Atmosphere 2.1.9
    - Wicket 6.18.0
    - Spring 4.1.2
    - Hibernate 4.3.7

The project is built with Maven 3, you should deploy it witout any problem in Tomcat 7 / 8 and Jetty. The used RDBMS is MariaDB 10.1.2 / MySQL 5.5.

The following dependencies of this project are on Github only:

    - wicket-quickview 6.0-SNAPSHOT: a wicket component (RepeatingView) that lets you add or remove the rows without the need to re-render the whole repeater again in Ajax. https://github.com/vineetsemwal/quickview
    - mistletoe 0.3-SNAPSHOT: my own version of this small test tool

Every other dependency is available through Maven Central.

There are file paths to change in order to match your own setup, in the following files:

    - src/main/java/org/alienlabs/hatchetharry/HatchetHarryApplication.properties
    - src/main/java/org/alienlabs/hatchetharry/service/DataGenerator.properties
    - src/main/java/org/alienlabs/hatchetharry/view/page/HomePage.properties
    - src/test/java/org/alienlabs/hatchetharry/integrationTest/FunctionalTests.java line 179 => you have to provide the Selenium Chrome driver for your platform (the one provided in src/main/resources/chromedriver is for Linux)
    - the PMD ruleset paths in the pom.xml (not strictly necessary)
    - src/main/resources/rebel.xml => JRebel configuration file, not strictly necessary. http://zeroturnaround.com/software/jrebel

You will need a database schema called hh in order to run the webapp, and a schema called hhTest in order to run the tests. Please see the files:

	- src/main/resources/applicationContext.xml
	- src/test/resources/applicationContextTest.xml
	
for the username & password.

Some technical tips & tricks can be found in the README file.

You'll find the official "Getting started rules" here:

    - http://media.wizards.com/2014/docs/EN_M15_QckStrtBklt_LR_Crop.pdf

For the reckless people out there, you'll find the official, comprehensive rules as of January 23, 2015 here:

    - http://media.wizards.com/2015/docs/MagicCompRules_20150123.pdf

Last but not least, this (translated) "Anatomy of a Magic Card" could be useful:

    - http://archive.wizards.com/Magic/magazine/article.aspx?x=mtgcom/academy/2

Have a lot of fun!

Some of the software products used are under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

Magic: The Gathering is a registered trademark of Wizards of the Coast, Inc., a subsidiary of Hasbro, Inc.

HatchetHarry (c) 2011-2015 [Zala Pierre GOUPIL](mailto:goupilpierre@gmail.com)