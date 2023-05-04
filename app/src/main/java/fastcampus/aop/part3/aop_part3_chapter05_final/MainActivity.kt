package fastcampus.aop.part3.aop_part3_chapter05_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

/**
 * Firebase 환경설정하기
 * 이메일 로그인 구현하기
 * Facebook 환경설정하기
 * Facebook 로그인 구현하기
 * Firebase Realtime Database 연동하기
 * Swipe Animation 라이브러리 사용해보기
 * Like DB 연동하기
 * Match 된 유저목록 보여주기 **/

class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.helloworldTextView).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, LikeActivity::class.java))
            finish()
        }
    }
}