package com.example.nan_calculator

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import kotlin.random.Random
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var handValueTextView: TextView
    private lateinit var changeCardButton: Button
    private lateinit var cardImageViews: List<ImageView>
    private lateinit var cardSpinners: List<Spinner>
    private val cardBack = R.drawable.card_back
    private val blackjackHand = BlackjackHand()
    private val cardImages = mapOf(
        "A_hearts" to R.drawable.ace_of_hearts,
        "A_diamonds" to R.drawable.ace_of_diamonds,
        "A_clubs" to R.drawable.ace_of_clubs,
        "A_spades" to R.drawable.ace_of_spades,
        "2_hearts" to R.drawable.heartstwo,
        "2_diamonds" to R.drawable.diamondstwo,
        "2_clubs" to R.drawable.clubstwo,
        "2_spades" to R.drawable.spadestwo,
        "3_hearts" to R.drawable.heartsthree,
        "3_diamonds" to R.drawable.diamondsthree,
        "3_clubs" to R.drawable.clubsthree,
        "3_spades" to R.drawable.spadesthree,
        "4_hearts" to R.drawable.heartsfour,
        "4_diamonds" to R.drawable.diamondsfour,
        "4_clubs" to R.drawable.clubsfour,
        "4_spades" to R.drawable.spadesfour,
        "5_hearts" to R.drawable.heartsfive,
        "5_diamonds" to R.drawable.diamondsfive,
        "5_clubs" to R.drawable.clubsfive,
        "5_spades" to R.drawable.spadesfive,
        "6_hearts" to R.drawable.heartssix,
        "6_diamonds" to R.drawable.diamondssix,
        "6_clubs" to R.drawable.clubssix,
        "6_spades" to R.drawable.spadessix,
        "7_hearts" to R.drawable.heartsseven,
        "7_diamonds" to R.drawable.diamondsseven,
        "7_clubs" to R.drawable.clubsseven,
        "7_spades" to R.drawable.spadesseven,
        "8_hearts" to R.drawable.heartseight,
        "8_diamonds" to R.drawable.diamondseight,
        "8_clubs" to R.drawable.clubseight,
        "8_spades" to R.drawable.spadeseight,
        "9_hearts" to R.drawable.heartsnine,
        "9_diamonds" to R.drawable.diamondsnine,
        "9_clubs" to R.drawable.clubsnine,
        "9_spades" to R.drawable.spadesnine,
        "10_hearts" to R.drawable.heartsten,
        "10_diamonds" to R.drawable.diamondsten,
        "10_clubs" to R.drawable.clubsten,
        "10_spades" to R.drawable.spadesten,
        "J_hearts" to R.drawable.jack_of_hearts2,
        "J_diamonds" to R.drawable.jack_of_diamonds2,
        "J_clubs" to R.drawable.jack_of_clubs2,
        "J_spades" to R.drawable.jack_of_spades2,
        "Q_hearts" to R.drawable.queen_of_hearts2,
        "Q_diamonds" to R.drawable.queen_of_diamonds2,
        "Q_clubs" to R.drawable.queen_of_clubs2,
        "Q_spades" to R.drawable.queen_of_spades2,
        "K_hearts" to R.drawable.king_of_hearts2,
        "K_diamonds" to R.drawable.king_of_diamonds2,
        "K_clubs" to R.drawable.king_of_clubs2,
        "K_spades" to R.drawable.king_of_spades2
    )

    private val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    private val suits = listOf("hearts", "diamonds", "clubs", "spades")
    private val cardOptions = ranks.flatMap { rank -> suits.map { suit -> "$rank of $suit" } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main)
        showPopupInstruct = findViewById(R.id.showDialogButton)
        showPopupInstruct.setOnClickListener{
            showPopup()
        }

        handValueTextView = findViewById(R.id.handValue)
        changeCardButton = findViewById(R.id.changeCardButton)
        cardImageViews = listOf(
            findViewById(R.id.cardImage1),
            findViewById(R.id.cardImage2),
            findViewById(R.id.cardImage3),
            findViewById(R.id.cardImage4),
            findViewById(R.id.cardImage5),
            findViewById(R.id.cardImage6)
        )

        cardSpinners = listOf(
            findViewById(R.id.cardSpinner1),
            findViewById(R.id.cardSpinner2),
            findViewById(R.id.cardSpinner3),
            findViewById(R.id.cardSpinner4),
            findViewById(R.id.cardSpinner5),
            findViewById(R.id.cardSpinner6)
        )

        val cardOptions = ranks.flatMap { rank -> suits.map { suit -> "$rank of $suit" } }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cardOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        cardSpinners.forEachIndexed { index, spinner ->
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCard = cardOptions[position]
                    val (rank, suit) = selectedCard.split(" of ")
                    val card = Card(rank, suit)
                    blackjackHand.cards[index] = card
                    updateCardImage(index, card)
                    updateHandValue() // Update hand value when card is selected
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        changeCardButton.setOnClickListener {
            changeCards()
        }

        cardImageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                flipCard(index)
            }
        }
    }

    private fun changeCards() {
        blackjackHand.clear()
        cardImageViews.forEachIndexed { index, imageView ->
            val card = drawRandomCard()
            blackjackHand.addCard(card)
            cardSpinners[index].setSelection(cardOptions.indexOf("${card.rank} of ${card.suit}"))
            updateCardImage(index, card)
        }
        updateHandValue() // Update hand value after changing cards
    }

    private fun flipCard(index: Int) {
        val card = blackjackHand.cards[index]
        if (card.value != 0) {
            blackjackHand.cards[index] = card.copy(value = 0)
            cardImageViews[index].setImageResource(cardBack)
        } else {
            val rank = ranks[Random.nextInt(ranks.size)]
            val suit = suits[Random.nextInt(suits.size)]
            val newCard = Card(rank, suit)
            blackjackHand.cards[index] = newCard
            updateCardImage(index, newCard)
        }
        updateHandValue()
    }

    private fun drawRandomCard(): Card {
        val rank = ranks[Random.nextInt(ranks.size)]
        val suit = suits[Random.nextInt(suits.size)]
        return Card(rank, suit)
    }

    private fun updateCardImage(index: Int, card: Card) {
        val cardKey = "${card.rank}_${card.suit}"
        cardImageViews[index].setImageResource(cardImages[cardKey] ?: cardBack)

        // Load and start the animation
        val animation = AnimationUtils.loadAnimation(this, R.anim.translate)
        cardImageViews[index].startAnimation(animation)
    }

    private fun updateHandValue() {
        handValueTextView.text = "Hand Value: ${blackjackHand.value}"
    }

    // Card class
    data class Card(val rank: String, val suit: String, var value: Int = 0) {
        init {
            value = when (rank) {
                "A" -> 11
                "K", "Q", "J" -> 10
                else -> rank.toIntOrNull() ?: 0
            }
        }
    }

    // BlackjackHand class
    class BlackjackHand {
        val cards = MutableList(6) { Card("2", "hearts", 0) }
        val value: Int
            get() {
                var total = 0
                var aceCount = 0
                for (card in cards) {
                    total += card.value
                    if (card.rank == "A") aceCount++
                }
                while (total > 21 && aceCount > 0) {
                    total -= 10
                    aceCount--
                }
                return total
            }

        fun addCard(card: Card) {
            for (i in cards.indices) {
                if (cards[i].value == 0) {
                    cards[i] = card
                    break
                }
            }
        }

        fun clear() {
            cards.replaceAll { Card("2", "hearts", 0) }

        }
    }
    private lateinit var showPopupInstruct : Button

    private fun showPopup(){
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.dialog_popup, null)

        val width = 500
        val height = 500

        val instructWindow = PopupWindow(popupView, width, height, true)
        instructWindow.showAtLocation(popupView, Gravity.BOTTOM, 20, 100)

        val btnDismiss = popupView.findViewById<Button>(R.id.btnDismiss)
        btnDismiss.setOnClickListener{
            instructWindow.dismiss()
        }
    }
}