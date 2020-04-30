package com.jiahaoliuliu.googlemapsroute

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jiahaoliuliu.datalayer.PlacesRepository
import com.jiahaoliuliu.googlemapsroute.databinding.FragmentLocationSearchBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class LocationSearchFragment: Fragment(), OnPlaceClickListener {

    companion object {
        private const val TIME_DIFFERENCE_FOR_INPUT = 1000L
        private const val ARGUMENT_KEY_ADDRESS = "Address"
        private const val ARGUMENT_KEY_CALLER = "Caller"
        private const val ARGUMENT_KEY_IS_SPEECH_TO_TEXT = "SpeedToText"
        private const val REQUEST_CODE_SPEECH_TO_TEXT = 10001
        private const val PERMISSIONS_RECORD_AUDIO = 10002
        private val compositeDisposable = CompositeDisposable()

        fun newInstance(address: String, caller: Caller, isSpeechToText: Boolean = false): LocationSearchFragment {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_KEY_ADDRESS, address)
            bundle.putString(ARGUMENT_KEY_CALLER, caller.toString())
            bundle.putBoolean(ARGUMENT_KEY_IS_SPEECH_TO_TEXT, isSpeechToText)
            val locationSearchFragment = LocationSearchFragment()
            locationSearchFragment.arguments = bundle
            return locationSearchFragment
        }
    }

    @Inject lateinit var placesRepository: PlacesRepository
    private lateinit var binding: FragmentLocationSearchBinding
    private var addressToBeFound: String? = null
    private var caller: Caller = Caller.ORIGIN
    private var isSpeechToText: Boolean = false
    private var userInputTimer: CountDownTimer? = null
    private lateinit var locationListAdapter: LocationsListAdapter
    private lateinit var onLocationFoundListener: OnLocationFoundListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLocationFoundListener) {
            onLocationFoundListener = context
        } else {
            throw ClassCastException("The attached activity must implement OnLocationFoundListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            addressToBeFound = it.getString(ARGUMENT_KEY_ADDRESS)
            caller = Caller.valueOf(it.getString(ARGUMENT_KEY_CALLER)!!)
            isSpeechToText = it.getBoolean(ARGUMENT_KEY_IS_SPEECH_TO_TEXT)
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

        binding.voiceSearchIcon.setOnClickListener { checkPermissionForSpeechToText() }

        addressToBeFound?.let {
            binding.addressInput.setText(addressToBeFound)
        }

        // If it is speech to text
        if (isSpeechToText) {
            checkPermissionForSpeechToText()
        }
    }

    private fun findAddress() {
        addressToBeFound?.let {
            if (addressToBeFound.isNullOrEmpty()) {
                return
            }

            placesRepository.retrievePredictions(it)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { binding.progressBar.visibility = View.VISIBLE }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { binding.progressBar.visibility = View.GONE }
                .subscribe({ placesList ->
                    locationListAdapter.updatePlacesList(placesList)
                    activity?.let {fragmentActivity ->
                        val imm: InputMethodManager =
                            fragmentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.addressInput.windowToken, 0)
                    }
                }, {throwable -> Timber.e(throwable, "Error finding the address")})
        }
    }

    override fun onPlaceClicked(id: String) {
        onLocationFoundListener.onLocationFound(id, caller)
    }

    private fun checkPermissionForSpeechToText() {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startSpeechToTextDialog()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                PERMISSIONS_RECORD_AUDIO
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_RECORD_AUDIO -> {
                // If request is cancelled, the result arrays are empty
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeechToTextDialog()
                } else {
                    Toast.makeText(activity,
                        "You need to allow the app to record audio in order to use speech to text",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startSpeechToTextDialog() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Where do you comes from?")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_TO_TEXT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(activity,
                "Speed to text not supported",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SPEECH_TO_TEXT) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.addressInput.setText(results[0])
            }
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}

interface OnLocationFoundListener {

    fun onLocationFound(id: String, caller: Caller)
}