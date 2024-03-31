$(document).ready(function () {

    console.log('Hello World')
    user_from_db =
        { "username": "1234567890", "password": "WHOOSH" }
    var keys = Object.keys(user_from_db);
    var values = Object.values(user_from_db);


    $("#login_button").click(function () {
        var url = "Attendance.html";
        //$(location).attr('href', url);
        var username = $("#user_input").val();
        var password = $('#pass_input').val();

        if (username == values[0] && password == values[1]) {
            $(location).attr('href', url);
        } else {
            alert('Wrong User Credentials!');
        }

        //if () {
        //console.log('yah')
        //} else {
        //console.log('no')
        //}

        //start of AJAX demo
        /*
       univ_fact_url = 'http://universities.hipolabs.com/search?country=United+States'
       $.ajax({
           url: univ_fact_url,
           type: 'GET',
           data: [],
           success: function (data) {
               console.log(data[0])
           },
           error: function (result) {
               console.log('not worked');
           }
       });
       */

        event.preventDefault();
    });
});