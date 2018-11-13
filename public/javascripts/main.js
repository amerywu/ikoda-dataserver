function jsStatus()
{
    alert("JS script found")
}

function loadDoc() {
   var xhttp = new XMLHttpRequest();
   xhttp.onreadystatechange = function() {
   if (this.readyState == 4 && this.status == 200) {
       var myObj=JSON.parse(this.responseText)
       document.getElementById("messages").innerHTML = myObj.email+"<BR/>"+myObj.firstName+"<BR/>";
       }
   };
   xhttp.open("GET", "http://localhost:9000/jsontest", true);
   xhttp.send();
}

function jqLoad()
{
 $(document).ready(function() {


               $.getJSON('jsontest', function(jd) {
                  $('#messages').html('<p>JQ : ' + jd.email + '</p>');
                  $('#messages').append('<p>JQ  : ' + jd.firstName+ '</p>');
                  $('#messages').append('<p>JQ : ' + jd.lastName+ '</p>');
               });


         });
}