Back-end service for the android application which allows the user to subscribe to push notifications about delays in public transportation and quickly check the departures board of selected stations.

It is implemented in Scala using Akka actor toolkit and has been reliably running on a single Raspberry Pi with zero maintenance. The implementation allows for an easy load balancing over multiple Raspberries.

Main components:
- NotificationActor handles communication with Azure Notification Hub.
- DeparturesManagerActor looks for traffic information subscriptions and delegates work to a pool of DeparturesCheckActors.
- DeparturesCheckActor queries the API for traffic information and tells NotificationActor to send push notifications if any delays are found.

The android application is avaliable in my [dsb-forsinket-android](https://github.com/omarcin/dsb-forsinket-android) repository.

The app uses [Rejseplannen's API](http://rejseplannen.dk) to get the information about the departures and delays.
