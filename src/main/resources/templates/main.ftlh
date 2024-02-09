<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" href="../static/css/main.css">

    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>


<div class="container">

    <form action="/converter/convert" method="get">
        <div class="inputForm">
           <h2> <label for="userInput">Input Url:</label><br></h2>
            <input class="reqInput" type="text" id="requestUrl" name="requestUrl"><br>
            <input class="submit_btn" type="submit" value="Convert">
        </div>


    </form>

    <div class="result_items">

        <div class="result_item">
            <#if productFilePath?? >
                <h2 style="font-family: sans-serif;font-size: large"> Download <a href="../static/products.csv" download="product.csv">product.csv</a> file</h2>
                <h6> Was created on ${createDate}</h6>
            </#if>
        </div>

        <div class="result_item">
            <#if inventoryFilePath?? >
                <h2 style="font-family: sans-serif;font-size: large"> Download <a href="../static/inventory.csv" download="inventory.csv">inventory.csv</a> file</h2>
                <h6> Was created on ${createDate}</h6>
            </#if>
        </div>
    </div>


</div>


</body>
</html>