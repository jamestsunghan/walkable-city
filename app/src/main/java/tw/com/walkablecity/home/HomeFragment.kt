package tw.com.walkablecity.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import tw.com.walkablecity.R
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.databinding.FragmentHomeBinding
import tw.com.walkablecity.ext.getVMFactory

class HomeFragment : Fragment() {


    private lateinit var mapFragment: SupportMapFragment

    val viewModel: HomeViewModel by viewModels{getVMFactory(HomeFragmentArgs.fromBundle(requireArguments()).routeKey)}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding = DataBindingUtil
            .inflate(inflater,
                R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.navigateToRating.observe(viewLifecycleOwner, Observer {
            if(it){
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRatingFragment())
                viewModel.navigateToRatingComplete()
            }
        })

        viewModel.navigateToLoad.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoadRouteFragment())
                viewModel.navigateToLoadComplete()
            }
        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */

        mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync {
            val school = LatLng(25.042536, 121.564888)
            it.addMarker(MarkerOptions().position(school).title("Marker in AppWorks School"))
            it.moveCamera(CameraUpdateFactory.newLatLng(school))
        }


        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }


}
