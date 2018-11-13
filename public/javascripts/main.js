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
 
 function checkin(uuid,callName)
 {
    if(!unDefined(uuid))
    {
    $(document).ready(function() {

         $.getJSON('checkin/'+uuid, function(sm) {
          if(sm.message=="ok")
          {
            showData(uuid,callName)
          }
          else
          {
          $('#messages').html('<p>c : ' + sm.message + '</p>');
            setTimeout(checkin,1000)
            checkin(uuid);
          }

         });
     });
     }
 }

 function showData(uuid,callName)
 {
    $(document).ready(function() {

          $.getJSON('data/'+uuid, function(jsonData) {
            $('#messages').html('<p>Keyspaces</p>');
            for (var i = 0; i < jsonData.records.length; i++) {
                var ks = jsonData.records[i];
                $('#messages').append('<p>c  : ' + ks.keyspaceName+ '</p>');
            }
          });
      });
 }


 function unDefined(myVar)
 {
    return typeof myVar == 'undefined'
 }


 function keyspaces()
 {
  $(document).ready(function() {
    $.getJSON('keyspaces', function(sm) {

       checkin(sm.uuid,'keyspaces')


    });
    });
 }