package tw.com.walkablecity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import tw.com.walkablecity.addfriend.AddFriendFragmentDirections
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.databinding.ActivityMainBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.home.createroute.CreateRouteDialogFragmentDirections
import tw.com.walkablecity.host.add2event.AddFriend2EventFragmentDirections
import tw.com.walkablecity.profile.badge.BadgeFragmentDirections
import tw.com.walkablecity.rating.RatingFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<MainViewModel>{getVMFactory()}


    private val onNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item ->
        when(item.itemId){
            R.id.home ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalHomeFragment(
                    null
                ,null))
                return@OnNavigationItemSelectedListener true
            }
            R.id.ranking ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalRankingFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.favorite ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalFavoriteFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.event ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalEventFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
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
        viewModel.invitation.observe(this, Observer{
            it?.let{eventCount ->
                addBadge(eventCount)
            }
        })

        viewModel.friendCount.observe(this, Observer{
            it?.let{count->
                Util.getCountFromSharedPreference(BadgeType.FRIEND_COUNT.key, count)
            }
        })

        viewModel.eventCount.observe(this, Observer{
            it?.let{count->
                Util.getCountFromSharedPreference(BadgeType.EVENT_COUNT.key, count)
            }
        })

    }

    private fun addBadge(num: Int){
        if(num>0){
            binding.bottomNav.getOrCreateBadge(R.id.event).apply {
                backgroundColor = getColor(R.color.secondaryDarkColor)
                number = num
            }
        }
    }

    private fun setupBottomNav(){
        binding.bottomNav.setOnNavigationItemSelectedListener(onNavItemSelectedListener)
    }

    private fun setupNavController(){

        findNavController(R.id.nav_host_fragment)
            .addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.currentFragment.value = when(controller.currentDestination?.id){
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.rankingFragment -> CurrentFragmentType.RANKING
                R.id.favoriteFragment -> CurrentFragmentType.FAVORITE
                R.id.eventFragment -> CurrentFragmentType.EVENT
                R.id.profileFragment -> CurrentFragmentType.PROFILE

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
                R.id.addFriend2EventFragment  -> CurrentFragmentType.ADD_2_EVENT
                R.id.createRouteDialogFragment -> CurrentFragmentType.CREATE_ROUTE_DIALOG

                else -> viewModel.currentFragment.value
            }


        }

    }

    override fun onBackPressed() {

        when(viewModel.currentFragment.value){
            CurrentFragmentType.RATING -> findNavController(R.id.nav_host_fragment).
                navigate(RatingFragmentDirections.actionGlobalHomeFragment(null,null))
            CurrentFragmentType.CREATE_ROUTE_DIALOG -> findNavController(R.id.nav_host_fragment).
                navigate(CreateRouteDialogFragmentDirections.actionGlobalHomeFragment(null,null))
            CurrentFragmentType.ADD_FRIEND -> findNavController(R.id.nav_host_fragment)
                .navigate(AddFriendFragmentDirections.actionGlobalProfileFragment())

            else -> super.onBackPressed()
        }
    }
}
