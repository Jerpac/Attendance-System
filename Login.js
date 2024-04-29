$(document).ready(function () {
    //code for when login button is clicked
    $("#login_button").click(function () {
        // Make AJAX request to Servlet
        $.ajax({
            url: '/myservlet', // URL of your Servlet
            type: 'GET', // Request type
            dataType: 'json', // Expected data type of response
            success: function (data) {
                // Process response data
                console.log(data);
                // Display data in the webpage as needed
            },
            error: function (xhr, status, error) {
                // Handle errors
                console.error('Error:', error);
            }
        });
    });
});



//start of original code

/*
$(document).ready(function () {

    //code for when login button is clicked
    $("#login_button").click(function () {
        // Get the values from the input fields
        var username = $("#user_input").val();
        var password = $('#pass_input').val();
        const url = 'https://jsonplaceholder.typicode.com/posts';

        //AJAX Syntax (one way)
        /*
        $.ajax({
            url: url,
            type: "GET",
            success: function (result) {

            },
            error: function (result) {

            }
        });
        // end of ajax comment

        $.get(url, function (data, status) {
            console.log(data[0]);
        });

        event.preventDefault();

    });


});
*/
