package com.hadeer.crypto.android

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceDataStore
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import com.hadeer.crypto.android.databinding.ActivityMainBinding
import com.hadeer.crypto.android.serializer.UserSerializer
import com.hadeer.crypto.android.serializer.UserSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private val Context.dataStore by dataStore(
        fileName = "user_setting.json",
        serializer = UserSerializer(CryptoManager())
    )
    private val coroutine = CoroutineScope(Dispatchers.Main)
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        
        handleViewScreenLink()
    }

    private fun handleViewScreenLink() {
        binding.encryptBtn.setOnClickListener { storeEncryptedData() }
        binding.decryptBtn.setOnClickListener { displayDecryptedData() }
    }

    private fun displayDecryptedData() {
        coroutine.launch {
            val storeData = dataStore.data
            println("stored data looks like $storeData")
            binding.shownText.text = storeData.first().toString()
        }
    }

    private fun storeEncryptedData() {
        val userName = binding.encryptNameTextFieldEdt.text.toString()
        val password = binding.encryptPasswordTextFieldEdt.text.toString()
        println("name $userName \n password $password")
        coroutine.launch {
            dataStore.updateData {
                UserSetting(
                    userName = userName,
                    password = password
                )
            }
        }
        binding.encryptNameTextFieldEdt.text?.clear()
        binding.encryptPasswordTextFieldEdt.text?.clear()
    }
}

//Data store (Package) to store the required data
//serialization package