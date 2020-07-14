package tw.com.walkablecity.rating.item


import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.databinding.FragmentRatingItemBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toLatLngPoints
import tw.com.walkablecity.rating.RatingType

class RatingItemFragment(private val type: RatingType, private val route: Route?, private val walk: Walk) : Fragment(),
    OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    val viewModel: RatingItemViewModel by viewModels{
        getVMFactory(route, walk , type)}

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
    }

    override fun onSnapshotReady(bitmap: Bitmap?) {
        if(type == RatingType.WALK){
            viewModel.getImageUrl(walk, requireNotNull(UserManager.user?.idCustom),bitmap as Bitmap)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRatingItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_rating_item, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.sendRating.observe(viewLifecycleOwner, Observer {
            if(it){
                val result = 1
                this.setFragmentResult("navigation",bundleOf("sent" to result))
            }
        })

        when(type){
            RatingType.ROUTE->{
                route?.let{
                    mapFragment.getMapAsync {map ->

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(route.waypoints.toLatLngPoints()[0],15f))
                        map.addPolyline(PolylineOptions().addAll(route.waypoints.toLatLngPoints()))
                    }

                    childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

                    binding.sendRating.setOnClickListener {
                        viewModel.sendRouteRating()
                    }
                }

            }
            RatingType.WALK->{
                mapFragment.getMapAsync {

                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(walk.waypoints[0],15f))
                    it.addPolyline(PolylineOptions().addAll(walk.waypoints))

                }

                childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

                binding.sendRating.setOnClickListener {
                    map.snapshot(this)
                }

            }
        }

        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            it?.let{
                viewModel.sendRouteRating()
            }
        })








        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapFragment = SupportMapFragment().apply{
            getMapAsync( this@RatingItemFragment )
        }
    }
}
