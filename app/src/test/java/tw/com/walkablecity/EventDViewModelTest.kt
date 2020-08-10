package tw.com.walkablecity

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import tw.com.walkablecity.data.Event
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.eventdetail.EventDetailViewModel
import tw.com.walkablecity.util.TestCoroutineRule

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EventDViewModelTest {

    @get:Rule
    val testInstantExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var walkableRepository: WalkableRepository

    @Mock
    private lateinit var joinObserver: Observer<Boolean>

    @Mock
    private lateinit var mockContext: Context


    private lateinit var user: User


    private lateinit var event: Event

    @Before
    fun setup(){
        user = User()
        event = Event()
    }
//    @Test
//    private fun buildTimeString_OverADay_isCorrect(){
//        val output = eventDetailViewModel
//    }

    @Test
    fun join_Success(){
        testCoroutineRule.runBlockTest {
            event = Event()

            doReturn(true)
                .`when`(walkableRepository)
                .joinEvent(user, event)

            val viewModel = EventDetailViewModel(walkableRepository, event)
            viewModel.joinSuccess.observeForever(joinObserver)
            verify(walkableRepository).joinEvent(user, event)
            verify(joinObserver).onChanged(true)
            viewModel.joinSuccess.removeObserver(joinObserver)
        }
    }


}