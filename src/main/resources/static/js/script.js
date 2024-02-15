const modal = document.getElementById("myModal");
const mainContent = document.getElementById("mainContent");
const submitBtn = document.getElementById("submitBtn");
const errorText = document.getElementById("errorText");
const passwordInput = document.getElementById("password");

modal.style.display = "block";


mainContent.classList.add("blur");

modal.addEventListener("click", function(event) {
    if (event.target === modal) {
        return;
    }
});

submitBtn.onclick = async function() {
    const password = passwordInput.value;

    try {
        const response = await fetch(`/converter/verifyPassword`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: password
        });

        const data = await response.json();
        console.log(data);

        if (data.valid) {
            modal.style.display = "none";
            mainContent.style.display = "block";
            mainContent.classList.remove("blur");
        } else {
            document.getElementById("password").value = "";
            errorText.textContent = "Incorrect password. Please try again.";
        }
    } catch (error) {
        console.error('Error:', error);
    }
}
