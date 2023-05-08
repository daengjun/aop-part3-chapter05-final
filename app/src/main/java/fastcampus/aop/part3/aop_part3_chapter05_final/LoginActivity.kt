package fastcampus.aop.part3.aop_part3_chapter05_final

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class LoginActivity : AppCompatActivity() {

    // Ctrl + Alt + M : 함수 추출하기!!

    // FirebaseAuth 인스턴스 선언
    private lateinit var auth: FirebaseAuth

    // 페이스북으로 로그인하고 결과를 받아오는것이기때문에 콜백 OnActivityResult 처리
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 인스턴스 초기화
        auth = Firebase.auth

        // 이메일 입력
        val emailEditText = findViewById<EditText>(R.id.emailEditText)

        // 패스워드 입력
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        // 로그인 버튼
        val loginButton = findViewById<Button>(R.id.loginButton)

        // 회원가입 버튼
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // 페이스북 로그인 버튼
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)


        // 로그인 버튼 기능
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // signInWithEmailAndPassword 메서드를 사용하여 이메일 주소와 비밀번호를 가져와 유효성을 검사한 후 사용자를 로그인

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        successLogin()
                    } else {
                        Toast.makeText(
                            this,
                            "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // 아이디 비밀번호를 입력하지 않으면 로그인 회원가입 버튼 비활성화

        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // 회원 가입 기능

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // createUserWithEmailAndPassword 메서드를 사용하여 이메일 주소와 비밀번호를 가져와 유효성을 검사

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "회원가입을 성공했습니다. 로그인 버튼을 눌러 로그인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // 페이스북 디벨로퍼가서 문서보고 신청해야됨, 파이어베이스에 아이디 , 비번 가져와서 입력해줘야됨

        // 콜백 매니저 생성
        callbackManager = CallbackManager.Factory.create()

        // 이메일과 사용자 프로필정보 두가지 요청
        facebookLoginButton.setPermissions("email", "public_profile")

        // 콜백 설정
        facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("Facebook", "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("Facebook", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    // 페이스북 로그인 에러
                    Log.d("Facebook", "facebook:onError", error)
                    Toast.makeText(
                        this@LoginActivity,
                        "페이스북 로그인에 실패했습니다. 다시 시도해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 페이스북 콜백 처리
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        // 페이스북에서 토큰을 가져와서 파이어베이스에 전달함
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    successLogin()
                } else {
                    Toast.makeText(this, "페이스북 로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun successLogin() {

        // 현재 사용자 정보가 없으면
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }


        val userId: String = auth.currentUser.uid
        // 파이어베이스 데이터베이스 경로 Users 아래에 저장
        val currentUserDb = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String, Any>()

        // 키값을 userId userId값 맵에 할당
        user["userId"] = userId

        // 파이어베이스 해당 경로의 값 업데이트
        currentUserDb.updateChildren(user)

        // 종료
        finish()
    }
}