<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" href="../static/css/main.css">

    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>


<div class="container">

    <form action="/converter/main" method="get">
        <div class="inputForm">
            <h2><label for="userInput">Input Url:</label><br></h2>
            <label> ${request} </label><br>
            <input class="submit_btn" type="submit" value="Back">
        </div>
    </form>

    <form action="/converter/downloadInventory" method="get">
        <div class="inputForm">
            <h2><label for="userInput"> inventory.csv </label><br></h2>
            <input class="submit_btn" type="submit" value="Download">
        </div>
    </form>

    <form action="/converter/downloadProduct" method="get">
        <div class="inputForm">
            <h2><label for="userInput"> product.csv </label><br></h2>
            <input class="submit_btn" type="submit" value="Download">
        </div>
    </form>
<#--    -->
<#--    <div class="result_items">-->

<#--        <div class="result_item">-->
<#--            <#if productFilePath?? >-->
<#--                <h2 style="font-family: sans-serif;font-size: large"> Download <a href="../static/products.csv"-->
<#--                                                                                  download="product.csv">product.csv</a>-->
<#--                    file</h2>-->
<#--                <h6> Was created on ${createDate}</h6>-->
<#--            </#if>-->
<#--        </div>-->

<#--        <div class="result_item">-->
<#--            <#if inventoryFilePath?? >-->
<#--                <h2 style="font-family: sans-serif;font-size: large"> Download <a href="../static/inventory.csv"-->
<#--                                                                                  download="inventory.csv">inventory.csv</a>-->
<#--                    file</h2>-->
<#--                <h6> Was created on ${createDate}</h6>-->
<#--            </#if>-->
<#--        </div>-->
<#--    </div>-->


</div>


</body>
</html>