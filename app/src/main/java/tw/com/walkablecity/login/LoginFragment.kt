package tw.com.walkablecity.login


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.databinding.FragmentLoginBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toSignInUser

class LoginFragment : Fragment() {


    private val viewModel: LoginViewModel by viewModels{getVMFactory()}

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
//        val account = GoogleSignIn.getLastSignedInAccount(requireContext())

        val user = Firebase.auth.currentUser
        user?.let{
            viewModel.getUser(it.uid)
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.signInButton.setOnClickListener {

            if(viewModel.idCustom.value == null)signIn()
            else {
                viewModel.checkUserCustomId(viewModel.idCustom.value as String)
            }
        }

        viewModel.isCustomIdUsable.observe(viewLifecycleOwner, Observer{
            it?.let{
                if(it){
                    signIn()
                }else{
                    makeShortToast(R.string.id_been_used)
                }
                viewModel.resetCustomIdCheck()
            }
        })

        viewModel.firebaseUser.observe(viewLifecycleOwner, Observer{
            it?.let{
                viewModel.addUser(it.toSignInUser(viewModel.idCustom.value))
            }
        })


        viewModel.user.observe(viewLifecycleOwner, Observer{
            it?.let{
                UserManager.user = it
                findNavController().navigate(LoginFragmentDirections.actionGlobalHomeFragment(null))
                viewModel.navigateComplete()
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account =task.getResult(ApiException::class.java)!!
                Log.d("JJ_fireAuth","fireAuth with google : ${account.id}")
                viewModel.signInWithGoogle(account.idToken)
            }catch (e: ApiException){
                Log.w("JJ_fireAuth","fireAuth fail with google : $e")
//                updateUI(null)
            }
        }


    }

    private fun signIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val singInIntent = googleSignInClient.signInIntent
        startActivityForResult(singInIntent, SIGN_IN)
    }

//    private fun updateUI(user: FirebaseUser?){
//        if(user != null){
//            binding.userTest.text = StringBuilder().append("email: ").append(user.email).append("id: ").append(user.uid)
//        }else{
//            binding.userTest.text = "not login yet"
//        }
//    }

    companion object{
        const val SIGN_IN = 9001
    }

}
