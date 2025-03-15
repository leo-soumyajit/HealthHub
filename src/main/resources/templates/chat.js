let stompClient = null;

/**
 * Cleans up JSON string by trimming, removing BOM characters, and fixing formatting.
 */
function cleanJsonString(jsonStr) {
    let cleaned = jsonStr.trim().replace(/^\uFEFF/, ""); // Remove BOM
    let prev;
    do {
        prev = cleaned;
        cleaned = cleaned.replace(/,\s*([\]}])/g, "$1"); // Remove trailing commas
    } while (cleaned !== prev);
    return cleaned.replace(/\n/g, " ").replace(/\s+/g, " "); // Collapse spaces
}

/**
 * Safely parses JSON content while fixing double-encoded JSON if needed.
 */
function safeParseJSON(content) {
    try {
        return JSON.parse(content); // Try parsing normally
    } catch (e) {
        console.warn("Initial JSON parse error:", e.message);
        const cleaned = cleanJsonString(content);
        try {
            return JSON.parse(cleaned);
        } catch (e2) {
            console.warn("Error after cleaning:", e2.message);
            try {
                return JSON.parse(JSON.parse(content)); // Fix double-encoded JSON
            } catch (e3) {
                console.warn("Double-encoded JSON fix failed:", e3.message);
                return content; // Return raw text if still invalid
            }
        }
    }
}

/**
 * Connects to WebSocket backend and subscribes to AI messages.
 */
function connect() {
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

/**
 * Displays AI and user messages in the chat box.
 */
function displayMessage(message) {
    const chatBox = document.getElementById("chat-box");
    const msgElem = document.createElement("div");
    msgElem.classList.add("message");

    if (message.sender === "AI") {
        msgElem.classList.add("ai");
        let content = message.content.trim();

        // Try to parse AI content as JSON
        content = safeParseJSON(content);

        // If content is structured AI JSON, format it nicely
        if (typeof content === "object" && content.title && Array.isArray(content.steps)) {
            let formattedHTML = `<h3>🩺 ${content.title}</h3><ol>`;
            content.steps.forEach(step => {
                formattedHTML += `<li><strong>Step ${step.step}:</strong> ${step.instruction}<br><em>${step.note}</em></li>`;
            });
            formattedHTML += "</ol>";

            if (content.note) {
                formattedHTML += `<p>⚠️ <em>${content.note}</em></p>`;
            }

            msgElem.innerHTML = formattedHTML;
        } else {
            // Ensure JSON is stringified properly
            msgElem.innerHTML = `<pre>${JSON.stringify(content, null, 2)}</pre>`;
        }
    } else {
        msgElem.classList.add("user");
        msgElem.textContent = "User: " + message.content;
    }

    chatBox.appendChild(msgElem);
    chatBox.scrollTop = chatBox.scrollHeight;
}

/**
 * Shows a loading message while AI is processing.
 */
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

/**
 * Removes the loading message after AI responds.
 */
function removeLoading() {
    const loadingElem = document.getElementById("loadingMsg");
    if (loadingElem) {
        loadingElem.remove();
    }
}

/**
 * Sends a user message to the backend via WebSocket.
 */
function sendMessage() {
    const input = document.getElementById("chatInput");
    const text = input.value.trim();
    if (!text || !stompClient) return;

    const userMessage = {
        sender: "User",
        content: text,
        type: "CHAT"
    };

    displayMessage(userMessage);
    showLoading();

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(userMessage));
    input.value = "";
}

/**
 * Handles "Enter" key press to send messages.
 */
function handleKeyPress(event) {
    if (event.key === "Enter") {
        sendMessage();
    }
}

// Start WebSocket connection when the page loads
window.onload = function() {
    connect();
    document.getElementById("chatInput").addEventListener("keypress", handleKeyPress);
};
