package tw.com.walkablecity.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString


class LoginActivityContract: ActivityResultContract<Int, Int?>() {

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()



    override fun createIntent(context: Context, input: Int?): Intent {
        val googleSignInClient = GoogleSignIn.getClient(context, gso)


        return googleSignInClient.signInIntent.apply {
            putExtra("signIn", SIGN_IN)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int? {
        val data = intent?.getStringExtra("signIn")
        return 3
    }

    companion object{
        const val SIGN_IN = 9001
    }

}