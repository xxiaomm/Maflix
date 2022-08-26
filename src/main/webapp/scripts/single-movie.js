/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function test(movieinfo) {
    var i = parseInt(movieinfo);
    jQuery.ajax({
        //dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/index?item=" + movieID[i], // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleCartArray(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function handleCartArray(resultDataString) {
    const resultArray = resultDataString.split(",");
    console.log(resultArray);

    // change it to html list
    let res = "<ul>";
    for(let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    $("#item_list").html("");
    $("#item_list").append(res);
}

var movieID = [];
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        movieID.push(resultData[i]["movie_title"]);
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<th>";
        rowHTML += '<input type="button" value="Purchase" onclick="test('+ i +')" />';
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        let movieGenre = resultData[i]["movie_genres"].split(",");
        rowHTML += "<th>";
        for(var j=0;j<movieGenre.length;j++) {
            rowHTML +=
                '<a href="movie-list.html?genre=' + movieGenre[j] + '&title=&year=&director=&name=&offset=0&limit=20&sortby=rd">' + movieGenre[j] + '</a>';
            if (j!=movieGenre.length-1){
                rowHTML += ", ";
            }
        }
        rowHTML += "</th>";

        rowHTML += "<th>";
        StarName = resultData[i]["movie_stars"].split(",");
        StarId = resultData[i]["movie_starsId"].split(",");
        //Stars hyperlink
        for(var j=0;j<StarId.length;j++) {
            rowHTML +=
                '<a href="single-star.html?id=' + StarId[j] + '">'
                + StarName[j] + '</a>';
            if (j!=StarId.length-1){
                rowHTML += ",  ";
            }
        }
        rowHTML += "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});