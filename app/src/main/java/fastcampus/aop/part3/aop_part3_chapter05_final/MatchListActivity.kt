package fastcampus.aop.part3.aop_part3_chapter05_final

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MatchListActivity : AppCompatActivity() {

    private lateinit var usersDb: DatabaseReference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_list)


        // RecyclerView 생성
        val recyclerView = findViewById<RecyclerView>(R.id.matchedUserRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Users DB 경로
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        getMatchUsers()

    }

    private fun getMatchUsers() {
        //내 아이디 경로 밑에 매치 컬럼 데이터 가져옴
        val matchedDb = usersDb.child(getCurrentUserID()).child("likedBy").child("match")

        matchedDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                // dataSnapshot 값이 비어 있지 않으면
                if(dataSnapshot.key?.isNotEmpty() == true) {
                    getMatchUser(dataSnapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun getMatchUser(userId: String) {
        // matchedDb 가져 와서 어뎁터에 전달
        val matchedDb = usersDb.child(userId)
        matchedDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId, snapshot.child("name").value.toString())) //키값 - 아이디 , 사용자 이름값 가져옴
                adapter.submitList(cardItems) // 리스트 어뎁터에 적용
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    // 사용자 id 가져 오기
    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return auth.currentUser.uid
    }
}