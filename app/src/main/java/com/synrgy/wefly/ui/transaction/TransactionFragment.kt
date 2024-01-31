package com.synrgy.wefly.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.synrgy.wefly.R
import com.synrgy.wefly.common.showDatePickerDialog
import com.synrgy.wefly.data.api.ApiResult
import com.synrgy.wefly.data.api.transaction.Orderer
import com.synrgy.wefly.data.api.transaction.Passenger
import com.synrgy.wefly.data.api.transaction.TransactionDetailRequest
import com.synrgy.wefly.data.api.transaction.TransactionRequest
import com.synrgy.wefly.databinding.FragmentTransactionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionFragment : Fragment(R.layout.fragment_transaction) {
    private lateinit var binding: FragmentTransactionBinding

    private val viewModel: TransactionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionBinding.bind(view)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            etDateOfBirthPassenger.setOnClickListener {
                showDatePickerDialog(requireContext(), etDateOfBirthPassenger)
            }
            btnOrder.setOnClickListener {
                val passengersFilled = Passenger(
                    id = 2,
                    firstName = "martin",
                    lastName = "shit",
                    dateOfBirth = "11-06-2000",
                    nationality = "Indonesia"
                )
                val passengers = Passenger(
                    id = 2,
                    firstName = etFirstName.text.toString(),
                    lastName = etLastName.text.toString(),
                    dateOfBirth = etDateOfBirthPassenger.text.toString(),
                    nationality = etNationalityPassenger.text.toString()
                )
                val passengerArray = arrayListOf(passengersFilled)
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
            viewModel.transactionFlow.collect {
                when(it) {
                    is ApiResult.Loading -> binding.pbMain.visibility = View.VISIBLE
                    is ApiResult.Success -> {
                        binding.pbMain.visibility = View.GONE

                        val result = it.data?.data?.passenger
                        Toast.makeText(context, "${result?.get(0)?.firstName}", Toast.LENGTH_SHORT).show()
                       /* val action = TransactionFragmentDirections
                            .actionTransactionFragmentToTransactionFragmentResponse(
                                passengers = result?.get(0) ?: Passenger(
                                    id = 2,
                                    firstName = "nothing",
                                    lastName = "shit",
                                    dateOfBirth = "11-06-2000",
                                    nationality = "Indonesia"
                                )
                            )
                        findNavController().navigate(action)*/
                    }
                    is ApiResult.Error -> binding.pbMain.visibility = View.GONE
                }
            }
        }
    }

}