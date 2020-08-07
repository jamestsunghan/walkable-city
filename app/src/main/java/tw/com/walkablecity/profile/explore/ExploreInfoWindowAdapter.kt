package tw.com.walkablecity.profile.explore


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.databinding.InfoWindowExploreBinding
import tw.com.walkablecity.ext.toDateString
import tw.com.walkablecity.ext.toLatLngPoints

class ExploreInfoWindowAdapter(val walks: List<Walk>, val parent: ViewGroup?) :
    GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View {
        val binding: InfoWindowExploreBinding = InfoWindowExploreBinding
            .inflate(LayoutInflater.from(parent?.context), parent, false)
        val realWalks = walks.filter { it.waypoints.isNotEmpty() }
        val markerWalk = realWalks.find { it.waypoints.toLatLngPoints()[0] == marker?.position }
        Logger.d("JJ_info marker walk start time ${markerWalk?.startTime?.toDateString()}")

        binding.walkStartTime.text = markerWalk?.startTime?.toDateString()
        binding.walksDistance.text = marker?.snippet
        binding.user = UserManager.user


        return binding.root
    }

    override fun getInfoWindow(marker: Marker?): View? {
        val binding: InfoWindowExploreBinding = InfoWindowExploreBinding
            .inflate(LayoutInflater.from(parent?.context), parent, false)
        val realWalks = walks.filter { it.waypoints.isNotEmpty() }
        val markerWalk = realWalks.find { it.waypoints.toLatLngPoints()[0] == marker?.position }
        Logger.d("JJ_info marker info title start time ${marker?.title}")
        binding.walkStartTime.text = markerWalk?.startTime?.toDateString()
        binding.walksDistance.text = marker?.snippet
        binding.user = UserManager.user


        return binding.root
    }
}