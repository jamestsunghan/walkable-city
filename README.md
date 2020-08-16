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

  <img src="screenshot/prepare_for_walk.jpg" width="200"><br/>

- Load a Route: <br/>
User can load a route for walk reference from route collections of users favorites, routes nearby and user's walk history by filtering with minutes requested, destination, or sorting with 6 different street characteristics or features, including Tranquility, Scenery, Rest, Snacks, Coverage and Vibe.

  From Destination

  <img src="screenshot/search_by_destination.gif" width="200"><br/>

  Load A route

  <img src="screenshot/search_by_load_route.gif" width="200"><br/>

- Walk recordings: <br/>
While walking, walkable city would record each walk's duration and distance, and user can pause and resume whenever he/she wants, even leave the app and come back later with a single click. Along the walk, user can also take photos, which would be shown on the map in a split second.

  Distance and duration

  <img src="screenshot/walk_recording.gif" width="200"><br/>

  Photo taking

  <img src="screenshot/take_photo.gif" width="200"><br/>

- Rating and Comments: <br/>
When user finished a walk, he/she can share his walk by create a new route on the platform. User can also add rating and comments for the route reference.

  <img src="screenshot/rating_create.gif" width="200"> <br/>

- Dark Mode: <br/>
Designed for user to walk at night.

  <img src="screenshot/dark_mode.png" width="200">

### Route Flow

- Overview: <br/>
Route flow is designed for user to find his/her route preference, load it on map, and start walking as easy as possible, which includes:

- Ranking and Fovarites collections: <br/>
User can browse through all routes by time filter and feature sorting, and add to his/her own favorite collection.

- Route details: <br/>
Displaying photo gallery, basic informations, average rating, and comments for a route. User can load to map from this page directly.

  <img src="screenshot/detail_gallery.png" width="200">&nbsp;&nbsp; <img src="screenshot/detail_rating.png" width="200">

### Event Flow

- Overview: <br/>
Event Flow is designed for users to interact with each other, and encourages each other to walk more frequently as a community.

  <img src="screenshot/event_type.png" width="200">

- Event details: <br/>
Displaying members' accomplishment indicated by event type, including collaboration, competition or habit cultivation

  <img src="screenshot/event_collaborative.png" width="200"> &nbsp;&nbsp;<img src="screenshot/event_competition.png" width="200"> &nbsp;&nbsp;<img src="screenshot/event_habit.png" width="200">

- Create an event: <br/>
User can create an event by his/her own, invites friends and sets event to be public.

  <img src="screenshot/create_event.gif" width="200">

### Recording Flow

- Overview: <br/>
As each walk is recorded, recording flow is designed to encourages user to walk more by visualized what he/she has accomplished.

- Explorer map: <br/>
User can see each walk path on a map clearly

  <img src="screenshot/explorer_map.png" width="200">

- Badges: <br/>
User would get a shareable image when he/she accomplished certain amount of walk total distance, walk total hours, events joined, or friends.

  <img src="screenshot/badges.png" width="200"> &nbsp;&nbsp; <img src="screenshot/badge_dialog.png" width="200">

- Ranking between friends: <br/>
Once users are friends, they can keep track of friends' weekly, monthly or total walk distance.

  <img src="screenshot/friend_competition.png" width="200">

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
