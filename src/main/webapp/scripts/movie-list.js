/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
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

$(function () {
    $('#movie-form').on('submit',function (e) {

        $.ajax({
            type: 'get',
            url: 'api/index',
            data: $('#movie-form').serialize(),
            success: (resultData) => handleCartArray(resultData)
        });
        e.preventDefault();
    });
});

function handleCartArray(resultDataString) {
    const resultArray = resultDataString.split(",");
    console.log(resultArray);

    // change it to html list
    /*let res = "<ul>";
    for(let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    $("#item_list").html("");
    $("#item_list").append(res);*/
    alert("Success to add into cart");
}

var movieID = [];

function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#Movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(40, resultData.length); i++) {
        movieID.push(resultData[i]["movie_title"]);
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        //let movieinfo = resultData[i]["movie_id"];
        //add to cart
        rowHTML += "<th>";
        rowHTML += '<input type="button" value="Purchase" onclick="test('+ i +')" />';
        rowHTML += "</th>";

        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>'+
            "</th>";
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

        StarName = resultData[i]["movie_stars"].split(",");
        StarId = resultData[i]["movie_starsId"].split(",");
        Star_first = StarId[0];

        rowHTML += "<th>";
        //Stars hyperlink
        for(var j=0;j<StarId.length;j++) {
            if(StarId[j]==Star_first & j!=0) break;
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
        starTableBodyElement.append(rowHTML);
    }
}

if (getParameterByName('sortby')!= ''){
    console.log("sortby!= null");
    let genre = getParameterByName('genre');
    let title = getParameterByName('title');
    let year = getParameterByName('year');
    let director = getParameterByName('director');
    let name = getParameterByName('name');
    let offset = getParameterByName('offset');
    let limit = getParameterByName('limit');
    let sortinfo = getParameterByName('sortby');
    if (getParameterByName('sortby')=='rd'){
        console.log("sortrd");
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/search?genre=" + genre + "&title=" + title + "&year=" + year + "&director=" + director + "&name=" + name + "&offset=" + offset + "&limit=" + limit + "&sortby=" + sortinfo, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    } else if (getParameterByName('sortby')== 'ra'){
        console.log("sortra");
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/sortra?genre=" + genre + "&title=" + title + "&year=" + year + "&director=" + director + "&name=" + name + "&offset=" + offset + "&limit=" + limit + "&sortby=" + sortinfo, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    } else if (getParameterByName('sortby')== 'td'){
        console.log("sorttd");
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/sorttd?genre=" + genre + "&title=" + title + "&year=" + year + "&director=" + director + "&name=" + name + "&offset=" + offset + "&limit=" + limit + "&sortby=" + sortinfo, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    } else if (getParameterByName('sortby')== 'ta'){
        console.log("sortta");
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/sortta?genre=" + genre + "&title=" + title + "&year=" + year + "&director=" + director + "&name=" + name + "&offset=" + offset + "&limit=" + limit + "&sortby=" + sortinfo, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
}

function sort(sortinfo) {
    let genre = getParameterByName('genre');
    let title = getParameterByName('title');
    let year = getParameterByName('year');
    let director = getParameterByName('director');
    let name = getParameterByName('name');
    let offset1 = getParameterByName('offset');
    let limit = getParameterByName('limit');
    var offset = parseInt(offset1);
    offset = 0;

    window.location.href = "movie-list.html?genre=" + genre + "&title=" + title + "&year=" + year + "&director=" + director + "&name=" + name + "&offset=" + offset + "&limit=" + limit + "&sortby=" + sortinfo;
}

function page(pageinfo) {
    let genre = getParameterByName('genre');
    let title = getParameterByName('title');
    let year = getParameterByName('year');
    let director = getParameterByName('director');
    let name = getParameterByName('name');
    let offset1 = getParameterByName('offset');
    let limit1 = getParameterByName('limit');
    let sortinfo = getParameterByName('sortby');
    var offset = parseInt(offset1);
    var limit = parseInt(limit1);

    if(pageinfo=='next'){
        console.log("offset=+limit");
        offset=offset + limit;
    } else if(pageinfo=='prev'){
        if(offset-limit>(-1)){
            offset=offset - limit;
        }else {
            alert("This is the first page!");
        }
    }
    window.location.href = "movie-list.html?genre=" + genre + "&title=" + title + "&year=" + year + "&director=" + director + "&name=" + name + "&offset=" + offset + "&limit=" + limit + "&sortby=" + sortinfo;
}

function changeLimit(limit1){
    let genre = getParameterByName('genre');
    let title = getParameterByName('title');
    let year = getParameterByName('year');
    let director = getParameterByName('director');
    let name = getParameterByName('name');
    let offset = getParameterByName('offset');
    let sortinfo = getParameterByName('sortby');
    var limit = parseInt(limit1);
    offset = 0;
    window.location.href = "movie-list.html?genre=" + genre + "&title=" + title + "&year=" + year + "&director=" + director + "&name=" + name + "&offset=" + offset + "&limit=" + limit + "&sortby=" + sortinfo;
}