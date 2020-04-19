package com.jiahaoliuliu.googlemapsroute

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentLocationSearchBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class LocationSearchFragment: Fragment(), OnPlaceClickListener {

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

    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentLocationSearchBinding
    private var addressToBeFound: String? = null
    private var userInputTimer: CountDownTimer? = null
    private lateinit var locationListAdapter: LocationsListAdapter

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
        locationListAdapter = LocationsListAdapter(this)
        binding.locationResultsList.adapter = locationListAdapter
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

            placesRepository.retrievePredictions(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ placesList ->
                    locationListAdapter.updatePlacesList(placesList)
                    val imm: InputMethodManager =
                        activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.addressInput.windowToken, 0)
                }, {throwable -> Timber.e(throwable, "Error finding the address")})
        }
    }

    fun updateAddress(address: String) {
        binding.addressInput.setText(address)
    }

    override fun onPlaceClicked(id: String) {
        // TODO
    }
}