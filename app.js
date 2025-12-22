const socket = io();

const loginPage = document.getElementById("loginPage");
const app = document.getElementById("app");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const authMsg = document.getElementById("authMsg");
const messages = document.getElementById("messages");
const msgInput = document.getElementById("msg");
const usersBox = document.getElementById("users");

let username = "";
let session_id = "";
let room = "global";

// ===== AUTH =====
function login() {
  socket.emit("login", {
    username: usernameInput.value,
    password: passwordInput.value
  });
}

function register() {
  socket.emit("register", {
    username: usernameInput.value,
    password: passwordInput.value
  });
}

socket.on("auth_success", data => {
  if (data.username) {
    username = data.username;
    session_id = data.session_id;
  }
  loginPage.classList.add("hidden");
  app.classList.remove("hidden");

  socket.emit("join_room", {
    session_id,
    room
  });
});

socket.on("auth_error", msg => {
  authMsg.innerText = msg;
});

// ===== CHAT =====
function send() {
  socket.emit("send_message", {
    session_id,
    room,
    message: msgInput.value
  });
  msgInput.value = "";
}

socket.on("new_message", m => {
  messages.innerHTML += `
    <div class="msg ${m.sender === username ? "me" : "other"}">
      <b>${m.sender}</b><br>${m.text}
      <span class="msg-time">${m.time}</span>
    </div>`;
  messages.scrollTop = messages.scrollHeight;
});

// ===== USERS =====
socket.on("user_list", list => {
  usersBox.innerHTML = "";
  list.forEach(u => {
    usersBox.innerHTML += `<div class="user">${u}</div>`;
  });
});

socket.on("system", msg => {
  messages.innerHTML += `<div class="system">${msg}</div>`;
});
