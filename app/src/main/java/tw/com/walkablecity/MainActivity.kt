package tw.com.walkablecity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.databinding.ActivityMainBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.WalkerStatus

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<MainViewModel>{getVMFactory()}


    private val onNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item ->
        when(item.itemId){
            R.id.home ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalHomeFragment(
                    null
                ))
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        setupNavController()
        setupBottomNav()
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

                else -> viewModel.currentFragment.value
            }





        }

    }

}
