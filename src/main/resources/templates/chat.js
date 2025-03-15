let stompClient = null;

function connect() {
  const socket = new SockJS("http://localhost:7000/ws-chat"); // adjust port if needed
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function(frame) {
    console.log("Connected: " + frame);
    stompClient.subscribe("/topic/public", function(messageOutput) {
      removeLoading();
      try {
        // Parse the incoming message (it should be a JSON string)
        let msg = JSON.parse(messageOutput.body);
        displayMessage(msg);
      } catch (e) {
        console.error("Error parsing message from server:", e);
      }
    });
  }, function(error) {
    console.error("STOMP error: " + error);
  });
}

function displayMessage(message) {
  const chatBox = document.getElementById("chat-box");
  const msgElem = document.createElement("div");
  msgElem.classList.add("message");

  if (message.sender === "AI") {
    msgElem.classList.add("ai");

    // Remove wrapping backticks if present (e.g., ```...```)
    let content = message.content;
    if (content.startsWith("```") && content.endsWith("```")) {
      content = content.substring(3, content.length - 3).trim();
    }

    try {
      // Attempt to parse the content as JSON
      const responseJson = JSON.parse(content);
      // Build formatted HTML
      let formattedHTML = `<h3>${responseJson.title}</h3>`;
      if (Array.isArray(responseJson.steps)) {
        formattedHTML += "<ol>";
        responseJson.steps.forEach(step => {
          formattedHTML += `<li><strong>Step ${step.step}:</strong> ${step.instruction}<br><em>${step.note}</em></li>`;
        });
        formattedHTML += "</ol>";
      }
      if (responseJson.note) {
        formattedHTML += `<p><em>${responseJson.note}</em></p>`;
      }
      msgElem.innerHTML = "AI: " + formattedHTML;
    } catch (e) {
      console.error("Error parsing AI JSON:", e);
      // Fallback to displaying the raw text if parsing fails
      msgElem.textContent = "AI: " + message.content;
    }
  } else {
    msgElem.classList.add("user");
    msgElem.textContent = "User: " + message.content;
  }

  chatBox.appendChild(msgElem);
  chatBox.scrollTop = chatBox.scrollHeight;
}

function showLoading() {
  const chatBox = document.getElementById("chat-box");
  if (!document.getElementById("loadingMsg")) {
    const loadingElem = document.createElement("div");
    loadingElem.id = "loadingMsg";
    loadingElem.classList.add("message", "loading");
    loadingElem.textContent = "AI is generating a response...";
    chatBox.appendChild(loadingElem);
    chatBox.scrollTop = chatBox.scrollHeight;
  }
}

function removeLoading() {
  const loadingElem = document.getElementById("loadingMsg");
  if (loadingElem) {
    loadingElem.remove();
  }
}

function sendMessage() {
  const input = document.getElementById("chatInput");
  const text = input.value.trim();
  if (!text || !stompClient) return;

  // Create a user message object
  const userMessage = {
    sender: "User",
    content: text,
    type: "CHAT"
  };

  // Display user's message immediately
  displayMessage(userMessage);
  // Show loading indicator while waiting for AI response
  showLoading();

  // Send message to backend via STOMP
  stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(userMessage));
  input.value = "";
}

window.onload = function() {
  connect();
};
