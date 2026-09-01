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
    fun disposedLazyRowWithFocusRestorerRestoresFocusCorrectlyLowRowCount() {

        val rowCount = 7
        val verticalMoves = rowCount - 1

        composeRule.setContent {
            FocusableGrid()
        }

        val root = composeRule.onAllNodes(isRoot())[0]

        composeRule.onNodeWithTag("0-0").requestFocus()
        composeRule.onNodeWithTag("0-0").assertIsFocused()

        root.performKeyInput {
            // Move to item 10-0
            repeat(verticalMoves) { count ->
                pressKey(Key.DirectionDown)
                composeRule.onNodeWithTag("${count + 1}-0").assertIsFocused()
            }

            // Go Back to the first row, selecting item x-1 in each row as we go up,
            // when we come back to this down later item x-1 should again be focused in every row
            repeat(verticalMoves) { count ->
                pressKey(Key.DirectionRight)
                // when we move up item right item x-1 should be focused
                composeRule.onNodeWithTag("${verticalMoves - count}-1").assertIsFocused()
                pressKey(Key.DirectionUp)
                // when we move up item right item x-0 should be focused
                composeRule.onNodeWithTag("${verticalMoves - count - 1}-0").assertIsFocused()
            }

            // Go back down to the row 10, each time we go down item x-1 should be focused
            // On BOM 2025.11.01 this passes  on later versions it fails (BOM 2025.12.00-2026.04.01)
            repeat(verticalMoves) { count ->
                pressKey(Key.DirectionDown)
                composeRule.onNodeWithTag("${count + 1}-1").assertIsFocused()
            }

        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun disposedLazyRowWithFocusRestorerRestoresFocusCorrectlyHighRowCount() {

        val verticalMoves = 15

        composeRule.setContent {
            FocusableGrid()
        }

        val root = composeRule.onAllNodes(isRoot())[0]

        composeRule.onNodeWithTag("0-0").requestFocus()
        composeRule.onNodeWithTag("0-0").assertIsFocused()

        root.performKeyInput {
            // Move to item 10-0
            repeat(verticalMoves) { count ->
                pressKey(Key.DirectionDown)
                composeRule.onNodeWithTag("${count + 1}-0").assertIsFocused()
            }

            // Go Back to the first row, selecting item x-1 in each row as we go up,
            // when we come back to this down later item x-1 should again be focused in every row
            repeat(verticalMoves) { count ->
                pressKey(Key.DirectionRight)
                // when we move up item right item x-1 should be focused
                composeRule.onNodeWithTag("${verticalMoves - count}-1").assertIsFocused()
                pressKey(Key.DirectionUp)
                // when we move up item right item x-0 should be focused
                composeRule.onNodeWithTag("${verticalMoves - count - 1}-0").assertIsFocused()
            }
            // Go back down to the row 10, each time we go down item x-1 should be focused
            // On BOM 2025.07.00 and androidx.activity:activity-compose 1.11.0 this passes
            // on later versions it fails (BOM 2025.08.00-2026.04.01 and activity-compose 1.12.2-1.13.0)
            repeat(verticalMoves) { count ->
                pressKey(Key.DirectionDown)
                composeRule.onNodeWithTag("${count + 1}-1").assertIsFocused()
            }

        }
    }
}
