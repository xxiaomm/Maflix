function refresh(resultDataString) {
    window.location.href = "shopping-cart.html";
}
function getValue(i){
    var input = document.getElementById(movieTitle[i]);
    console.log(movieTitle[i]+input.value);
//         jQuery.ajax({
//        	    method: "GET", // Setting request method
//        	    url: "/api/shopping-cart?quan=" + input.value + "&title=" + movieTitle[i], // Setting request url, which is mapped by StarsServlet in Stars.java
//        	    success: (resultData) => handleSessionData(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
//        });
    $.ajax({
        type: "GET",
        url: "api/shopping-cart?quan=" + input.value + "&title=" + movieTitle[i],
        success: (resultDataString) => refresh(resultDataString)
    });
}
function getValue0(i){
    $.ajax({
        type: "GET",
        url: "api/shopping-cart?quan=" + 0 + "&title=" + movieTitle[i],
        success: (resultDataString) => refresh(resultDataString)
    });
}
var movieTitle = [];
function handleSessionData(resultDataString) {
    const resultArray = resultDataString.split(",");

    let saleTableBodyElement = jQuery("#Sale_table_body");
    var len = resultArray.length/2;
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < len; i++) {
        // Concatenate the html tags with resultData jsonObject
        movieTitle.push(resultArray[i]);
        let rowHTML = "";
        rowHTML += "<tr>";
        //add to cart
        rowHTML += '<th>' + '<input type="button" value="Delete" onclick="getValue0('+i+')"/>' + '</th>';
        rowHTML += '<th>' + '<input type="text" id="'+resultArray[i]+'"><input type="button" value="Set Quantity" onclick="getValue('+i+')"/>' + '</th>';
        rowHTML += "<th>" + resultArray[i] + "</th>";
        rowHTML += "<th>" + resultArray[i+len] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        saleTableBodyElement.append(rowHTML);
    }
}

$.ajax({
    type: "POST",
    url: "api/shopping-cart",
    success: (resultDataString) => handleSessionData(resultDataString)
});