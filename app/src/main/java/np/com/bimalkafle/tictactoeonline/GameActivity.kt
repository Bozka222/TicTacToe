package np.com.bimalkafle.tictactoeonline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import np.com.bimalkafle.tictactoeonline.databinding.ActivityGameBinding

// On Click Listener for all buttons (method down)
class GameActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityGameBinding

    private var gameModel : GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)

        binding.startGameBtn.setOnClickListener{
            startGame()
        }

        GameData.gameModel.observe(this){
            gameModel =  it
            setUI()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setUI(){
        gameModel?.apply {
            binding.btn0.text = filledPosition[0]
            binding.btn1.text = filledPosition[1]
            binding.btn2.text = filledPosition[2]
            binding.btn3.text = filledPosition[3]
            binding.btn4.text = filledPosition[4]
            binding.btn5.text = filledPosition[5]
            binding.btn6.text = filledPosition[6]
            binding.btn7.text = filledPosition[7]
            binding.btn8.text = filledPosition[8]

            binding.startGameBtn.visibility = View.VISIBLE

            binding.gameStatusText.text =
                when(gameStatus){
                    GameStatus.CREATED -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        "Game ID: $gameId"
                    }
                    GameStatus.JOINED -> {
                        "Click on start game"
                    }
                    GameStatus.INPROGRESS -> {
                        binding.startGameBtn.visibility = View.INVISIBLE
                        "$currentPlayer turn"
                    }
                    GameStatus.FINISHED -> {
                        if (winner.isNotEmpty()) "$winner Won"
                        else "DRAW"
                    }
                }
        }
    }

    fun startGame(){
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS
                )
            )
        }
    }

    fun updateGameData(model : GameModel){
        GameData.saveGameModel(model)
    }

    fun checkForWinner(){
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6),
        )

        gameModel?.apply {
            for(i in winningPos){
                //012
                if(
                    filledPosition[i[0]] == filledPosition[i[1]] &&
                    filledPosition[i[1]] == filledPosition[i[2]] &&
                    filledPosition[i[0]].isNotEmpty()
                ){
                    gameStatus = GameStatus.FINISHED
                    winner = filledPosition[i[0]]
                }
            }
            if(filledPosition.none(){it.isEmpty()}){
                gameStatus = GameStatus.FINISHED
            }

            updateGameData(this)
        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if(gameStatus!= GameStatus.INPROGRESS){
                Toast.makeText(applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return
            }
            //game is in progress
            val clickedPos = (v?.tag as String).toInt()
            if (filledPosition[clickedPos].isEmpty()){
                filledPosition[clickedPos] = currentPlayer
                currentPlayer = if(currentPlayer=="X")  "O" else "X"
                checkForWinner()
                updateGameData(this)
            }
        }
    }
}