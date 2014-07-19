Traffic-Track-Android-Arduino
=============================

The project consists in the tracking of the user's location through an ARDUINO board that communicates with a remote database (MySQL) through RESTful webservices built with JAVA Enterprise 7 (using the Glassfish 4 application server) and Android APP. 

The ARDUINO with GPS module allows you to capture and collect, with various options, the datas about user's current location.

The app can also show the map with the routes created with the points stored in the remote database. The routes are created with different color according to the speed for traffic monitoring. The communication between the app and the webservices is done with messages formatted according to the JSON standard.  

The app runs with default preferences that the user can change. 

Options are divided into two groups: 

Network: 

	•	Automatic map update 

Map: 

	•	Default zoom level

	•	Map Mode (Map, Earth, Hybrid)

This project was developed by Alessio Oglialoro, Salvatore Consolato Meli and Daniele Saitta.
 
