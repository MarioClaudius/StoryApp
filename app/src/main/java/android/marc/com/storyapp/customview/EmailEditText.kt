package android.marc.com.storyapp.customview

import android.content.Context
import android.marc.com.storyapp.R
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import kotlinx.coroutines.*
import java.util.*

class EmailEditText: AppCompatEditText {

    private var timer: Timer? = null
    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                timer?.cancel()
            }

            override fun afterTextChanged(text: Editable?) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (!text.isNullOrEmpty()) {
                            if (!isValidEmail(text.toString())) {
                                uiScope.launch {
                                    setError(context.getString(R.string.invalid_email_error))
                                }
                            }
                        }
                    }

                }, 1000)
            }
        })
    }

    private fun isValidEmail(email: String) : Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}