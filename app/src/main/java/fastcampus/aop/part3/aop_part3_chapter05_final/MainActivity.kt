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


    // 파이어 베이스 계정 정보 가져오기
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 클릭 하면 로그인 PAGE 이동
        findViewById<TextView>(R.id.helloworldTextView).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        //  currentUser 속성을 사용하여 현재 로그인한 사용자를 가져올 수도 있습니다. 사용자가 로그인 상태가 아니라면 currentUser 값이 null입니다.

        // 현재 계정 정보가 없으면 로그인 페이지로 이동
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // 계정 정보가 있을 경우 LikeActivity로 이동
            startActivity(Intent(this, LikeActivity::class.java))
            // 종료
            finish()
        }
    }
}