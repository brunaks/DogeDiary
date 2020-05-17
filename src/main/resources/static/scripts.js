$( document ).ready(function() {

    $.get({
        url: "http://localhost:8080/PetModel/metadata",
        dataType: 'json',
        crossDomain:true,
        success: function(result){
            console.log(result);
        }
    });
});