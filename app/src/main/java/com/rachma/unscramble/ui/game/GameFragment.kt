/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.rachma.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rachma.unscramble.R
import com.rachma.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Untuk mendeklarasikan class GameFragment dan function Fragment()
// Merupakan tempat dimana permainan dimainkan dan berisi logika
class GameFragment : Fragment() {

    // Untuk mengikat instance objek dengan akses ke tampilan di layout game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Untuk membuat ViewModel saat pertama kali fragmen dibuat.
    // Jika fragmen dibuat ulang, ia menerima instance GameViewModel yang sama yang dibuat oleh
    // fragmen pertama.
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Untuk engembang file XML tata letak dan mengembalikan instance objek yang mengikat
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Untuk mengatur viewModel untuk mengikat data
        // Dan akan memungkinkan akses tata letak terikat ke semua data di VieWModel
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // Untuk menentukan tampilan fragmen sebagai pemilik siklus hidup pengikatan.
        // Dan digunakan agar pengikatan dapat mengamati pembaruan LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        // Untuk menyiapkan klik listener untuk tombol submit dan skip
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    // Untuk mendeklarasikan function onSubmitWord()
    // Untuk memeriksa kata pengguna, dan memperbarui skor yang sesuai dengan hasilnya
    // Untuk menampilkan kata acak selanjutnya
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    // Untuk mendeklarasikan function onSkipWord()
    // Untuk melewati kata yang tampil sekarang tanpa mengubah skor
    // Untuk memperbarui jumlah kata yang akan ditampilkan selanjutnya
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    // Untuk membuat function yang bernama showFinalScoreDialog()
    // Untuk membuat dan menunjukkan dialog alert atau pop up dengan skor akhirnya
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.congratulations))
                .setMessage(getString(R.string.you_scored, viewModel.score.value))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    exitGame()
                }
                .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                    restartGame()
                }
                .show()
    }

    // Untuk mendeklarasikan function restartGame
    // Inisialisasi ulang data di ViewModel dan perbarui tampilan dengan data baru, untuk mulai ulang permainan.
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    // Untuk mendeklarasikan function exitGame()
    // Untuk keluar dari permainan
    private fun exitGame() {
        activity?.finish()
    }

    // Untuk mendeklarasikan function setErrorTextField()
    // Untuk mengatur dan mengatur ulang status kesalahan pada teks
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
