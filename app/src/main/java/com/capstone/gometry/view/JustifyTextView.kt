package com.capstone.gometry.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import android.view.ViewGroup
import java.lang.StringBuilder
import java.util.*

internal class JustifiedTextView : AppCompatTextView {
    private var viewWidth = 0
    private val sentences: MutableList<String> = ArrayList()
    private val currentSentence: MutableList<String> = ArrayList()
    private val sentenceWithSpaces: MutableList<String> = ArrayList()
    private var justifiedText = ""
    private val random = Random()

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    )

    override fun onDraw(canvas: Canvas) {
        if (justifiedText != text.toString()) {
            val params = layoutParams
            val text = text.toString()
            viewWidth = measuredWidth - (paddingLeft + paddingRight)

            if (params.width != ViewGroup.LayoutParams.WRAP_CONTENT && viewWidth > 0 && text.isNotEmpty()) {
                justifiedText = getJustifiedText(text)
                if (justifiedText.isNotEmpty()) {
                    setText(justifiedText)
                    sentences.clear()
                    currentSentence.clear()
                }
            } else {
                super.onDraw(canvas)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    private fun getJustifiedText(text: String): String {
        val words = text.split(NORMAL_SPACE).toTypedArray()
        for (word in words) {
            val containsNewLine = word.contains("\n") || word.contains("\r")
            if (fitsInSentence(word, currentSentence, true)) {
                addWord(word, containsNewLine)
            } else {
                sentences.add(fillSentenceWithSpaces(currentSentence))
                currentSentence.clear()
                addWord(word, containsNewLine)
            }
        }

        if (currentSentence.size > 0) {
            sentences.add(getSentenceFromList(currentSentence, true))
        }

        return getSentenceFromList(sentences, false)
    }

    private fun addWord(word: String, containsNewLine: Boolean) {
        currentSentence.add(word)
        if (containsNewLine) {
            sentences.add(getSentenceFromListCheckingNewLines(currentSentence))
            currentSentence.clear()
        }
    }

    private fun getSentenceFromList(strings: List<String>, addSpaces: Boolean): String {
        val stringBuilder = StringBuilder()
        for (string in strings) {
            stringBuilder.append(string)
            if (addSpaces) {
                stringBuilder.append(NORMAL_SPACE)
            }
        }
        return stringBuilder.toString()
    }

    private fun getSentenceFromListCheckingNewLines(strings: List<String>): String {
        val stringBuilder = StringBuilder()
        for (string in strings) {
            stringBuilder.append(string)

            if (!string.contains("\n") && !string.contains("\r")) {
                stringBuilder.append(NORMAL_SPACE)
            }
        }
        return stringBuilder.toString()
    }

    private fun fillSentenceWithSpaces(sentence: List<String>): String {
        sentenceWithSpaces.clear()

        if (sentence.size > 1) {
            for (word in sentence) {
                sentenceWithSpaces.add(word)
                sentenceWithSpaces.add(NORMAL_SPACE)
            }

            while (fitsInSentence(HAIR_SPACE, sentenceWithSpaces, false)) {
                sentenceWithSpaces.add(getRandomNumber(sentenceWithSpaces.size - 2), HAIR_SPACE)
            }
        }
        return getSentenceFromList(sentenceWithSpaces, false)
    }

    private fun fitsInSentence(word: String, sentence: List<String>, addSpaces: Boolean): Boolean {
        var stringSentence = getSentenceFromList(sentence, addSpaces)
        stringSentence += word
        val sentenceWidth = paint.measureText(stringSentence)
        return sentenceWidth < viewWidth
    }

    private fun getRandomNumber(max: Int): Int {
        return random.nextInt(max) + 1
    }

    companion object {
        private const val HAIR_SPACE = "\u200A"
        private const val NORMAL_SPACE = " "
    }
}