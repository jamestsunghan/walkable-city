package tw.com.walkablecity.host


import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.firebase.Timestamp.now
import tw.com.walkablecity.*

import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.databinding.FragmentHostBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toDateLong
import tw.com.walkablecity.host.add2event.AddListAdapter
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util
import tw.com.walkablecity.util.Util.lessThenTenPadStart
import java.text.SimpleDateFormat
import java.util.*

class HostFragment : Fragment() {


    private val viewModel: HostViewModel by navGraphViewModels(R.id.navigationHost) {
        getVMFactory()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val binding: FragmentHostBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_host, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerFriendAdding.adapter = AddListAdapter()

        binding.eventTypeSpinner.adapter = EventTypeSpinnerAdapter(
            mutableListOf(
                getString(R.string.select_event_type),
                getString(R.string.frequency_distance_spinner)
            )
                .plus(EventType.values().map {type->
                    if (type == EventType.FREQUENCY) getString(R.string.frequency_hour_spinner)
                    else type.title
                }
                )
        )

        binding.eventTargetSpinner.adapter = EventTypeSpinnerAdapter(
            mutableListOf(getString(R.string.select_fr_type)).plus(FrequencyType.values().map {type->
                type.text
            })
        )

        binding.publicCheckbox.setOnCheckedChangeListener { _ , isChecked ->
            viewModel.isPublic.value = isChecked
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.selectEventStartDate.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),

                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView

                    viewModel.startDateDisplay.value =
                        "${year}-${lessThenTenPadStart((monthOfYear + 1).toLong())}-${lessThenTenPadStart(
                            dayOfMonth.toLong()
                        )}"

                },
                year,
                month,
                day
            )
            dpd.datePicker.minDate = now().seconds.times(ONE_SECOND)
            dpd.show()
        }



        binding.selectEventEndDate.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),

                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView

                    viewModel.endDateDisplay.value =
                        "${year}-${lessThenTenPadStart((monthOfYear + 1).toLong())}-${lessThenTenPadStart(
                            dayOfMonth.toLong()
                        )}"
                },
                year,
                month,
                day
            )

            dpd.datePicker.minDate = when (viewModel.frequencyType.value) {

                FrequencyType.DAILY -> (viewModel.startDate.value
                    ?: now()).seconds.times(ONE_SECOND).plus(ONE_DAY)
                FrequencyType.WEEKLY -> (viewModel.startDate.value
                    ?: now()).seconds.times(ONE_SECOND).plus(ONE_WEEK)
                FrequencyType.MONTHLY -> {
                    val dateString = viewModel.startDateDisplay.value
                        ?: SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN).format(
                            now().seconds.times(ONE_SECOND)
                        )
                    Logger.d("JJ_date dateString $dateString")

                    val newString = Util.dateAddMonth(dateString) ?: dateString
                    Logger.d("JJ_date newString $newString")
                    Util.dateToTimeStamp(newString)?.seconds?.times(ONE_SECOND)
                        ?: now().seconds.times(ONE_SECOND).plus(THIRTY_DAYS)
                }
                null -> (viewModel.startDate.value ?: now()).seconds.times(ONE_SECOND).plus(ONE_DAY)
            }

            dpd.show()
        }


        viewModel.startDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("timestamp ${it.toDateLong()}")
            }
        })

        viewModel.navigateToAddFriends.observe(viewLifecycleOwner, Observer {confirmed->
            if (confirmed) {
                findNavController().navigate(
                    HostFragmentDirections
                        .actionHostFragmentToAddFriend2EventFragment()
                )
                viewModel.addSomeFriendsComplete()
            }
        })


        viewModel.navigateToEvents.observe(viewLifecycleOwner, Observer {confirmed->
            if (confirmed) {
                UserManager.user?.id?.let { id ->
                    Logger.d("badge event dialog from host")
                    mainViewModel.getUserEventCount(id)
                }
                findNavController().navigate(HostFragmentDirections.actionGlobalEventFragment())
                viewModel.navigateToEventsComplete()
            }
        })

        viewModel.type.observe(viewLifecycleOwner, Observer {
            it?.let {type->
                Logger.d("JJ_type eventType selected ${type.title}")
            }
        })

        viewModel.frequencyType.observe(viewLifecycleOwner, Observer {
            it?.let {type->
                Logger.d("JJ_type FQType selected ${type.text}")
            }
        })
        viewModel.endDate.observe(viewLifecycleOwner, Observer {
            it?.let {time->
                Logger.d("endDate ${time.toDateLong()}")
            }
        })
        viewModel.target.observe(viewLifecycleOwner, Observer {
            it?.let {target->
                Logger.d("JJ_target target $target")
            }
        })

        return binding.root
    }


    companion object {
        const val ONE_SECOND = 1000L
        const val ONE_DAY = ONE_SECOND * 60 * 60 * 24
        const val ONE_WEEK = ONE_DAY * 7
        const val THIRTY_DAYS = ONE_DAY.times(30)
    }
}
