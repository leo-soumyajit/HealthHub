let stompClient = null;

// Aggressively clean JSON string: trim whitespace, remove BOM, remove trailing commas, and collapse whitespace.
function cleanJsonString(jsonStr) {
  let cleaned = jsonStr.trim().replace(/^\uFEFF/, "");
  // Remove trailing commas before } or ]
  let prev;
  do {
    prev = cleaned;
    cleaned = cleaned.replace(/,\s*([\]}])/g, "$1");
  } while (cleaned !== prev);
  // Replace newline characters with a space and collapse multiple spaces
  cleaned = cleaned.replace(/\n/g, " ").replace(/\s+/g, " ");
  return cleaned;
}

/**
 * Attempt to parse a JSON string.
 * If parsing fails, try cleaning and extracting substring from first "{" to last "}".
 */
function safeParseJSON(content) {
  try {
    return JSON.parse(content);
  } catch (e) {
    console.error("Initial JSON parse error:", e.message);
    const cleaned = cleanJsonString(content);
    try {
      return JSON.parse(cleaned);
    } catch (e2) {
      console.error("Error after cleaning:", e2.message);
      const start = cleaned.indexOf("{");
      const end = cleaned.lastIndexOf("}");
      if (start !== -1 && end !== -1 && end > start) {
        const trimmedContent = cleaned.substring(start, end + 1);
        try {
          return JSON.parse(trimmedContent);
        } catch (e3) {
          console.error("Error parsing substring JSON:", e3.message);
          console.error("Substring content:", trimmedContent);
          throw e3;
        }
      } else {
        throw e2;
      }
    }
  }
}

function connect() {
  // Connect to your WebSocket endpoint (adjust port if needed)
  const socket = new SockJS("http://localhost:7000/ws-chat");
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function(frame) {
    console.log("Connected: " + frame);
    stompClient.subscribe("/topic/public", function(messageOutput) {
      removeLoading();
      try {
        const msg = JSON.parse(messageOutput.body);
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
    let content = message.content;

    // Remove wrapping backticks if present (common with markdown responses)
    if (content.startsWith("```") && content.endsWith("```")) {
      content = content.substring(3, content.length - 3).trim();
    }
    content = content.trim();

    // Only attempt JSON parsing if the content starts with "{"
    if (content.startsWith("{")) {
      try {
        const responseJson = safeParseJSON(content);
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
        console.error("Raw AI content after cleaning:", content);
        // Fallback: display raw content if parsing fails
        msgElem.textContent = "AI (raw): " + content;
      }
    } else {
      // If content doesn't look like JSON, display it as plain text.
      msgElem.textContent = "AI: " + content;
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
  showLoading();

  // Send the message to the backend via STOMP
  stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(userMessage));
  input.value = "";
}

function handleKeyPress(event) {
  if (event.key === "Enter") {
    sendMessage();
  }
}

window.onload = function() {
  connect();
  // Add event listener to input for Enter key press
  document.getElementById("chatInput").addEventListener("keypress", handleKeyPress);
};
