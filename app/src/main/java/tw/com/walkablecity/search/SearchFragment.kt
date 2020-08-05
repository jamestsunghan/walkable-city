package tw.com.walkablecity.search


import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getColor
import tw.com.walkablecity.util.Util.makeShortToast
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.databinding.FragmentSearchBinding
import tw.com.walkablecity.ext.getVMFactory

class SearchFragment : DialogFragment() {

    private lateinit var placesClient: PlacesClient

    private val viewModel: SearchViewModel by viewModels {
        getVMFactory( SearchFragmentArgs.fromBundle(requireArguments()).latLngKey )
    }

    private lateinit var autoCompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Places.initialize(WalkableApp.instance, getString(R.string.google_api_key))

        placesClient = Places.createClient(WalkableApp.instance)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentSearchBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_search, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setLayout(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 300f, WalkableApp.instance.resources.displayMetrics
            ).toInt()
            , WindowManager.LayoutParams.WRAP_CONTENT
        )

        viewModel.selectedRoute.observe(viewLifecycleOwner, Observer {
            it?.let { route ->
                if (route.waypoints.isEmpty()) {
                    makeShortToast(R.string.no_route_fit_as_shortest)
                }
                findNavController().navigate(SearchFragmentDirections
                    .actionGlobalHomeFragment(route, viewModel.destination.value))
                viewModel.searchComplete()
            }
        })

        viewModel.destination.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getShortestTime(viewModel.currentLocation, it)
            }
        })

        autoCompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment


        autoCompleteFragment.apply {

            this.view?.layoutParams?.width = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 300f, WalkableApp.instance.resources.displayMetrics
            ).toInt()

            view?.findViewById<ImageView>(R.id.places_autocomplete_search_button)?.apply {
                setImageResource(R.drawable.map_search_24px)
                imageTintList = ColorStateList.valueOf(getColor(R.color.primaryDarkColor))
            }

            view?.findViewById<EditText>(R.id.places_autocomplete_search_input).apply {

                this?.letterSpacing = 0.15f
                this?.setHint(R.string.search_destination)
                this?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                this?.typeface = Typeface.create("noto_sans", Typeface.NORMAL)
            }

            view?.findViewById<ImageView>(R.id.places_autocomplete_clear_button)?.apply {
                setImageResource(R.drawable.cancel_24px)
                imageTintList = ColorStateList.valueOf(getColor(R.color.primaryDarkColor))
            }

            setCountries("TW")

            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    viewModel.setDestination(place.latLng)
                    Logger.d("Place: ${place.name}, ${place.id}, ${place.latLng}")
                }

                override fun onError(status: Status) {
                    Logger.i("An error occurred: $status")
                }
            })
        }

        return binding.root
    }


}
