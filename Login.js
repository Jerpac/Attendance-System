$(document).ready(function () {
    valid_users = {
        "users": [
            { "username": "tst100000", "password": "testUser10" },
            { "username": "utd100000", "password": "utdUser5" },
            { "username": "hey100000", "password": "Hello2" }
        ]
    }
    $("#login_button").click(function () {
        //var url = "Attendance.html";
        //$(location).attr('href', url);
        var username = $("#user_input").val();
        var password = $('#pass_input').val();
        console.log(username);
        console.log(password);

        //if () {
        //console.log('yah')
        //} else {
        //console.log('no')
        //}

        //start of AJAX demo

        univ_fact_url = 'http://universities.hipolabs.com/search?country=United+States'
        $.ajax({
            url: univ_fact_url,
            type: 'GET',
            data: [],
            success: function (data) {
                console.log(data[2])
            },
            error: function (result) {
                console.log('not worked');
            }
        });


        event.preventDefault();
    });
});