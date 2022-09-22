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

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


// Untuk mendeklarasikan class GameViewModel
// ViewModel yang berisi data aplikasi dan metode untuk memproses data
class GameViewModel : ViewModel() {

    // Mendeklarasikan variabel yang dapat diubah yang hanya dapat dimodifikasi di dalam kelas dideklarasikan
    // Untuk mendapatkan skor
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    // Untuk mendeklarasikan variabel _currentWordCount
    // Untuk mengubah jenis variabel _currentScrambledWord menjadi val karena nilai objek LiveData/MutableLiveData akan tetap sama,
    // dan hanya data yang disimpan dalam objek yang akan berubah
    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    // Untuk mendeklarasikan variabel _currentScrambleWord
    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                    TtsSpan.VerbatimBuilder(scrambledWord).build(),
                    0,
                    scrambledWord.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    // List of words used in the game
    // Untuk mendeklarasikan variabel wordlist
    // Untuk mendeklarasikan daftarkata yang akan digunakan pada permainan dan menyiapkan daftar kata selanjutnya
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    // Untuk mendeklarasikan function yang bernama getNextWord
    // Untuk mengakses data dalam objek LiveData, digunakan properti value
    // Untuk memperbarui kata saat ini dan kata acak yang tampil saat ini dengan kata selanjutnya
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            Log.d("Unscramble", "currentWord= $currentWord")
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    // Untuk mendeklarasikan function reintalizeData()
    // Untuk menginisialisasi ulang daftar kata pada permainan untuk memulai ulang permainan
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    // Untuk mendeklarasikan function increaseScore()
    // Untuk meningkatkan skor permainan jika kata yang dituliskan pemain benar
    private fun increaseScore() {
        _score.value = _score.value?.plus(SCORE_INCREASE)
    }

    // Untuk mendeklarasikan function isUserWordCorrect
    // Untuk mengembalikan nilai true jika kata yang dituliskan pemain benar
    // Untuk meningkatkan skor yang sesuai
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    // Untuk mendeklarasikan function nextWord()
    // Untuk mengembalikan nilai true jika kata saat ini kurang dari maksimal nomor dari kata yang telah ditentukan
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}
