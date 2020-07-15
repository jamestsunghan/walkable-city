package tw.com.walkablecity.search


import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.databinding.FragmentSearchBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.HomeFragmentDirections
import java.util.*

class SearchFragment : DialogFragment() {

    private lateinit var placesClient: PlacesClient
    private val viewModel: SearchViewModel by viewModels{getVMFactory()}
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(WalkableApp.instance,getString(R.string.google_api_key))
        placesClient = Places.createClient(WalkableApp.instance)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this



        binding.timerSlider.addOnChangeListener { slider, value, fromUser ->
            viewModel.range.value = slider.values
        }

        viewModel.range.observe(viewLifecycleOwner, Observer {
            it?.let{
                Log.d("JJ", "list float $it")
            }
        })

        viewModel.selectedRoute.observe(viewLifecycleOwner, Observer {
            it?.let{
                findNavController().navigate(SearchFragmentDirections.actionGlobalHomeFragment(it))
                viewModel.searchComplete()
            }
        })

        binding.viewModel = viewModel
        autoCompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment


        autoCompleteFragment.apply {

            val searchIcon = view?.findViewById<ImageView>(R.id.places_autocomplete_search_button)
            searchIcon?.setImageResource(R.drawable.map_search_24px)

            val searchBar = view?.findViewById<EditText>(R.id.places_autocomplete_search_input)
            searchBar.apply {

                this?.letterSpacing = 0.15f
                this?.setHint(R.string.search_destination)
                this?.setTextSize(TypedValue.COMPLEX_UNIT_SP,16F)
                this?.typeface = Typeface.create("noto_sans", Typeface.NORMAL)
            }



            val dismissIcon = view?.findViewById<ImageView>(R.id.places_autocomplete_clear_button)
            dismissIcon?.setImageResource(R.drawable.cancel_24px)

            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            setOnPlaceSelectedListener(object : PlaceSelectionListener{
                override fun onPlaceSelected(place: Place) {
                    // TODO: Get info about the selected place.
                    Log.d("JJ", "Place: ${place.name}, ${place.id}, ${place.latLng}")
                }

                override fun onError(status: Status) {
                    Log.i("JJ", "An error occurred: $status")
                }
            })
        }






        return binding.root
    }


}
