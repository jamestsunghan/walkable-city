package tw.com.walkablecity.host


import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp.now

import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.databinding.FragmentHostBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toDateLong
import tw.com.walkablecity.host.add2event.AddFriend2EventAdapter
import tw.com.walkablecity.host.add2event.AddListAdapter
import java.text.SimpleDateFormat
import java.util.*

class HostFragment : Fragment() {


    private val viewModel: HostViewModel by viewModels{getVMFactory( HostFragmentArgs.fromBundle(requireArguments()).friendList?.toList())}

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHostBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_host, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerFriendAdding.adapter = AddListAdapter()

        binding.eventTypeSpinner.adapter = EventTypeSpinnerAdapter(
            mutableListOf(
                getString(R.string.select_event_type), getString(R.string.frequency_distance_spinner))
                .plus(EventType.values().map{
                    if(it == EventType.FREQUENCY) getString(R.string.frequency_hour_spinner)
                    else it.title
                }
            )
        )


        binding.eventTargetSpinner.adapter = EventTypeSpinnerAdapter(
            mutableListOf(getString(R.string.select_fr_type)).plus(FrequencyType.values().map{it.text})
        )

        binding.publicCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.isPublic.value = isChecked
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.selectEventStartDate.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),

                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView


                    viewModel.startDateDisplay.value =
                        "${year}-${Util.lessThenTenPadStart((monthOfYear + 1).toLong())}-${Util.lessThenTenPadStart(dayOfMonth.toLong())}"

                },
                year,
                month,
                day
            )
            dpd.datePicker.minDate = now().seconds.times(1000)
            dpd.show()
        }



        binding.selectEventEndDate.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),

                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView


                    viewModel.endDateDisplay.value =
                        "${year}-${Util.lessThenTenPadStart((monthOfYear + 1).toLong())}-${Util.lessThenTenPadStart(dayOfMonth.toLong())}"

                },
                year,
                month,
                day
            )

            dpd.datePicker.minDate = when(viewModel.frequencyType.value){

                FrequencyType.DAILY -> (viewModel.startDate.value ?: now()).seconds.times(1000).plus(ONE_DAY)
                FrequencyType.WEEKLY -> (viewModel.startDate.value ?: now()).seconds.times(1000).plus(ONE_WEEK)
                FrequencyType.MONTHLY ->{
                    val dateString = viewModel.startDateDisplay.value
                        ?: SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN).format(now().seconds.times(1000))
                    Log.d("JJ_date", "dateString $dateString")

                    val newString = Util.dateAddMonth(dateString) ?: dateString
                    Log.d("JJ_date","newString $newString")
                    Util.dateToTimeStamp(newString)?.seconds?.times(1000)
                        ?: now().seconds.times(1000).plus(THIRTY_DAYS)
                }
                null ->(viewModel.startDate.value ?: now()).seconds.times(1000).plus(ONE_DAY)
            }

            dpd.show()
        }


        viewModel.startDate.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ","timestamp ${it.toDateLong()}")
            }
        })

        viewModel.navigateToAddFriends.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(HostFragmentDirections
                    .actionHostFragmentToAddFriend2EventFragment(viewModel.friendList?.toTypedArray()))
                viewModel.addSomeFriendsComplete()
            }
        })


        viewModel.navigateToEvents.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(HostFragmentDirections.actionGlobalEventFragment())
                viewModel.navigateToEventsComplete()
            }
        })

        viewModel.type.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ_type", "eventType selected ${it.title}")
            }
        })

        viewModel.frequencyType.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ_type", "FQType selected ${it.text}")
            }
        })

        viewModel.endDate.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ", "endDate ${it.toDateLong()}")
            }
        })

        viewModel.target.observe(viewLifecycleOwner, Observer {
            it?.let{
                Log.d("JJ_target", "target $it")
            }
        })

        return binding.root
    }



    companion object{
        const val ONE_DAY = 1000 * 60 * 60 * 24L
        const val ONE_WEEK = ONE_DAY * 7
        const val THIRTY_DAYS = ONE_DAY.times(30)
    }
}
