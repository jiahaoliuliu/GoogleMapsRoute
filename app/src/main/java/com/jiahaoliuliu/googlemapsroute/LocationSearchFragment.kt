package com.jiahaoliuliu.googlemapsroute

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jiahaoliuliu.datalayer.GeocodingRepository
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentLocationSearchBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class LocationSearchFragment: Fragment() {

    companion object {
        private const val TIME_DIFFERENCE_FOR_INPUT = 1000L
        private const val ARGUMENT_KEY_ADDRESS = "Address"

        fun newInstance(address: String): LocationSearchFragment {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_KEY_ADDRESS, address)
            val locationSearchFragment = LocationSearchFragment()
            locationSearchFragment.arguments = bundle
            return locationSearchFragment
        }
    }

    @Inject lateinit var geocodingRepository: GeocodingRepository
    private lateinit var binding: FragmentLocationSearchBinding
    private var addressToBeFound: String? = null
    private var userInputTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            addressToBeFound = it.getString(ARGUMENT_KEY_ADDRESS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentLocationSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MainApplication.getMainComponent()?.inject(this)
        binding.addressInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                addressToBeFound = s.toString()
                userInputTimer?.cancel()
                userInputTimer = object: CountDownTimer(TIME_DIFFERENCE_FOR_INPUT, TIME_DIFFERENCE_FOR_INPUT) {
                    override fun onFinish() {
                        findAddress()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        // DO nothing
                    }
                }.start()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not do anything
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not do anything
            }
        })

        addressToBeFound?.let {
            updateAddress(it)
        }
    }

    private fun findAddress() {
        addressToBeFound?.let {
            if (addressToBeFound.isNullOrEmpty()) {
                return
            }

            geocodingRepository.retrieveLocation(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ location -> Timber.v("New location $location")
                }, {throwable -> Timber.e(throwable, "Error finding the address")})
        }
    }

    fun updateAddress(address: String) {
        binding.addressInput.setText(address)
    }
}