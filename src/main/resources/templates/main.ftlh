<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" href="/css/main.css">

    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>


<div id="passwordForm">
    <label for="password">Please enter the password:</label><br>
    <input type="password" id="password" name="password">
    <button onclick="verifyPassword()">Submit</button>
</div>


<div class="container" style="display: none">

    <table>
        <tr>
            <th><p>Revit</p></th>
            <th><p> Download <a href="/converter/downloadProduct">product.csv</a></p></th>
            <th><p> Download <a href="/converter/downloadInventory">inventory.csv</a></p></th>
        </tr>
        <tr hidden="hidden">
            <td>LinkInt</td>
            <td></td>
            <td></td>
        </tr>
        <tr hidden="hidden">
            <td>Whitesmoto</td>
            <td></td>
            <td></td>
        </tr>
        <tr hidden="hidden">
            <td>Motonational</td>
            <td></td>
            <td></td>
        </tr>
    </table>

</div>


<script>
    async function verifyPassword() {
        const password = document.getElementById("password").value;

        try {
            const response = await fetch('/converter/verifyPassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({password: password})
            });

            const data = await response.json();
            console.log(data);
            if (data.valid) {
                // Пароль верный, показать содержимое страницы
                document.getElementById("passwordForm").style.display = "none";
                document.getElementById("container").style.display = "flex";
            } else {
                // Пароль неверный, вывести предупреждение
                alert("Incorrect password. Please try again.");
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }
</script>
</body>
</html>