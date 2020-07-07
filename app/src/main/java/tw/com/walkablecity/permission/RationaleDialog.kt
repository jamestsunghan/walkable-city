package tw.com.walkablecity.permission

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.WalkableApp

class RationaleDialog: DialogFragment() {
    private var finishActivity = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val requestCode = arguments?.getInt(REQUEST_CODE) ?: 0
        finishActivity = arguments?.getBoolean(FINISH_CODE) ?: false
        return AlertDialog.Builder(WalkableApp.instance.applicationContext)
            .setMessage(R.string.permission_rational_location).setPositiveButton(R.string.ok){dialog, which ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    requestCode
                )
                finishActivity = false
            }.setNegativeButton(R.string.cancel,null).create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(finishActivity){
            Util.makeShortToast(R.string.permission_required_toast)
        }
    }

    companion object{
        private const val REQUEST_CODE = "requestCode"
        private const val FINISH_CODE = "finish"

        fun newInstance(requestCode: Int,finishActivity: Boolean): RationaleDialog{
            val arguments = Bundle().apply{
                putInt(REQUEST_CODE, requestCode)
                putBoolean(FINISH_CODE,finishActivity)
            }
            return RationaleDialog().apply {
                this.arguments = arguments
            }
        }
    }

}