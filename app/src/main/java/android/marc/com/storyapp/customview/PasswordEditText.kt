package android.marc.com.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import kotlinx.coroutines.*
import java.util.*

class PasswordEditText: AppCompatEditText {

    private var timer: Timer? = null
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        super.addTextChangedListener(watcher)
    }

    private fun init() {
        this.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (timer != null) {
                    timer?.cancel()
                }
            }

            override fun afterTextChanged(text: Editable?) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (!text.isNullOrEmpty()) {
                            if (text.length < 8) {
                                uiScope.launch {
                                    withContext(Dispatchers.Main) {
                                        setError("Password must not less than 8 characters")
                                    }
                                }
                            }
                        }
                    }

                }, 1000)
            }
        })
    }
}