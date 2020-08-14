# Walkable City

Walkable City aims to make our city become pedestrian friendly and low carbon.
It encourages citizens to walk more frequently by offering interesting and cozy walk routes,
walk recordings and create event with friends!

<a href="https://play.google.com/store/apps/details?id=tw.com.walkablecity"><img src="https://camo.githubusercontent.com/9b43e9e7bdf73be90eaee8bf94cf61440638567e/68747470733a2f2f692e696d6775722e636f6d2f49353862574c642e706e67" width="230" height="90"></a>

## Flows

### Walker Flow

- Overview: <br/>
Walker flow is designed base on walking behaviors from start to end, which includes:

- Prepare for a Walk: <br/>
User can decide whether he/she wants to load a route and have a walk, choose a destination to walk to, or simply start a walk directly.

![prepare_for_walk](screenshot/prepare_for_walk.jpg)

- Load a Route: <br/>
User can load a route for walk reference from route collections of users favorites, routes nearby and user's walk history by filtering with minutes requested, destination, or sorting with 6 different street characteristics or features, including Tranquility, Scenery, Rest, Snacks, Coverage and Vibe.

  From Destination

![destination](screenshot/search_by_destination.gif)

  Load A route
search_by_load_route

![search_by_load_route](screenshot/search_by_load_route.gif)


- Walk recordings: <br/>
While walking, walkable city would record each walk's duration and distance, and user can pause and resume whenever he/she wants, even leave the app and come back later with a single click. Along the walk, user can also take photos, which would be shown on the map in a split second.

  Distance and duration

![walk_recording](screenshot/walk_recording.gif)

  Photo taking

![take_photo](screenshot/take_photo.gif)

- Rating and Comments: <br/>
When user finished a walk, he/she can share his walk by create a new route on the platform. User can also add rating and comments for the route reference.

![rating_create](screenshot/rating_create.gif)

- Dark Mode: <br/>
Designed for user to walk at night.

![dark_mode](screenshot/dark_mode.png)

### Route Flow

- Overview: <br/>
Route flow is designed for user to find his/her route preference, load it on map, and start walking as easy as possible, which includes:

- Ranking and Fovarites collections: <br/>
User can browse through all routes by time filter and feature sorting, and add to his/her own favorite collection.

- Route details: <br/>
Displaying photo gallery, basic informations, average rating, and comments for a route. User can load to map from this page directly.

![detail_gallery](screenshot/detail_gallery.png) &nbsp;&nbsp;  ![detail_rating](screenshot/detail_rating.png)

### Event Flow

- Overview: <br/>
Event Flow is designed for users to interact with each other, and encourages each other to walk more frequently as a community.

![event_type](screenshot/event_type.png)

- Event details: <br/>
Displaying members' accomplishment indicated by event type, including collaboration, competition or habit cultivation

![event_collaborative](screenshot/event_collaborative.png) &nbsp;&nbsp; ![event_competition](screenshot/event_competition.png) &nbsp;&nbsp; ![event_habit](screenshot/event_habit.png) &nbsp;&nbsp;

- Create an event: <br/>
User can create an event by his/her own, invites friends and sets event to be public.

![create_event](screenshot/create_event.gif)


### Recording Flow

- Overview: <br/>
As each walk is recorded, recording flow is designed to encourages user to walk more by visualized what he/she has accomplished.

- Explorer map: <br/>
User can see each walk path on a map clearly

![explorer_map](screenshot/explorer_map.png)

- Badges: <br/>
User would get a shareable image when he/she accomplished certain amount of walk total distance, walk total hours, events joined, or friends.

![badges](screenshot/badges.png) &nbsp;&nbsp; ![badge_dialog](screenshot/badge_dialog.png)

- Ranking between friends: <br/>
Once users are friends, they can keep track of friends' weekly, monthly or total walk distance.

![friend_competition](screenshot/friend_competition.png)

## Framework & Tools

- Android
  - Kotlin
  - Activity & Service
  - MVVM
  - Coroutines
  - Unit Test


- JetPack
  - Lifecycle
  - Navigation
  - WorkManager
  - Data binding


- Networking
  - Google Map & Location API
  - Open Weather API
  - Firebase & Firestore
  - Retrofit2 & Moshi
  - Glide


- Tools
  - Git & GitHub

## IDE

**Android Studio** - 3.6.2

**Android SDK** - 23+

**Gradle** - 3.6.2

## Latest Version

1.1.3

## Contact

James walkablecity@gmail.com
