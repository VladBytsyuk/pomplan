package com.vbytsyuk.pomodoro.core

import com.vbytsyuk.pomodoro.core.screens.SignIn
import com.vbytsyuk.pomodoro.core.screens.SignIn.*
import com.vbytsyuk.pomodoro.mock.AuthApiMock
import com.vbytsyuk.pomodoro.mock.SocialSignInMock
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals


class `Sign In screen` {

    @Test
    fun `SignIn initialization`() = test(
        actions = emptyList(),
        expectedState = State()
    )


    @Test
    fun `Successful sign in by password`() = test(
        actions = listOf(
            Action.Initialize,
            Action.Changed.Login(CORRECT_LOGIN),
            Action.Changed.Password(CORRECT_PASSWORD),
            Action.Clicked.SignIn
        ),
        expectedState = State(
            login = CORRECT_LOGIN,
            password = CORRECT_PASSWORD,
            subState = State.SubState.Success
        )
    )

    @Test
    fun `Failure sign in by password`() = test(
        actions = listOf(
            Action.Initialize,
            Action.Changed.Login(CORRECT_LOGIN),
            Action.Changed.Password(INCORRECT_PASSWORD),
            Action.Clicked.SignIn
        ),
        expectedState = State(
            login = "",
            password = "",
            subState = State.SubState.Error
        )
    )

    @Test
    fun `Failure sign in by login`() = test(
        actions = listOf(
            Action.Initialize,
            Action.Changed.Login(INCORRECT_LOGIN),
            Action.Changed.Password(CORRECT_PASSWORD),
            Action.Clicked.SignIn
        ),
        expectedState = State(
            login = "",
            password = "",
            subState = State.SubState.Error
        )
    )

    @Test
    fun `Failure sign in by login & password`() = test(
        actions = listOf(
            Action.Initialize,
            Action.Changed.Login(INCORRECT_LOGIN),
            Action.Changed.Password(INCORRECT_PASSWORD),
            Action.Clicked.SignIn
        ),
        expectedState = State(
            login = "",
            password = "",
            subState = State.SubState.Error
        )
    )


    @Test
    fun `Successful sign in by google`() = test(
        actions = listOf(
            Action.Initialize,
            Action.Clicked.Social(SocialNetwork.GOOGLE)
        ),
        expectedState = State(
            login = "",
            password = "",
            subState = State.SubState.Success
        )
    )


    private val authApiMock = AuthApiMock(
        correctLogin = CORRECT_LOGIN,
        correctPassword = CORRECT_PASSWORD,
        errorMessage = ERROR_MESSAGE
    )
    private val googleSignIn = SocialSignInMock("google")
    private val appleSignIn = SocialSignInMock("apple")
    private val twitterSignIn = SocialSignInMock("twitter")

    private fun test(
        actions: List<Action>,
        expectedState: State
    ) = runBlockingTest {
        val controller = SignIn(authApiMock, googleSignIn, appleSignIn, twitterSignIn).controller
        controller.attach()

        actions.forEach {
            controller.setAction(it)
            delay(100)
        }

        controller.detach()
        assertEquals(expected = expectedState, actual = controller.currentState)
    }

    companion object {
        private const val CORRECT_LOGIN = "correct_login"
        private const val CORRECT_PASSWORD = "correct_password"

        private const val INCORRECT_LOGIN = "incorrect_login"
        private const val INCORRECT_PASSWORD = "incorrect_password"

        private const val ERROR_MESSAGE = "Wrong login or password"
    }
}
