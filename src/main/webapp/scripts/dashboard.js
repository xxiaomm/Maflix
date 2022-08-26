/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/dashboard", // Setting request url, which is mapped by DashBoardServlet.java
    success: (resultData) => handleDBInfoResult(resultData) // Setting callback function to handle data returned successfully by the DashBoardServlet
});

function handleDBInfoResult(resultData) {
    console.log("handleDBInfoResult: populating metadata table from resultData");
    // Populate the star table
    // Find the empty table body by id "metadata_table_body"
    let metadataTableBodyElement = jQuery("#metadata_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["table_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["column_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["column_type"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        metadataTableBodyElement.append(rowHTML);
    }
}

function myCheck() {
    if (insertStar_form.name.value==""){
        alert("please input the name of star");
        return false;
    }else{
        return true;
    }
}


function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("index.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

function handleInsertResult(resultData) {
    const resultArray = resultData.split(',');
    console.log("handleInsertStarResult: populating insert info from resultData");
//    let res = "<ul>";
//    for(let i = 0; i < resultArray.length; i++) {
//        // each item will be in a bullet point
//        res += "<li>" + resultArray[i] + "</li>";
//    }
//    res += "</ul>";
//
//    // clear the old array and show the new array in the frontend
//    $("#insert_message").html("");
//    $("#insert_message").append(res);
    alert(resultData);
}


/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitInsertStarForm(formSubmitEvent) {
    console.log("submit Insert Star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.post(
        "api/dashboard",
        // Serialize the login form to the data sent by POST request
        $("#insertStar_form").serialize(),
        (resultDataString) => handleInsertResult(resultDataString)
    );
}

// Bind the submit action of the form to a handler function
$("#insertStar_form").submit((event) => submitInsertStarForm(event));




/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitInsertMovieForm(formSubmitEvent) {
    console.log("submit Insert Movie form");
    formSubmitEvent.preventDefault();

    $.post(
        "api/addmovie",
        // Serialize the login form to the data sent by POST request
        $("#insertMovie_form").serialize(),
        (resultDataString) => handleInsertResult(resultDataString)
    );
}

// Bind the submit action of the form to a handler function
$("#insertMovie_form").submit((event) => submitInsertMovieForm(event));



