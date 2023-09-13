package com.lighthouse.auth.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.dialog.ImagePickerDialog
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.intentSerializable
import com.lighthouse.android.common_ui.util.isValidBirthday
import com.lighthouse.android.common_ui.util.isValidEmail
import com.lighthouse.android.common_ui.util.onCloseKeyBoard
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.auth.databinding.FragmentBasicInfoBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class BasicInfoFragment :
    BindingFragment<FragmentBasicInfoBinding>(com.lighthouse.auth.R.layout.fragment_basic_info),
    ImagePickerDialog.CameraDialogListener {
    private val viewModel: AuthViewModel by activityViewModels()
    private val interestList = mutableListOf<InterestVO>(
        InterestVO("여행", listOf("해변", "도시 여행"))
    )
    private lateinit var interestAdapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private var selectedCountry: CountryVO? = CountryVO("kr", "한국")
    private lateinit var imagePicker: ImagePickerDialog

    private val genderMap = mapOf(
        0 to "TMP",
        1 to "MALE",
        2 to "FEMALE",
        3 to "RATHER_NOT_SAY"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        initSpinner()
        initInterest()
        initCountry()
        initNext()
        initAdapter()
        observeCountry()
        initChip()
        initCalender()
        initCamera()
        observeImage()
        initBack()
    }


    private fun initBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initCamera() {
        binding.ivCamera.setOnClickListener {
            getImagePicker()
        }
    }

    override fun openCamera() {
        val intent = mainNavigator.navigateToCamera(requireContext())
        resultLauncher.launch(intent)
    }

    override fun openGallery() {
        val intent = ImageUtils.newInstance().openGallery(requireActivity())
        resultLauncher.launch(intent)
    }


    private fun getImagePicker() {
        when (::imagePicker.isInitialized) {
            false -> imagePicker = ImagePickerDialog.newInstance()
            else -> {}
        }
        when (!imagePicker.isAdded) {
            true -> {
                imagePicker.setListener(this)
                imagePicker.show(
                    requireActivity().supportFragmentManager, imagePicker.javaClass.simpleName

                )
            }

            else -> {}
        }
    }

    private fun observeImage() {
        getResult.observe(viewLifecycleOwner) {
            if (it.data != null) {
                val result = Uri.parse(it.data.toString())
                val fileName = getFileExtensionFromUri(result)
                val file = File(fileName)

                val serverFileName = "/${viewModel.getUID()}/${file.name}"
                try {
                    Glide.with(this).load(result).fitCenter()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.question)
                        .override(calSize(200f)).into(binding.ivProfileImg)

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.getPreSignedURL(serverFileName)
                            viewModel.result.collect { url ->
                                when (url) {
                                    is UiState.Success<*> -> {
                                        viewModel.registerInfo.profileImageUri = serverFileName
                                        viewModel.profileUrl = url.data.toString()
                                        viewModel.profilePath = result.toString()
                                    }

                                    is UiState.Loading -> {
                                        Log.d("PICTURE", "uploading!")
                                    }

                                    else -> {
                                        delay(5000)
                                        viewModel.getPreSignedURL(serverFileName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CAMERA ERROR", e.message.toString())
                }
            }
        }
    }

    private fun getFileExtensionFromUri(uri: Uri): String {
        return if (uri.scheme == "content") {
            val mimeType = requireContext().contentResolver.getType(uri)
            "$uri.${mimeType?.substringAfterLast('/')}"
        } else {
            uri.toString()
        }
    }

    private fun hideKeyboard() {
        binding.etName.onCloseKeyBoard(requireContext())
        binding.etBirthday.onCloseKeyBoard(requireContext())
        binding.etEmail.onCloseKeyBoard(requireContext())
        binding.etIntroduction.onCloseKeyBoard(requireContext())
    }

    private fun initCalender() {
        binding.btnCalendar.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            val supportFragment = requireActivity().supportFragmentManager

            supportFragment.setFragmentResultListener(
                "Birthday", viewLifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == "Birthday") {
                    val date = bundle.getString("Birthday")
                    binding.etBirthday.setText(date.toString())
                }
            }
            datePickerFragment.show(supportFragment, "datePicker")
        }

    }

    private fun convertToStandardDateFormat(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val parsedDate: Date = inputFormat.parse(inputDate) as Date
        return outputFormat.format(parsedDate)
    }

    private fun initAdapter() {
        interestAdapter = makeAdapter()
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(context, 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterest.layoutManager = linearLayoutManager
        binding.rvInterest.adapter = interestAdapter
    }

    private fun initNext() {
        binding.btnNext.setOnClickListener {
            if (validateInputs()) {
                viewModel.registerInfo.apply {
                    uid = viewModel.getUID()
                    name = binding.etName.text.toString()
                    email = binding.etEmail.text.toString()
                    birthday = convertToStandardDateFormat(binding.etBirthday.text.toString())
                    gender = genderMap[binding.spinnerGender.selectedItemPosition]
                    region = selectedCountry?.code
                    preferredInterests = interestList
                    description = binding.etIntroduction.text.toString()
                }

                findNavController().navigate(BasicInfoFragmentDirections.actionInfoFragmentToLanguageFragment())
            }
        }
    }

    private fun validateInputs(): Boolean {
        val emailIsValid = binding.etEmail.text.toString().isValidEmail()
        setErrorAndBackground(
            binding.etEmail, emailIsValid, getString(R.string.invalid_email)
        )

        val birthdayIsValid = binding.etBirthday.text.toString().isValidBirthday()
        setErrorAndBackground(
            binding.etBirthday, birthdayIsValid, getString(R.string.invalid_birthday)
        )

        val nameIsEmpty = binding.etName.text.toString().isEmpty()
        setErrorAndBackground(
            binding.etName, !nameIsEmpty, getString(R.string.invalid_null)
        )

        val genderIsEmpty =
            binding.spinnerGender.selectedItem.toString() == getString(R.string.gender)
        setErrorAndBackground(
            binding.spinnerGender, !genderIsEmpty, getString(R.string.invalid_null)
        )

//        val nationIsEmpty = binding.btnNation.text.toString().isEmpty()
//        setErrorAndBackground(
//            binding.btnNation, !nationIsEmpty, getString(R.string.invalid_null)
//        )

        val interestIsEmpty = interestList.isEmpty()
        setErrorAndBackground(
            binding.collapseInterest, !interestIsEmpty, getString(R.string.invalid_null)
        )

        return emailIsValid && birthdayIsValid && !nameIsEmpty && !genderIsEmpty && !interestIsEmpty
    }

    private fun setErrorAndBackground(
        view: View,
        isValid: Boolean,
        errorMessage: String,
    ) {
        if (view is EditText) {
            if (!isValid) {
                view.error = errorMessage
                view.setBackgroundResource(R.drawable.error_box)
                view.requestFocus()
            } else {
                view.error = null
                view.setBackgroundResource(R.drawable.edit_box)
            }
        } else {
            if (!isValid) {
                view.setBackgroundResource(R.drawable.error_box)
                view.requestFocus()
            } else {
                view.setBackgroundResource(R.drawable.edit_box)
            }
        }
    }


    private fun initSpinner() {
        val arrayList = arrayListOf(
            resources.getString(R.string.gender),
            resources.getString(R.string.male),
            resources.getString(R.string.female),
            resources.getString(R.string.rather_not_tell)
        )

        val arrayAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, arrayList
        ).apply {
            setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        }

        binding.spinnerGender.adapter = arrayAdapter
    }

    private fun initInterest() {
        binding.btnInterest.setOnClickListener {
            val hash = hashMapOf<String, List<String>>()

            interestList.forEach {
                hash[it.category] = it.interest
            }
            val intent = mainNavigator.navigateToInterest(
                requireContext(),
                Pair("SelectedList", hash),
            )
            resultLauncher.launch(intent)
        }
    }

    private fun initCountry() {
        binding.btnNation.setOnClickListener {
            val intent =
                mainNavigator.navigateToCountry(
                    requireContext(),
                    Pair("multiSelect", false),
                    Pair("SelectedList", listOf(selectedCountry?.name ?: ""))
                )
            resultLauncher.launch(intent)
        }
    }

    private fun observeCountry() {
        getResult.observe(viewLifecycleOwner) {
            var result = it.getStringArrayListExtra("CountryNameList")?.toList() ?: listOf()

            if (result.isNotEmpty()) {
                val code = it.getStringArrayListExtra("CountryCodeList")?.toList() ?: listOf()
                selectedCountry = CountryVO(code = code.first(), name = result.first())
                binding.btnNation.text =
                    result.first()
            }
        }
    }

    private fun initChip() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.intentSerializable("InterestList", HashMap::class.java)
            if (result != null) {
                binding.tvInterestNull.setGone()
                interestList.clear()
                for ((key, value) in result) {
                    interestList.add(InterestVO(key as String, value as List<String>))
                }

                interestAdapter.submitList(interestList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        cameraExecutor.shutdown()
    }
}