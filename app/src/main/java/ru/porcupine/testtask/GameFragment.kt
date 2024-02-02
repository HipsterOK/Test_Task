package ru.porcupine.testtask

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.porcupine.testtask.databinding.CardUpBinding
import ru.porcupine.testtask.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private val cards: MutableList<Card> = mutableListOf()
    private var selectedCard: Card? = null
    private var time = 0
    private var score = 0
    private var canClick = false

    private val imgList = listOf(
        R.drawable.el1,
        R.drawable.el2,
        R.drawable.el3,
        R.drawable.el4,
        R.drawable.el5,
    )
    private var elList = listOf<CardUpBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        elList = listOf(
            binding.el1,
            binding.el2,
            binding.el3,
            binding.el4,
            binding.el5,
            binding.el6,
            binding.el7,
            binding.el8,
            binding.el9,
            binding.el10,
        )

        initializeCards()

        startGame()

    }

    private fun initializeCards() {
        for (i in 0..4) {
            cards.add(Card(i, CardState.SHIRT, imgList[i]))
            cards.add(Card(i, CardState.SHIRT, imgList[i]))
        }

        cards.shuffle()

        for (i in elList.indices) {
            elList[i].imageViewCard.setImageResource(cards[i].imageResId)
            cards[i].id = i
        }
    }

    private fun startGame() {
        lifecycleScope.launch {
            for (i in 0 until cards.size) {
                showElement(i)
            }
            delay(2500)
            for (i in 0 until cards.size) {
                showShirts(i)
                canClick = true
            }
            startTimer()
            addClickListeners()
        }
    }

    private fun startTimer() {
        lifecycleScope.launch {
            while (true) {
                delay(1000)
                time ++
                Log.e("time", time.toString())
                binding.time.text = formatTime(time)
            }
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return String.format("%02d:%02d", minutes, remainingSeconds)
    }


    private fun addClickListeners() {
        for ((index, element) in elList.withIndex()) {
            element.root.setOnClickListener {
                val clickedCard = cards[index]

                if (clickedCard.state != CardState.SHIRT || !canClick) return@setOnClickListener

                canClick = false
                showElement(index)

                if (selectedCard == null) {
                    selectedCard = clickedCard
                    canClick = true
                } else {
                    lifecycleScope.launch {
                        if (clickedCard.imageResId == selectedCard!!.imageResId) {
                            score++
                            selectedCard = null
                            canClick = true

                            if (score >= 5) {
                                win()
                            }
                        } else {
                            delay(500)
                            showShirts(index)
                            showShirts(selectedCard!!.id)
                            selectedCard = null
                            canClick = true
                        }
                    }
                }
            }
        }
    }


    private fun win() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_gameFragment_to_resultFragment)
    }

    private fun showShirts(i: Int) {
        elList[i].imageViewShirt.visibility = View.VISIBLE
        cards[i].state = CardState.SHIRT
    }

    private fun showElement(i: Int) {
        elList[i].imageViewShirt.visibility = View.GONE
        cards[i].state = CardState.IMAGE
    }

}

data class Card(var id: Int, var state: CardState, var imageResId: Int)

enum class CardState {
    SHIRT, IMAGE
}