package np.com.bimalkafle.tictactoeonline

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import np.com.bimalkafle.tictactoeonline.databinding.ActivityMainBinding
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.playOfflineBtn.setOnClickListener{
            createOfflineGame()
        }

        binding.createOnlineGameBtn.setOnClickListener{
            createOnlineGame()
        }
        binding.joinOnlineGameBtn.setOnClickListener{
            joinOnlineGame()
        }
    }
    fun createOfflineGame(){
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }

    fun createOnlineGame(){
        GameData.myID = "X"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED,
                gameId = kotlin.random.Random.nextInt(1000..9999).toString()
            )
        )
        startGame()
    }

    fun joinOnlineGame(){
        var gameId = binding.gameIdInput.text.toString()
        if(gameId.isEmpty()){
            binding.gameIdInput.setError("Please enter game ID")
            return
        }
        GameData.myID = "O"
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it?.toObject(GameModel::class.java)
                if(model==null){
                    binding.gameIdInput.setError("Please enter valid game ID")
                }else{
                    model.gameStatus = GameStatus.JOINED
                    GameData.saveGameModel(model)
                    startGame()
                }
            }
    }

    fun startGame(){
        startActivity(Intent(this,GameActivity::class.java))
    }
}