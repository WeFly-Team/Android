package com.synrgy.wefly.ui.homepage

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.synrgy.wefly.R
import com.synrgy.wefly.common.repeatCollectionOnCreated
import com.synrgy.wefly.common.showDatePickerDialog
import com.synrgy.wefly.common.spinnerAdapter
import com.synrgy.wefly.data.api.ApiResult
import com.synrgy.wefly.data.api.transaction.Orderer
import com.synrgy.wefly.data.api.transaction.Passenger
import com.synrgy.wefly.data.api.transaction.TransactionDetailRequest
import com.synrgy.wefly.data.api.transaction.TransactionRequest
import com.synrgy.wefly.databinding.FragmentHomepageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomepageFragment : Fragment(R.layout.fragment_homepage) {
    private lateinit var binding: FragmentHomepageBinding

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomepageBinding.bind(view)

        setupUI()
        repeatCollectionOnCreated {
            tokenRan()
        }
        viewModel.getAirportList()
        observeStateFlow()
    }

    private fun setupUI() {
        with(binding) {
            ivDateDeparture.setOnClickListener {
                showDatePickerDialog(requireContext(), etDateDeparture)
            }
            ivDateReturn.setOnClickListener {
                showDatePickerDialog(requireContext(), etDateReturn)
            }
            btnSearchFlight.setOnClickListener {
                val action = HomepageFragmentDirections.actionHomepageFragmentToFlightFragment(
                    seatClass = spSeatClass.selectedItem.toString(),
                    passenger = spPassenger.selectedItem.toString().toInt()
                )
                findNavController().navigate(action)
            }
            val passengerArray = arrayOf(1, 2, 3, 4, 5)
            homeSpinnerAdapter(array = passengerArray, spinner = spPassenger)
            val classArray = arrayOf("ECONOMY", "BUSINESS")
            homeSpinnerAdapter(classArray, spSeatClass)

            ivNotification.setOnClickListener {
                val passengers = Passenger(
                    id = 2,
                    firstName = "martin",
                    lastName = "shit",
                    dateOfBirth = "11-06-2000",
                    nationality = "Indonesia"
                )
                val passengerArray = arrayListOf(passengers)
                val orderer = Orderer(
                    createdDate = "",
                    deletedDate = "",
                    updatedDate= "",
                    id = 2,
                    firstName = "firstname",
                    lastName = "lastname",
                    phoneNumber = "324234",
                    email = "ex@gmail.com"
                )
                val transactionDetails = TransactionDetailRequest(2)
                val transactionRequest = TransactionRequest(
                    adultPassenger = 2,
                    childPassenger = 2,
                    infantPassenger = 2,
                    passengers = passengerArray,
                    orderer = orderer,
                    transactionDetails = arrayListOf(transactionDetails)
                )
                viewModel.transaction(transactionRequest)
            }
        }
    }

    private fun observeStateFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.airportList.collect {
                val content = it.data?.data?.content
                when(it){
                    is ApiResult.Loading -> binding.pbHome.visibility = View.VISIBLE
                    is ApiResult.Success -> {
                        binding.pbHome.visibility = View.GONE
                        val city = content?.map { it.city }?.toTypedArray() ?: emptyArray()
                        departure(city)
                        arrival(city)
                        Log.d("neotica", "observeStateFlow: $content")
                    }
                    is ApiResult.Error -> {
                        binding.pbHome.visibility = View.GONE
                        Log.d("neotica", "observeStateFlow: Error")
                    }
                }
            }
        }
    }

    private fun departure(city: Array<String>){
        homeSpinnerAdapter(city, binding.spFlightFrom)
    }
    private fun arrival(city: Array<String>){
        homeSpinnerAdapter(city, binding.spFlightTo)
    }

    private fun homeSpinnerAdapter(array: Array<out Any>, spinner: Spinner, onItemSelected: (position: Int) -> Unit = {}){
        spinnerAdapter(array = array, spinner = spinner, context = requireContext(), onItemSelected = onItemSelected)
    }

    private suspend fun tokenRan () {
        viewModel.token.collect {
            if (it.isEmpty()) {
                gotoLogin()
            }
            Log.d("neotica", "token: $it")
        }
    }

    private fun gotoLogin() {
        val action = HomepageFragmentDirections.actionHomepageFragmentToAuthGroup()
        findNavController().navigate(action)
    }
}
