package com.example.playlistmaker.playlists.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class NewPlaylistFragment : Fragment() {

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    var imageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationIcon(requireContext().getDrawable(R.drawable.arrow_back))
        binding.toolbar.setNavigationOnClickListener {
            backAction()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backAction()
                }
            }
        )

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.playlistCreate.isEnabled =
                    binding.playlistNameField.text.toString().isNotEmpty() && (binding.playlistNameField.text.toString().trim().isNotEmpty())
            }
        }
        binding.playlistNameField.addTextChangedListener(simpleTextWatcher)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    imageUri = uri
                    binding.cover.setImageURI(uri)
                } else {
                    imageUri = null
                }
            }

        binding.cover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistCreate.setOnClickListener {

            var description: String?

            if (binding.playlistDescriptionField.text.toString().isEmpty()) {
                description = null
            } else {
                description = binding.playlistDescriptionField.text.toString()
            }

            viewModel.addPlaylist(binding.playlistNameField.text.toString(), description, imageUri)
            Toast.makeText(requireContext(), requireContext().getString(R.string.playlist_created, binding.playlistNameField.text.toString()),
                Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun backAction(){
        if( imageUri == null &&
            binding.playlistNameField.text.toString().isEmpty() &&
            binding.playlistDescriptionField.text.toString().isEmpty()) {
            findNavController().popBackStack()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.finish_creating_a_playlist))
                .setMessage(requireContext().getString(R.string.all_unsaved_data_will_be_lost))

                .setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, which ->
                }
                .setPositiveButton(requireContext().getString(R.string.complete)) { dialog, which ->
                    findNavController().popBackStack()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}