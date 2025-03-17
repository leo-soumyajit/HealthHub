document.addEventListener("DOMContentLoaded", function() {
    const imageInput = document.getElementById("imageInput");
    if (imageInput) {
        imageInput.addEventListener("change", uploadImage);
    } else {
        console.error("Element with id 'imageInput' not found.");
    }
});

/**
 * Uploads an image to the backend for AI analysis.
 */
function uploadImage(event) {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    const chatBox = document.getElementById("chat-box");
    const loadingMsg = document.createElement("div");
    loadingMsg.classList.add("message", "loading");
    loadingMsg.textContent = "AI is analyzing the image...";
    chatBox.appendChild(loadingMsg);

    fetch("http://localhost:7000/api/chat/image", {
        method: "POST",
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        loadingMsg.remove();
        displayImageMessage(file);
        displayMessage({ sender: "AI", content: JSON.stringify(data, null, 2) });
    })
    .catch(error => {
        loadingMsg.remove();
        console.error("Error sending image:", error);
        alert("Failed to analyze image.");
    });
}

/**
 * Displays the uploaded image in the chat box.
 */
function displayImageMessage(file) {
    const chatBox = document.getElementById("chat-box");
    const reader = new FileReader();

    reader.onload = function(event) {
        const imgElem = document.createElement("img");
        imgElem.src = event.target.result;
        imgElem.classList.add("message", "user-image");
        chatBox.appendChild(imgElem);
        chatBox.scrollTop = chatBox.scrollHeight;
    };

    reader.readAsDataURL(file);
}

/**
 * Displays a message in the chat box.
 * (This function should be defined in your shared JS if not already present.)
 */
function displayMessage(message) {
    const chatBox = document.getElementById("chat-box");
    const msgElem = document.createElement("div");
    msgElem.classList.add("message");
    if (message.sender === "AI") {
        msgElem.classList.add("ai");
        msgElem.innerHTML = `<pre>${JSON.stringify(message.content, null, 2)}</pre>`;
    } else {
        msgElem.classList.add("user");
        msgElem.textContent = "User: " + message.content;
    }
    chatBox.appendChild(msgElem);
    chatBox.scrollTop = chatBox.scrollHeight;
}
