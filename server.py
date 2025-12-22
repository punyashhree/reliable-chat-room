from flask import Flask, render_template, request
from flask_socketio import SocketIO, emit, join_room, leave_room
from flask_cors import CORS
from collections import deque
from datetime import datetime
import sqlite3, hashlib, secrets

app = Flask(__name__)
app.config["SECRET_KEY"] = "chronochat"
CORS(app)
socketio = SocketIO(app, cors_allowed_origins="*", async_mode="threading")

# ================= DATABASE =================
db = sqlite3.connect("chat.db", check_same_thread=False)
cur = db.cursor()

cur.execute("CREATE TABLE IF NOT EXISTS users(username TEXT PRIMARY KEY, password TEXT)")
cur.execute("CREATE TABLE IF NOT EXISTS messages(room TEXT, sender TEXT, text TEXT, time TEXT)")
db.commit()

# ================= DATA STRUCTURES =================
class ChatRoom:
    def __init__(self):
        self.messages = deque()
        self.users = set()

rooms = {}
sessions = {}
online_users = set()

# ================= HELPERS =================
def hash_pw(p): return hashlib.sha256(p.encode()).hexdigest()

def auth(data):
    sid = data.get("session_id")
    return sessions.get(sid)

# ================= ROUTES =================
@app.route("/")
def index():
    return render_template("index.html")

# ================= AUTH =================
@socketio.on("register")
def register(data):
    try:
        cur.execute("INSERT INTO users VALUES (?,?)",
                    (data["username"], hash_pw(data["password"])))
        db.commit()
        emit("auth_success")
    except:
        emit("auth_error", "User exists")

@socketio.on("login")
def login(data):
    cur.execute("SELECT password FROM users WHERE username=?",
                (data["username"],))
    row = cur.fetchone()
    if not row or row[0] != hash_pw(data["password"]):
        emit("auth_error", "Invalid credentials")
        return

    sid = secrets.token_hex(8)
    sessions[sid] = data["username"]
    online_users.add(data["username"])

    emit("auth_success", {
        "username": data["username"],
        "session_id": sid
    })

# ================= ROOMS =================
@socketio.on("join_room")
def join(data):
    user = auth(data)
    if not user:
        return

    room = data["room"]
    if room not in rooms:
        rooms[room] = ChatRoom()

    join_room(room)
    rooms[room].users.add(user)

    emit("system", f"{user} joined", room=room)
    emit("user_list", list(rooms[room].users), room=room)

    cur.execute("SELECT sender,text,time FROM messages WHERE room=?", (room,))
    for s,t,ti in cur.fetchall():
        emit("new_message", {
            "sender": s, "text": t, "time": ti
        })

# ================= CHAT =================
@socketio.on("send_message")
def send_message(data):
    user = auth(data)
    if not user:
        return

    room = data["room"]
    text = data["message"]
    time = datetime.now().strftime("%H:%M")

    rooms[room].messages.append((user, text, time))
    cur.execute("INSERT INTO messages VALUES (?,?,?,?)",
                (room, user, text, time))
    db.commit()

    emit("new_message", {
        "sender": user,
        "text": text,
        "time": time
    }, room=room)

# ================= DISCONNECT =================
@socketio.on("disconnect")
def disc():
    for sid,u in list(sessions.items()):
        if request.sid == sid:
            online_users.discard(u)
            sessions.pop(sid)

# ================= RUN =================
if __name__ == "__main__":
    socketio.run(app, host="0.0.0.0", port=5000, debug=True)
