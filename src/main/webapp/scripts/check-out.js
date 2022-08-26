function handleCartArray(resultDataString) {
    const resultArray = resultDataString.split(",");
    console.log(resultArray);
    document.getElementById('card').innerHTML = "";
    // change it to html list
    let res = "";
    if(resultArray.length==1){
        res += "<h1>Wrong Information</h1>";
    } else{
        res += "<h1>Congratulations!</h1>";
        var len = (resultArray.length-1)/4;
        for(let i = 0; i < len; i++) {
            res += "<p>You have purchased ";
            res += "  sale ID:  " + resultArray[i*4] + "  Number  " + resultArray[i*4+1] + "  movie name:  " + resultArray[i*4+2] +  "  at time: " + resultArray[i*4+3];
            res += "</p>";

            var param = JSON.parse(
                '{'
                + '"number":"' + resultArray[i*4+1]
                + '", "movieName":"' + resultArray[i*4+2]
                + '", "saleDate":"' + resultArray[i*4+3]
                + '"}');
            var u = 'https://fir-49094.firebaseio.com/purchase/' + resultArray[i*4] + '.json';
            $.ajax({
                url: u,
                type: 'PUT',
                data: JSON.stringify(param),
                success: function(result) {
                    // alert("success");
                    // Do something with the result
                }
            });
            // $.put('https://inf551-44093.firebaseio.com/purchase.json',
            //     JSON.stringify(param),
            //     function () {
            //         alert("success");
            //     }
            // );
        }
    }
    // clear the old array and show the new array in the frontend
    $("#item_list").html("");
    $("#item_list").append(res);

}

function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.post(
        "api/check-out",
        // Serialize the cart form to the data sent by POST request
        $("#cart").serialize(),
        (resultDataString) => handleCartArray(resultDataString)
    );

}

$("#cart").submit((event) => handleCartInfo(event));