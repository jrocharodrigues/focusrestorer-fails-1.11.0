package com.example.myapplication

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FocusTest {
    @get:Rule
    val composeRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun disposedLazyRowWithFocusRestorerRestoresFocusCorrectly() {
        composeRule.setContent {
            FocusableGrid()
        }

        val root = composeRule.onAllNodes(isRoot())[0]

        composeRule.onNodeWithTag("0-0").requestFocus()
        composeRule.onNodeWithTag("0-0").assertIsFocused()

        root.performKeyInput {
            // Move to item 0-10, when we come back to this row later this item should again be focused
            repeat(10) { count ->
                pressKey(Key.DirectionRight)
                composeRule.onNodeWithTag("0-${count + 1}").assertIsFocused()
            }

            // Go down to the next row
            pressKey(Key.DirectionDown)
            repeat(10) {
                // Make sure we're at the start of the row now
                pressKey(Key.DirectionLeft)
            }
            composeRule.onNodeWithTag("1-0").assertIsFocused()

            // Go down 5 rows
            repeat(5) { count ->
                pressKey(Key.DirectionDown)
                composeRule.onNodeWithTag("${count + 2}-0").assertIsFocused()
            }

            // Go back up to the second row
            repeat(5) { count ->
                pressKey(Key.DirectionUp)
                composeRule.onNodeWithTag("${5 - count}-0").assertIsFocused()
            }

            // Go back to the first row, focus should go back to the previously focused item within that row (ie. 0-10)
            // On BOM 2025.11.01 this passes (compose 1.9.x), on later versions it fails (compose 1.10)
            pressKey(Key.DirectionUp)
            composeRule.onNodeWithTag("0-10").assertIsFocused()
        }
    }
}