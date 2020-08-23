package tw.com.walkablecity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import tw.com.walkablecity.addfriend.AddFriendFragmentDirections
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.databinding.ActivityMainBinding
import tw.com.walkablecity.eventdetail.EventDetailFragmentDirections
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.HomeFragmentDirections
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.home.createroute.CreateRouteDialogFragmentDirections
import tw.com.walkablecity.profile.badge.BadgeFragmentDirections
import tw.com.walkablecity.rating.RatingFragmentDirections
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util
import tw.com.walkablecity.util.Util.getCountFromSharedPreference

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<MainViewModel> { getVMFactory() }

    private val onNavItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    findNavController(R.id.nav_host_fragment)
                        .navigate(NavigationDirections.actionGlobalHomeFragment(null, null))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.ranking -> {
                    findNavController(R.id.nav_host_fragment)
                        .navigate(NavigationDirections.actionGlobalRankingFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favorite -> {
                    findNavController(R.id.nav_host_fragment)
                        .navigate(NavigationDirections.actionGlobalFavoriteFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.event -> {
                    findNavController(R.id.nav_host_fragment)
                        .navigate(NavigationDirections.actionGlobalEventFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    findNavController(R.id.nav_host_fragment)
                        .navigate(NavigationDirections.actionGlobalProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private var bound = false
    private lateinit var walkService: WalkService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as WalkService.WalkerBinder
            walkService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.viewModel = viewModel

        setupNavController()

        setupBottomNav()

        viewModel.invitation.observe(this, Observer {
            it?.let { eventCount ->
                addBadge(eventCount, R.id.event)
            }
        })

        viewModel.badgeTotal.observe(this, Observer {
            it?.let { badge ->
                addBadge(badge.sum(), R.id.profile)
            }
        })

        viewModel.friendCount.observe(this, Observer {
            it?.let { count ->
                val origin = getCountFromSharedPreference(BadgeType.FRIEND_COUNT.key, count)
                viewModel.addToBadgeTotal(
                    BadgeType.FRIEND_COUNT.newCountBadgeCheck(count, origin), R.id.profileFragment
                )
            }
        })

        viewModel.eventCount.observe(this, Observer {
            it?.let { count ->
                Logger.d("event count from main activity")

                val origin = getCountFromSharedPreference(BadgeType.EVENT_COUNT.key, count)

                viewModel.addToBadgeTotal(
                    BadgeType.EVENT_COUNT.newCountBadgeCheck(count, origin), R.id.eventFragment
                )
            }
        })

        viewModel.cacheDeleted.observe(this, Observer {
            it?.let { deleted ->
                if (deleted) {
                    findNavController(R.id.nav_host_fragment).navigate(
                        HomeFragmentDirections
                            .actionGlobalHomeFragment(null, null)
                    )
                    viewModel.deletionReset()
                }
            }
        })

        var previousStatus: WalkerStatus? = null
        viewModel.walkerStatus.observe(this, Observer {
            it?.let { status ->
                val serviceIntent = Intent(this, WalkService::class.java)

                when (status) {
                    WalkerStatus.PREPARE -> {
//                        unbindService(connection)
                    }

                    WalkerStatus.WALKING -> {
                        if (previousStatus != WalkerStatus.PAUSING) {
                            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
                            binding.bottomNav.startAnimation(
                                AnimationUtils.loadAnimation(this, R.anim.anim_slide_down)
                            )
                        } else {
                            walkService.startTimer()
                            walkService.startRecordingDistance()
                        }
                    }

                    WalkerStatus.PAUSING -> {
                        walkService.stopTimer()
                        walkService.stopRecordingDistance()
                    }
                    WalkerStatus.FINISH -> {
                        unbindService(connection)
                    }
                }

                previousStatus = status
            }
        })
    }

    private fun addBadge(num: Int, itemId: Int) {

        binding.bottomNav.getOrCreateBadge(itemId).apply {
            backgroundColor = getColor(R.color.secondaryDarkColor)
            number = num
            isVisible = num > 0
        }

    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnNavigationItemSelectedListener(onNavItemSelectedListener)
    }

    private fun setupNavController() {

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, _, _ ->

            viewModel.currentFragment.value = when (controller.currentDestination?.id) {

                R.id.homeFragment -> {
                    binding.bottomNav.menu.getItem(BOTTOM_HOME_POS).isChecked = true
                    CurrentFragmentType.HOME
                }

                R.id.rankingFragment -> CurrentFragmentType.RANKING
                R.id.favoriteFragment -> CurrentFragmentType.FAVORITE
                R.id.eventFragment -> CurrentFragmentType.EVENT

                R.id.profileFragment -> {
                    binding.bottomNav.menu.getItem(BOTTOM_PROFILE_POS).isChecked = true
                    CurrentFragmentType.PROFILE
                }

                R.id.loadRouteFragment -> CurrentFragmentType.LOAD_ROUTE
                R.id.ratingFragment -> CurrentFragmentType.RATING

                R.id.detailFragment -> CurrentFragmentType.DETAIL

                R.id.badgeFragment -> CurrentFragmentType.BADGE
                R.id.bestWalkersFragment -> CurrentFragmentType.BEST_WALKERS
                R.id.exploreFragment -> CurrentFragmentType.EXPLORE
                R.id.settingsFragment -> CurrentFragmentType.SETTINGS

                R.id.eventDetailFragment -> CurrentFragmentType.EVENT_DETAIL
                R.id.hostFragment -> CurrentFragmentType.HOST
                R.id.loginFragment -> CurrentFragmentType.LOGIN

                R.id.addFriendFragment -> CurrentFragmentType.ADD_FRIEND
                R.id.addFriend2EventFragment -> CurrentFragmentType.ADD_2_EVENT
                R.id.createRouteDialogFragment -> CurrentFragmentType.CREATE_ROUTE_DIALOG

                else -> viewModel.currentFragment.value
            }
        }
    }

    override fun onBackPressed() {

        val navController = findNavController(R.id.nav_host_fragment)

        when (viewModel.currentFragment.value) {
            CurrentFragmentType.RATING -> {
                val dialog = Util.showWalkDestroyDialog(this, R.string.keep_rating)
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        navController.navigate(
                            HomeFragmentDirections
                                .actionGlobalHomeFragment(null, null)
                        )
                    }.create()

                dialog.show()
            }

            CurrentFragmentType.CREATE_ROUTE_DIALOG -> {
                navController.navigate(
                    CreateRouteDialogFragmentDirections
                        .actionGlobalHomeFragment(null, null)
                )
            }

            CurrentFragmentType.ADD_FRIEND -> {
                navController.navigate(AddFriendFragmentDirections.actionGlobalProfileFragment())
            }

            CurrentFragmentType.BADGE -> {
                navController.navigate(BadgeFragmentDirections.actionGlobalProfileFragment())
            }

            CurrentFragmentType.EVENT_DETAIL -> {
                navController.navigate(EventDetailFragmentDirections.actionGlobalEventFragment())
            }

            CurrentFragmentType.HOME -> {
                if (viewModel.walkerStatus.value == WalkerStatus.WALKING
                    || viewModel.walkerStatus.value == WalkerStatus.PAUSING
                ) {

                    val dialog = Util.showWalkDestroyDialog(this, R.string.keep_walking)
                        .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                            unbindService(connection)
                            viewModel.deletionCache(false)

                        }.create()

                    dialog.show()
                } else {
                    super.onBackPressed()
                }
            }
            else -> super.onBackPressed()
        }
    }

    companion object {
        private const val BOTTOM_HOME_POS = 0
        private const val BOTTOM_PROFILE_POS = 4
    }
}
